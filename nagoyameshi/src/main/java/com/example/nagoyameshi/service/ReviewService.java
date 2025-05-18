package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReviewEditForm;
import com.example.nagoyameshi.form.ReviewRegisterForm;
import com.example.nagoyameshi.repository.ReviewRepository;

@Service
public class ReviewService {
	private final ReviewRepository reviewRepository;
	
	public ReviewService(ReviewRepository reviewRepository) { 
						
        this.reviewRepository = reviewRepository;  
        
    } 
	
	//登録用
	@Transactional
	public void create(Restaurant restaurant, User user, ReviewRegisterForm reviewRegisterForm) {
		// コメント: パラメータのnullチェックを行うべきです
		Review review = new Review();
		
		review.setRestaurant(restaurant);
		review.setUser(user);
		review.setScore(reviewRegisterForm.getScore());
		review.setComment(reviewRegisterForm.getComment());
		
		reviewRepository.save(review);
	}
		
	//更新用
	@Transactional
	public void update(ReviewEditForm reviewEditForm) {
		// コメント: getReferenceByIdではなく、findByIdを使用してレビューが存在するか確認すべきです
		Review review = reviewRepository.getReferenceById(reviewEditForm.getId());
		
		review.setScore(reviewEditForm.getScore());
		review.setComment(reviewEditForm.getComment());
		
		reviewRepository.save(review);
	}
	
	public boolean reviewJudge(Restaurant restaurant, User user) {
		// コメント: パラメータのnullチェックを行うべきです
		Review review = reviewRepository.findByUserAndRestaurant(user, restaurant);
		return review != null;
	}
}


