package com.example.nagoyameshi.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;

@Controller
public class HomeController {
	private final RestaurantRepository restaurantRepository;
	private final CategoryRepository categoryRepository;
	
	public HomeController(RestaurantRepository restaurantRepository, CategoryRepository categoryRepository) {
		this.restaurantRepository = restaurantRepository;
		this.categoryRepository = categoryRepository;
	}
	@GetMapping("/")
	public String index(Model model) {
		List<Restaurant> newRestaurants = restaurantRepository.findTop5ByOrderByCreatedAtDesc();
		model.addAttribute("newRestaurants", newRestaurants);
		
		List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        return "index";
	}
	
	@GetMapping("/category/{categoryId}")
	public String getRestaurantsByCategory(@PathVariable Integer categoryId,
										   @RequestParam(value = "order", required = false) String order,
										   @PageableDefault Pageable pageable, 
										   Model model) {
		
	    Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
	    if (categoryOptional.isPresent()) {
	        Category category = categoryOptional.get();
	        Page<Restaurant> restaurantPage;
	        
	        // 並び替えの条件を追加
	        if ("priceAsc".equals(order)) {
	            restaurantPage = restaurantRepository.findByCategoryOrderByPriceAsc(category.getName(), pageable);
	        } else if ("priceDesc".equals(order)) {
	            restaurantPage = restaurantRepository.findByCategoryOrderByPriceDesc(category.getName(), pageable);
	        } else {
	            restaurantPage = restaurantRepository.findByCategoryOrderByCreatedAtDesc(category.getName(), pageable);
	        }
	        
	        model.addAttribute("category", category);
	        model.addAttribute("restaurantPage", restaurantPage);
	        model.addAttribute("order", order);  // orderの値をモデルに追加
	     
	        
	        return "restaurants/category";  // 通常のページ遷移
	    } else {
	        return "error/404";  // カテゴリが見つからない場合のエラーページ
	    }
	}
	
	@GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id, Model model) {
    	Restaurant restaurant = restaurantRepository.getReferenceById(id);
        Category category = restaurant.getCategory(); //restaurantからcategoryを取得
    	model.addAttribute("restaurant", restaurant);
        model.addAttribute("category", category); //categoryをmodelに追加
    
    return "restaurants/show";
   }
}