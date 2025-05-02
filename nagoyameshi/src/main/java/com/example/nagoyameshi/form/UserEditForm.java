package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserEditForm {
	@NotNull
    private Integer id;
    
    @NotBlank(message = "氏名を入力してください。")
    private String name;
    
    @NotBlank(message = "フリガナを入力してください。")
    private String furigana;
    
    @NotBlank(message = "電話番号を入力してください。")
    private String phoneNumber;
    
    @NotBlank(message = "メールアドレスを入力してください。")
    private String email;
    
    @NotNull(message = "会員種別を選択してください。")
    private Boolean isPremium = false;
    
    // コンストラクタを追加 (特にIDが必要な場合)
    public UserEditForm() {} // 引数なしコンストラクタは必須

    public UserEditForm(Integer id, String name, String furigana, String phoneNumber, String email, Boolean isPremium) {
        this.id = id;
        this.name = name;
        this.furigana = furigana;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.isPremium = isPremium;
    }
}

