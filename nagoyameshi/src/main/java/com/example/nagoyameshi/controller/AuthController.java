package com.example.nagoyameshi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.entity.VerificationToken;
import com.example.nagoyameshi.event.SignupEventPublisher;
import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.service.StripeService;
import com.example.nagoyameshi.service.UserService;
import com.example.nagoyameshi.service.VerificationTokenService;
import com.stripe.exception.StripeException;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {

    private final UserService userService;
    private final SignupEventPublisher signupEventPublisher;
    private final VerificationTokenService verificationTokenService;
    private final StripeService stripeService;

    public AuthController(UserService userService, SignupEventPublisher signupEventPublisher, VerificationTokenService verificationTokenService, StripeService stripeService) {
        this.userService = userService;
        this.signupEventPublisher = signupEventPublisher;
        this.verificationTokenService = verificationTokenService;
        this.stripeService = stripeService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        return "auth/signup";
    }

    @PostMapping("/signup")
    public Object signup(@ModelAttribute @Validated SignupForm signupForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) throws StripeException {
        // メールアドレスが登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
        if (userService.isEmailRegistered(signupForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);
        }

        // パスワードとパスワード（確認用）の入力値が一致しなければ、BindingResultオブジェクトにエラー内容を追加する
        if (!userService.isSamePassword(signupForm.getPassword(), signupForm.getPasswordConfirmation())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
            bindingResult.addError(fieldError);
        }

        // バリデーションエラーがあればサインアップページを再表示
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }

        // ユーザー作成処理
        User createdUser = userService.create(signupForm);

        // プレミアムプランの支払い処理
        if (signupForm.getIsPremium()) {
            String successUrl = httpServletRequest.getRequestURL().toString().replace("/signup", "/signup/premium/success?userId=" + createdUser.getId());
            String cancelUrl = httpServletRequest.getRequestURL().toString().replace("/signup", "/signup/premium/cancel");

            // SignupFormを渡してStripeセッションを作成
            String sessionId = stripeService.createCheckoutSession(signupForm, successUrl, cancelUrl);

            return new RedirectView("https://checkout.stripe.com/c/pay/" + sessionId);
        }

        // サインアップイベントの発行
        String requestUrl = httpServletRequest.getRequestURL().toString();
        signupEventPublisher.publishSignupEvent(createdUser, requestUrl);
        redirectAttributes.addFlashAttribute("successMessage", "ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。");

        return "redirect:/";
    }

    @GetMapping("/signup/verify")
    public String verify(@RequestParam(name = "token") String token, Model model) {
        VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);

        if (verificationToken != null) {
            User user = verificationToken.getUser();
            userService.enableUser(user);
            String successMessage = "会員登録が完了しました。";
            model.addAttribute("successMessage", successMessage);
        } else {
            String errorMessage = "トークンが無効です。";
            model.addAttribute("errorMessage", errorMessage);
        }

        return "auth/verify";
    }

    @GetMapping("/signup/premium/success")
    public String premiumSuccess(@RequestParam("userId") Integer userId, Model model) {
        User user = userService.getUserById(userId);
        if (user != null) {
            userService.upgradeToPremium(user);
            model.addAttribute("successMessage", "有料会員登録が完了しました。");
        } else {
            model.addAttribute("errorMessage", "ユーザーが見つかりませんでした。");
        }
        return "auth/verify";
    }

    @GetMapping("/signup/premium/cancel")
    public String premiumCancel(Model model) {
        model.addAttribute("errorMessage", "有料会員登録がキャンセルされました。");
        return "auth/verify";
    }
}
