package com.example.nagoyameshi.form;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRegisterForm {
	 @NotBlank(message = "カテゴリ名を入力してください。")
     private String name;
     
	 private MultipartFile imageFile;

}
