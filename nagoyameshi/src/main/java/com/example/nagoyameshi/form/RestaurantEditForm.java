package com.example.nagoyameshi.form;

import java.time.LocalTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// コメント: @NoArgsConstructorアノテーションも追加すべきです。フレームワークでのシリアライゼーション時に必要です
public class RestaurantEditForm {
	@NotNull
    private Integer id;    
    
    @NotBlank(message = "店舗名を入力してください。")
    // コメント: 名前の長さ制限（@Size(max=255)など）を追加すべきです
    private String name;
        
    // コメント: 画像ファイルのサイズ制限やタイプ検証のバリデーションを追加すべきです
    private MultipartFile imageFile;
    
    @NotNull(message = "カテゴリを選択してください。")
	private Integer categoryId; // 既存カテゴリを選択
	
        
    @NotBlank(message = "説明を入力してください。")
    // コメント: 説明の長さ制限（@Size(max=1000)など）を追加すべきです
    private String description; 
    
    @NotNull(message = "料金を入力してください。")
    @Min(value = 1, message = "料金は1円以上に設定してください。")
    // コメント: 料金の上限（@Max）も設定すると良いと思います
    private Integer price; 
    
    @NotBlank(message = "郵便番号を入力してください。")
    // コメント: 郵便番号のフォーマット検証（@Pattern(regexp="^\\d{3}-?\\d{4}$")など）を追加すべきです
    private String postalCode;
    
    @NotBlank(message = "住所を入力してください。")
    private String address;
    
    @NotBlank(message = "電話番号を入力してください。")
    // コメント: 電話番号のフォーマット検証（@Pattern）を追加すべきです
    private String phoneNumber;
	
    @NotBlank(message = "定休日を入力してください。")
    private String holidays;

    @NotNull(message = "開店時間を入力してください。")
    private LocalTime openTime;

    @NotNull(message = "閉店時間を入力してください。")
    private LocalTime closeTime;
    
    // コメント: 開店時間と閉店時間の関係をチェックする（openTime < closeTime）カスタムバリデーションを追加すべきです
}
