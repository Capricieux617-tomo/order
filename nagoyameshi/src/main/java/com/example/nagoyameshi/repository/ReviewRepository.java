package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Integer>{
	public Page<Review> findByRestaurantOrderByCreatedAtDesc(Restaurant restaurant, Pageable pageable);
	
	public long countByRestaurant(Restaurant restaurant);
	
	public Review findByUserAndRestaurant(User user, Restaurant restaurant);

	
	public List<Review> findTop6ByRestaurantOrderByCreatedAtDesc(Restaurant restaurant);

}
