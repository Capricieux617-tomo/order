package com.example.nagoyameshi.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRegisterForm {
	@NotNull(message = "評価を入力してください。")
	@Min(value = 1, message = "評価は1以上で入力してください。")
	@Max(value = 5, message = "評価は5以下で入力してください。")
	private Integer score;
	
	@NotBlank(message = "レビューを書き込んでください")
	// コメント: コメントの長さ制限（例：@Size(max=1000)）を追加すべきです
	private String comment;

}
