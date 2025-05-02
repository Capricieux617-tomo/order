package com.example.nagoyameshi.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.StripeService;
import com.example.nagoyameshi.service.UserService;
import com.stripe.exception.StripeException;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final StripeService stripeService;

    public UserController(UserRepository userRepository, UserService userService, StripeService stripeService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.stripeService = stripeService;
    }

    // 会員情報ページ（indexページ）
    @GetMapping
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
        model.addAttribute("user", user);
        return "user/index";
    }

    // 会員情報編集ページ
    @GetMapping("/edit")
    public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model, HttpServletRequest httpServletRequest) throws StripeException {
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());

        if (user == null) {
            model.addAttribute("errorMessage", "ユーザーが見つかりませんでした。");
            return "error/userNotFound";
        }

        UserEditForm userEditForm = new UserEditForm();
        userEditForm.setId(user.getId());
        userEditForm.setName(user.getName());
        userEditForm.setFurigana(user.getFurigana());
        userEditForm.setPhoneNumber(user.getPhoneNumber());
        userEditForm.setEmail(user.getEmail());
        userEditForm.setIsPremium(user.getIsPremium());

        // 無料会員の場合は、プレミアム会員登録用のセッションIDを生成
        if (!user.getIsPremium()) {
            String successUrl = httpServletRequest.getRequestURL().toString().replace("/edit", "/premium/success?userId=" + user.getId());
            String cancelUrl = httpServletRequest.getRequestURL().toString().replace("/edit", "/premium/cancel");

            try {
                String sessionId = stripeService.createCheckoutSession(userEditForm, successUrl, cancelUrl);
                model.addAttribute("sessionId", sessionId);
            } catch (StripeException e) {
                model.addAttribute("errorMessage", "Stripeとの通信に失敗しました。後ほど再度お試しください。");
                return "user/edit";
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("userEditForm", userEditForm);
        return "user/edit";
    }

    @PostMapping("/update")
    public Object update(@ModelAttribute @Validated UserEditForm userEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Model model) throws StripeException {
       
        System.out.println("=== フォーム送信 ===");
        System.out.println("UserEditForm.isPremium: " + userEditForm.getIsPremium());
        System.out.println("UserEditForm.email: " + userEditForm.getEmail());

    	// メールアドレスが変更されている場合の重複チェック
        if (userService.isEmailChanged(userEditForm) && userService.isEmailRegistered(userEditForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);
        }

        // 入力にエラーがあればフォームを再表示
        if (bindingResult.hasErrors()) {
            return "user/edit";
        }

        // ユーザー情報の更新
        User user = userService.getUserById(userEditForm.getId());
        
        System.out.println("=== DBから取得したユーザー情報 ===");
        System.out.println("User.id: " + user.getId());
        System.out.println("User.isPremium: " + user.getIsPremium());


        // プレミアムプランへのアップグレード処理
        if (userEditForm.getIsPremium() && !user.getIsPremium()) {
            String successUrl = httpServletRequest.getRequestURL().toString().replace("/update", "/premium/success?userId=" + user.getId());
            String cancelUrl = httpServletRequest.getRequestURL().toString().replace("/update", "/premium/cancel");
            
            // 明示的にプレミアム会員の価格IDを指定
            String sessionId = stripeService.createCheckoutSession(userEditForm, successUrl, cancelUrl, "premium");

            // sessionId をモデルに追加し、再度フォームを表示
            model.addAttribute("sessionId", sessionId);
            return "user/edit"; // 再表示
        }
 

        // ユーザー情報を更新
        userService.update(userEditForm);
        redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");

        return "redirect:/user";
    }

    // プレミアムプラン成功ページ
    @GetMapping("/premium/success")
    public String premiumSuccess(@RequestParam("userId") Integer userId, Model model) {
        User user = userService.getUserById(userId);
        if (user != null) {
            // プレミアム会員にアップグレード
            userService.upgradeToPremium(user);
            model.addAttribute("successMessage", "有料会員登録が完了しました。");
        } else {
            model.addAttribute("errorMessage", "ユーザーが見つかりませんでした。");
        }
        return "auth/verify"; // 成功ページ
    }

    // プレミアムプランキャンセルページ
    @GetMapping("/premium/cancel")
    public String premiumCancel(Model model) {
        model.addAttribute("errorMessage", "有料会員登録がキャンセルされました。");
        return "auth/verify"; // キャンセルページ
    }

    // プレミアム会員解除
    @PostMapping("/premium")
    public String cancelPremium(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, RedirectAttributes redirectAttributes) {
        User user = userService.getUserById(userDetailsImpl.getUser().getId());
        if (user != null && user.getIsPremium()) {
            userService.downgradeToFree(user);
            redirectAttributes.addFlashAttribute("successMessage", "有料会員を解除しました。");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "有料会員ではありません。");
        }
        return "redirect:/user";
    }
}
