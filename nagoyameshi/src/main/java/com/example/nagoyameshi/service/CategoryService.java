package com.example.nagoyameshi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.form.CategoryEditForm;
import com.example.nagoyameshi.form.CategoryRegisterForm;
import com.example.nagoyameshi.repository.CategoryRepository;


@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    @Transactional
    public void create(CategoryRegisterForm categoryRegisterForm) {
    	Category category = new Category();
    	MultipartFile imageFile = categoryRegisterForm.getImageFile();
    	
    	if(!imageFile.isEmpty()) {
    		// コメント: 画像処理のコードが重複しています。ファイル操作用のユーティリティクラスを作成すべきです
    		String imageName = imageFile.getOriginalFilename();
    		String hashedImageName = generateNewFileName(imageName);
    		Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
    		copyImageFile(imageFile, filePath);
    		category.setImageName(hashedImageName);
    	}
    	
    	category.setName(categoryRegisterForm.getName());
    	
    	categoryRepository.save(category);
    }
    
    @Transactional
    public void update(CategoryEditForm categoryEditForm) {
        // コメント: getReferenceByIdではなく、findByIdを使用してカテゴリが存在するか確認すべきです
        Category category = categoryRepository.getReferenceById(categoryEditForm.getId());
        MultipartFile imageFile = categoryEditForm.getImageFile();
        
        if (!imageFile.isEmpty()) {
            String imageName = imageFile.getOriginalFilename(); 
            String hashedImageName = generateNewFileName(imageName);
            Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
            copyImageFile(imageFile, filePath);
            category.setImageName(hashedImageName);
        }
        
        category.setName(categoryEditForm.getName());                
       
        // コメント: 以前の画像を削除する処理がありません。ディスクスペースの節約のために考慮すべきです        
        categoryRepository.save(category);
    }    
    
    // UUIDを使って生成したファイル名を返す
    public String generateNewFileName(String fileName) {
        String[] fileNames = fileName.split("\\.");                
        for (int i = 0; i < fileNames.length - 1; i++) {
            fileNames[i] = UUID.randomUUID().toString();            
        }
        String hashedFileName = String.join(".", fileNames);
        return hashedFileName;
    }     
    
    // 画像ファイルを指定したファイルにコピーする
    public void copyImageFile(MultipartFile imageFile, Path filePath) {           
        try {
            Files.copy(imageFile.getInputStream(), filePath);
        } catch (IOException e) {
            // コメント: 例外処理が不十分です。適切なログ出力と例外処理が必要です
            e.printStackTrace();
        }          
    } 

}
