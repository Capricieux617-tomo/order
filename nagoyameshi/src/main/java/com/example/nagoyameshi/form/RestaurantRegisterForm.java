package com.example.nagoyameshi.form;

import java.time.LocalTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RestaurantRegisterForm {
    @NotBlank(message = "店舗名を入力してください。")
    private String name;

    private MultipartFile imageFile;

    @NotNull(message = "カテゴリを選択してください。")
    private Integer categoryId; // 既存カテゴリを選択

    @NotBlank(message = "説明を入力してください。")
    private String description;

    @NotNull(message = "料金を入力してください。")
    @Min(value = 1, message = "料金は1円以上に設定してください。")
    private Integer price;

    @NotBlank(message = "郵便番号を入力してください。")
    private String postalCode;

    @NotBlank(message = "住所を入力してください。")
    private String address;

    @NotBlank(message = "電話番号を入力してください。")
    private String phoneNumber;

    @NotBlank(message = "定休日があれば入力してください。")
    private String holidays; 

    @NotNull(message = "開店時間を入力してください。")
    private LocalTime openTime;

    @NotNull(message = "閉店時間を入力してください。")
    private LocalTime closeTime;
}