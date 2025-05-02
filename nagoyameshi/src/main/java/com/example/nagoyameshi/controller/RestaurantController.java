package com.example.nagoyameshi.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.form.ReservationInputForm;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {
    @Autowired
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;

    public RestaurantController(RestaurantRepository restaurantRepository, CategoryRepository categoryRepository) {
        this.restaurantRepository = restaurantRepository;
        this.categoryRepository = categoryRepository;
    }
   
    @GetMapping("/category/{categoryId}")
    public String getRestaurantsByCategory(@PathVariable Integer categoryId, @PageableDefault Pageable pageable, Model model) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            Page<Restaurant> restaurantPage = restaurantRepository.findByCategory(category, pageable);
            model.addAttribute("category", category);
            model.addAttribute("restaurantPage", restaurantPage);
            return "restaurants/category";  // テンプレート名を指定
        } else {
            return "error/404";  // カテゴリが見つからない場合のエラーページ
        }
    }
    
    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
                        @RequestParam(name = "category", required = false) String categoryName,
                        @RequestParam(name = "price", required = false) Integer price, 
                        @RequestParam(name = "order", required = false) String order,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                        Model model) 
    {
        Page<Restaurant> restaurantPage;

        if (keyword != null && !keyword.isEmpty()) {
            if (order != null && order.equals("priceAsc")) {
                restaurantPage = restaurantRepository.findByNameLikeOrAddressLikeOrderByPriceAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
            } else if (order != null && order.equals("priceDesc")) {
                restaurantPage = restaurantRepository.findByNameLikeOrAddressLikeOrderByPriceDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
            } else {
                restaurantPage = restaurantRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%",  pageable);
            }
        } else if (categoryName != null && !categoryName.isEmpty()) {
            Category categoryEntity = categoryRepository.findByName(categoryName);
            if (categoryEntity != null) {
                if (order != null && order.equals("priceAsc")) {
                    restaurantPage = restaurantRepository.findByCategoryOrderByPriceAsc(categoryEntity.getName(), pageable);
                } else if (order != null && order.equals("priceDesc")) {
                    restaurantPage = restaurantRepository.findByCategoryOrderByPriceDesc(categoryEntity.getName(), pageable);
                } else {
                    restaurantPage = restaurantRepository.findByCategoryOrderByCreatedAtDesc(categoryEntity.getName(), pageable);
                }
            } else {
                restaurantPage = Page.empty();
            }
        } else if (price != null) {
            if (order != null && order.equals("priceAsc")) {
                restaurantPage = restaurantRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
            } else if (order != null && order.equals("priceDesc")) {
                restaurantPage = restaurantRepository.findByPriceLessThanEqualOrderByPriceDesc(price, pageable);
            } else {
                restaurantPage = restaurantRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);    
            }
        } else {
            if (order != null && order.equals("priceAsc")) {
                restaurantPage = restaurantRepository.findAllByOrderByPriceAsc(pageable);
            } else if (order != null && order.equals("priceDesc")) {
                restaurantPage = restaurantRepository.findAllByOrderByPriceDesc(pageable);
            } else {
                restaurantPage = restaurantRepository.findAllByOrderByCreatedAtDesc(pageable);
            }
        }

        model.addAttribute("restaurantPage", restaurantPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", categoryName);
        model.addAttribute("price", price);
        model.addAttribute("order", order);

        return "restaurants/index";
    }
    
    @GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id, Model model){
        Restaurant restaurant = restaurantRepository.getReferenceById(id);
        model.addAttribute("restaurant", restaurant);
        
        ReservationInputForm reservationInputForm = new ReservationInputForm();
        model.addAttribute("reservationInputForm", reservationInputForm);

        return "restaurants/show";
    }
    
}
