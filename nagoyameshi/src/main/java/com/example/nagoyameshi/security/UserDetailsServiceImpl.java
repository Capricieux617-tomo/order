package com.example.nagoyameshi.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;    
    
    // コメント: Loggerを追加すると、認証処理のデバッグや監視が容易になります
    // private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;        
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {  
        try {
            // コメント: emailのバリデーション（nullや空文字チェック）を追加すべきです
            User user = userRepository.findByEmail(email);
            if (user == null) {
                // コメント: エラーメッセージはプロパティファイルに外部化すべきです
                throw new UsernameNotFoundException("ユーザーが見つかりませんでした。");
            }

            // 権限を取得する際に、Roleエンティティのauthorityプロパティを使用
            // コメント: user.getRole()がnullの場合の処理が必要です
            String userRole = user.getRole().getAuthority(); 
            Collection<GrantedAuthority> authorities = new ArrayList<>();         
            authorities.add(new SimpleGrantedAuthority(userRole));
            return new UserDetailsImpl(user, authorities);
        } catch (Exception e) {
            // コメント: 一般的なExceptionではなく、より具体的な例外をキャッチすべきです
            // コメント: ログ出力を追加すべきです（例：logger.error("ユーザー認証エラー: {}", email, e);）
            throw new UsernameNotFoundException("ユーザーが見つかりませんでした。", e); 
        }
    }   
}