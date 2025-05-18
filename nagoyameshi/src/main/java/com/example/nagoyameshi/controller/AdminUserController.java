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
        
        // コメント: この条件分岐が複雑すぎるため、メソッドを分割するかサービスレイヤーに移動すべきです
        if (keyword != null && !keyword.isEmpty()) {
            if (isPremium != null) {
                userPage = userRepository.findByNameContainingOrFuriganaContainingAndIsPremium(keyword, keyword, isPremium, pageable);
            } else {
                // コメント: リポジトリメソッドが"%"を含んでいるなら、ここでは不要です。メソッド名と実装を確認してください
                userPage = userRepository.findByNameContainingOrFuriganaContaining("%" + keyword + "%", "%" + keyword + "%", pageable);
            }
        } else {
            if (isPremium != null) {
                userPage = userRepository.findByIsPremium(isPremium, pageable);
            } else {
                userPage = userRepository.findAll(pageable);
            }
        } 
        
        // コメント: isPremiumの処理順序が不適切です。検索の前にデフォルト値を設定すべきです
        // コメント: この処理は実際には検索結果に影響しないため、不要か誤りである可能性があります
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
        // コメント: getReferenceByIdではなく、findByIdを使用して存在確認すべきです
        // コメント: ユーザーが存在しない場合のエラーハンドリングが必要です
        User user = userRepository.getReferenceById(id);
        
        model.addAttribute("user", user);
        
        return "admin/users/show";
    }
}
