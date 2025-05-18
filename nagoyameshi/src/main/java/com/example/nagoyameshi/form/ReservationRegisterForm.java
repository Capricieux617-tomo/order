package com.example.nagoyameshi.form;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// コメント: @NoArgsConstructorアノテーションも追加すべきです。JSONシリアライズなどで必要になります
public class ReservationRegisterForm {
	// コメント: フィールドにバリデーションアノテーション（@NotNull, @Min等）を追加すべきです
	private Integer restaurantId;
    
    private Integer userId;    
        
    // コメント: 日付型はLocalDateを使用し、文字列ではなく適切な型を使うべきです
    // コメント: import java.time.LocalDate; が必要です
    private String orderDate;    
        
    // コメント: 時間型はLocalTimeを使用し、文字列ではなく適切な型を使うべきです
    // コメント: import java.time.LocalTime; が必要です
    private String orderTime;    
    
    // コメント: @Min(1)などのバリデーションを追加すべきです
    private Integer numberOfPeople;

}
