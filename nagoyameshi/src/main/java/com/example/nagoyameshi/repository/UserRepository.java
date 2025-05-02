package com.example.nagoyameshi.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>{
	public User findByEmail(String email);
	public Optional<User> findById(Integer id);
	public Page<User> findByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable);
    Page<User> findByNameContainingOrFuriganaContainingAndIsPremium(String nameKeyword, String furiganaKeyword, Boolean isPremium, Pageable pageable);

    Page<User> findByNameContainingOrFuriganaContaining(String nameKeyword, String furiganaKeyword, Pageable pageable); 

    Page<User> findByIsPremium(Boolean isPremium, Pageable pageable);
 
	}


