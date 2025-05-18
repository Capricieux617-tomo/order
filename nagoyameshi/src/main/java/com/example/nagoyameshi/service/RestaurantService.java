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
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.form.RestaurantEditForm;
import com.example.nagoyameshi.form.RestaurantRegisterForm;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;

@Service
public class RestaurantService {
	private final RestaurantRepository restaurantRepository;
	private final CategoryRepository categoryRepository;
	
	public RestaurantService(RestaurantRepository restaurantRepository, CategoryRepository categoryRepository) {
		this.restaurantRepository = restaurantRepository;
		this.categoryRepository = categoryRepository;
	}
	
	@Transactional
	public void create(RestaurantRegisterForm restaurantRegisterForm) {
		Restaurant restaurant = new Restaurant();
		MultipartFile imageFile = restaurantRegisterForm.getImageFile();
		
		
		if (!imageFile.isEmpty()) {
            // コメント: 画像処理のコードがCategoryServiceと重複しています。共通ユーティリティに抽出すべきです
            String imageName = imageFile.getOriginalFilename(); 
            String hashedImageName = generateNewFileName(imageName);
            Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
            copyImageFile(imageFile, filePath);
            restaurant.setImageName(hashedImageName);
        }
		
		restaurant.setName(restaurantRegisterForm.getName());
		// categoryIdをCategoryエンティティに変換
		// コメント: 例外メッセージをより具体的にし、例外処理を追加すべきです
		Category category = categoryRepository.findById(restaurantRegisterForm.getCategoryId()).orElseThrow(() -> new IllegalArgumentException("カテゴリが見つかりません"));
		restaurant.setCategory(category);
		restaurant.setDescription(restaurantRegisterForm.getDescription());
		restaurant.setPrice(restaurantRegisterForm.getPrice());
		restaurant.setPostalCode(restaurantRegisterForm.getPostalCode());
		restaurant.setAddress(restaurantRegisterForm.getAddress());
		restaurant.setPhoneNumber(restaurantRegisterForm.getPhoneNumber());
		restaurant.setHolidays(restaurantRegisterForm.getHolidays());
        restaurant.setOpenTime(restaurantRegisterForm.getOpenTime());
        restaurant.setCloseTime(restaurantRegisterForm.getCloseTime());
		
		restaurantRepository.save(restaurant);
	}
	
	@Transactional
	public void update(RestaurantEditForm restaurantEditForm) {
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantEditForm.getId());
		MultipartFile imageFile = restaurantEditForm.getImageFile();
		
		if (!imageFile.isEmpty()) {
			String imageName = imageFile.getOriginalFilename();
			String hashedImageName = generateNewFileName(imageName);
			Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
			copyImageFile(imageFile, filePath);
			restaurant.setImageName(hashedImageName);
		}
		
		restaurant.setName(restaurantEditForm.getName());
		//categoryIdをCategoryエンティティに変換
		Category category = categoryRepository.findById(restaurantEditForm.getCategoryId()).orElseThrow(() -> new IllegalArgumentException("カテゴリが見つかりません"));
		restaurant.setCategory(category);
		restaurant.setDescription(restaurantEditForm.getDescription());
		restaurant.setPrice(restaurantEditForm.getPrice());
		restaurant.setPostalCode(restaurantEditForm.getPostalCode());
		restaurant.setAddress(restaurantEditForm.getAddress());
		restaurant.setPhoneNumber(restaurantEditForm.getPhoneNumber());
		restaurant.setHolidays(restaurantEditForm.getHolidays());
        restaurant.setOpenTime(restaurantEditForm.getOpenTime());
        restaurant.setCloseTime(restaurantEditForm.getCloseTime());
		
		restaurantRepository.save(restaurant);
		
	}
         
	
	 // UUIDを使って生成したファイル名を返す
    public String generateNewFileName(String fileName) {
        // コメント: fileNameがnullまたは空文字の場合のチェックが必要です
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
            // コメント: より適切な例外処理が必要です。例外をログに記録し、意味のある例外に変換すべきです
            e.printStackTrace();
        }          
    } 
}
