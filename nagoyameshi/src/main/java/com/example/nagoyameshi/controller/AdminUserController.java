package com.example.nagoyameshi.controller;

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

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.UserRepository;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
	
	private final UserRepository userRepository;        
    
    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;                
    }    
    
    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
    		            @RequestParam(name = "isPremium", required = false) Boolean isPremium,
    		            @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
    		            Model model) {
        Page<User> userPage;
        
        if (keyword != null && !keyword.isEmpty()) {
            if (isPremium != null) {
                userPage = userRepository.findByNameContainingOrFuriganaContainingAndIsPremium(keyword, keyword, isPremium, pageable);
            } else {
                userPage = userRepository.findByNameContainingOrFuriganaContaining("%" + keyword + "%", "%" + keyword + "%", pageable);
            }
        } else {
            if (isPremium != null) {
                userPage = userRepository.findByIsPremium(isPremium, pageable);
            } else {
                userPage = userRepository.findAll(pageable);
            }
        } 
        
     // isPremiumがnullの場合、デフォルト値を設定
        if (isPremium == null) {
        	isPremium = false; // デフォルトで無料会員とする
        }
        
        model.addAttribute("userPage", userPage);        
        model.addAttribute("keyword", keyword);
        model.addAttribute("isPremium", isPremium);               
        
        return "admin/users/index";
    }
    
    @GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id, Model model) {
        User user = userRepository.getReferenceById(id);
        
        model.addAttribute("user", user);
        
        return "admin/users/show";
    }
    
}
