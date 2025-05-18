package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// コメント: フィールドに@Emailアノテーションを追加すべきです
// コメント: import jakarta.validation.constraints.Email; が必要です

@Data
public class UserEditForm {
	@NotNull
    private Integer id;
    
    @NotBlank(message = "氏名を入力してください。")
    // コメント: 名前の長さ制限を設定すべきです（@Size(max=50)など）
    private String name;
    
    @NotBlank(message = "フリガナを入力してください。")
    // コメント: フリガナのバリデーション（カタカナのみなど）を追加すべきです
    private String furigana;
    
    @NotBlank(message = "電話番号を入力してください。")
    // コメント: 電話番号の形式チェック（@Pattern）を追加すべきです
    private String phoneNumber;
    
    @NotBlank(message = "メールアドレスを入力してください。")
    // コメント: @Emailアノテーションでメールアドレスの形式をチェックすべきです
    private String email;
    
    @NotNull(message = "会員種別を選択してください。")
    // コメント: デフォルト値とNotNullを同時に設定すると、通常はNotNullが不要になります（デフォルト値があるため）
    private Boolean isPremium = false;
    
    // コンストラクタを追加 (特にIDが必要な場合)
    public UserEditForm() {} // 引数なしコンストラクタは必須

    public UserEditForm(Integer id, String name, String furigana, String phoneNumber, String email, Boolean isPremium) {
        this.id = id;
        this.name = name;
        this.furigana = furigana;
        this.phoneNumber = phoneNumber;
        this.email = email;
        // コメント: isPremiumがnullの場合の処理が考慮されていません
        this.isPremium = isPremium;
    }
}

