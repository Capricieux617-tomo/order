package com.example.nagoyameshi.controller;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationInputForm;
import com.example.nagoyameshi.form.ReservationRegisterForm;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.ReservationService;

import jakarta.validation.Valid;

@Controller
public class ReservationController {
    private final ReservationRepository reservationRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReservationService reservationService;

    public ReservationController(ReservationRepository reservationRepository, RestaurantRepository restaurantRepository, ReservationService reservationService) {
        this.reservationRepository = reservationRepository;
        this.restaurantRepository = restaurantRepository;
        this.reservationService = reservationService;
    }
    
    // 予約関連の処理を行う前に、ユーザーが有料会員かどうかを確認する
    private boolean isPremiumUser(User user) {
        return user != null && user.getIsPremium();
    }

    @GetMapping("/reservations")
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, Model model) {
        User user = userDetailsImpl.getUser();
        
     // ユーザーが有料会員でない場合、予約画面に遷移しないようにする
        if (!isPremiumUser(user)) {
            return "redirect:/user/edit";  // 有料会員登録ページにリダイレクト
        }
        Page<Reservation> reservationPage = reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        model.addAttribute("reservationPage", reservationPage);
        return "reservations/index";
    }

    @GetMapping("/restaurants/{id}/reservations/input")
    public String input(@PathVariable(name = "id") Integer id,
                        @ModelAttribute @Valid ReservationInputForm reservationInputForm,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes,
                        Model model,
                        @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
    	
    	// ユーザーが有料会員かどうかチェック
        User user = userDetailsImpl.getUser();
        if (!isPremiumUser(user)) {
            return "redirect:/user/edit";  // 有料会員登録ページにリダイレクト
        }
        Restaurant restaurant = restaurantRepository.getReferenceById(id);
        model.addAttribute("restaurant", restaurant);

        // 入力値検証エラーの場合、restaurants/show.htmlへ遷移
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "予約内容に不備があります。");
            return "restaurants/show";
        }

        // 入力値をセッションまたはFlashスコープに保存し、確認画面へリダイレクト
        redirectAttributes.addFlashAttribute("reservationInputForm", reservationInputForm);
        return "redirect:/restaurants/{id}/reservations/confirm";
    }

    @GetMapping("/restaurants/{id}/reservations/confirm")
    public String confirm(@PathVariable(name = "id") Integer id,
                          @ModelAttribute ReservationInputForm reservationInputForm,
                          @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                          Model model) {
    	
    	// ユーザーが有料会員かどうかチェック
        User user = userDetailsImpl.getUser();
        if (!isPremiumUser(user)) {
            return "redirect:/user/edit";  // 有料会員登録ページにリダイレクト
        }
        Restaurant restaurant = restaurantRepository.getReferenceById(id);
        LocalDate orderDate = reservationInputForm.getOrderDate();
        LocalTime orderTime = reservationInputForm.getOrderTime();

        ReservationRegisterForm reservationRegisterForm = new ReservationRegisterForm(restaurant.getId(), user.getId(), orderDate.toString(), orderTime.toString(), reservationInputForm.getNumberOfPeople());

        model.addAttribute("restaurant", restaurant);  
        model.addAttribute("reservationRegisterForm", reservationRegisterForm);

        return "reservations/confirm";
    }

    @PostMapping("/restaurants/{id}/reservations/create")
    public String create(@PathVariable(name = "id") Integer id, 
                         @ModelAttribute ReservationRegisterForm reservationRegisterForm, 
                         Model model,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
    	
        User user = userDetailsImpl.getUser();
        if (!isPremiumUser(user)) {
            return "redirect:/user/edit";
        }
        try {
            reservationService.create(reservationRegisterForm);
            return "redirect:/reservations?reserved";
        } catch (IllegalArgumentException e) {
            // 予約に失敗した場合（定休日、営業時間外など）
            Restaurant restaurant = restaurantRepository.getReferenceById(id); 
            model.addAttribute("restaurant", restaurant);
            model.addAttribute("reservationInputForm", new ReservationInputForm()); // フォームを再表示するためにインスタンス化
            model.addAttribute("errorMessage", e.getMessage()); // エラーメッセージを表示
            return "restaurants/show"; // 予約ページへ戻る
        }
    }
    
}
	