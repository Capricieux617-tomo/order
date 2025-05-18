package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.FavoriteRepository;

@Service
public class FavoriteService {
	private final FavoriteRepository favoriteRepository;
	
	public FavoriteService(FavoriteRepository favoriteRepository) {
		this.favoriteRepository = favoriteRepository;
	}

    @Transactional
    public void subscribe(Restaurant restaurant, User user) {
        // コメント: 引数のnullチェックを追加すべきです
        Favorite existingFavorite = favoriteRepository.findByRestaurantAndUser(restaurant, user);
        
        if (existingFavorite == null) {
            Favorite favorite = new Favorite();
            favorite.setRestaurant(restaurant);
            favorite.setUser(user);
            favoriteRepository.save(favorite);
        }
    }

    // お気に入りか判定
    public boolean isFavorite(Restaurant restaurant, User user) {
        // コメント: 引数のnullチェックを追加すべきです
        Favorite favorite = favoriteRepository.findByRestaurantAndUser(restaurant, user);
        return favorite != null;
    }
    
    // コメント: お気に入りを解除するメソッド（unsubscribeやremoveFavorite）を追加すべきです
}
