package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer>{
	public Page<Restaurant> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword, Pageable pageable);
	public Page<Restaurant> findByNameLikeOrAddressLikeOrderByPriceAsc(String nameKeyword, String addressKeyword, Pageable pageable);
	public Page<Restaurant> findByNameLikeOrAddressLikeOrderByPriceDesc(String nameKeyword, String addressKeyword, Pageable pageable);
	public Page<Restaurant> findByCategoryOrderByCreatedAtDesc(String string, Pageable pageable);
	public Page<Restaurant> findByCategoryOrderByPriceAsc(String string, Pageable pageable);
	public Page<Restaurant> findByCategoryOrderByPriceDesc(String string, Pageable pageable);
    public Page<Restaurant> findByPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);
    public Page<Restaurant> findByPriceLessThanEqualOrderByPriceAsc(Integer price, Pageable pageable);
    public Page<Restaurant> findByPriceLessThanEqualOrderByPriceDesc(Integer price, Pageable pageable);
    public Page<Restaurant> findAllByOrderByCreatedAtDesc(Pageable pageable);
    public Page<Restaurant> findAllByOrderByPriceAsc(Pageable pageable);
    public Page<Restaurant> findAllByOrderByPriceDesc(Pageable pageable);
    
	public Page<Restaurant> findByCategory(Category category, Pageable pageable);
	public Page<Restaurant> findByNameLike(String string, Pageable pageable);
	public List<Restaurant> findTop5ByOrderByCreatedAtDesc();
	public List<Restaurant> findByCategory(Category category);    
}