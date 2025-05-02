package com.example.nagoyameshi.form;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryEditForm {
	@NotNull
	private Integer id;
	
	@NotBlank(message = "カテゴリ名を入力してください。")
	private String name;
	
	private MultipartFile imageFile;

}
