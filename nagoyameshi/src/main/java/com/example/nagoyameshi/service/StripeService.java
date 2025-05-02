package com.example.nagoyameshi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.form.UserEditForm;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class StripeService {

    @Value("${stripe.api-key}")
    private String apiKey;

    @Value("${stripe.premium.priceId}")
    private String premiumPriceId;

    @Value("${stripe.free.priceId}")
    private String freePriceId;

    // ============================
    // ① 既存の 3引数バージョン
    // ============================
    public String createCheckoutSession(Object form, String successUrl, String cancelUrl) throws StripeException {
        Stripe.apiKey = apiKey;

        String priceId = getPriceIdFromForm(form);

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPrice(priceId)
                        .setQuantity(1L)
                        .build())
                .build();

        Session session = Session.create(params);
        return session.getId();
    }

    // ============================
    // ② 新しく追加する 4引数バージョン
    // ============================
    public String createCheckoutSession(Object form, String successUrl, String cancelUrl, String priceType) throws StripeException {
        Stripe.apiKey = apiKey;

        String priceId;
        if ("premium".equalsIgnoreCase(priceType)) {
            priceId = premiumPriceId;
        } else {
            priceId = freePriceId;
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPrice(priceId)
                        .setQuantity(1L)
                        .build())
                .build();

        Session session = Session.create(params);
        return session.getId();
    }

    // ============================
    // Price ID をフォームから取得
    // ============================
    private String getPriceIdFromForm(Object form) {
        if (form instanceof SignupForm) {
            SignupForm signupForm = (SignupForm) form;
            System.out.println("SignupForm isPremium: " + signupForm.getIsPremium());
            return signupForm.getIsPremium() ? premiumPriceId : freePriceId;

        } else if (form instanceof UserEditForm) {
            UserEditForm userEditForm = (UserEditForm) form;
            System.out.println("UserEditForm isPremium: " + userEditForm.getIsPremium());
            return userEditForm.getIsPremium() ? premiumPriceId : freePriceId;
        }
        return freePriceId;
    }
}
