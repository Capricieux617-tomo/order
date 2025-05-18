package com.example.nagoyameshi.service;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.RoleRepository;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(SignupForm signupForm) {
        // メールアドレス重複チェック
        if (isEmailRegistered(signupForm.getEmail())) {
            throw new RuntimeException("このメールアドレスは既に登録されています。");
        }

        User user = new User();
        BeanUtils.copyProperties(signupForm, user);
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setRole(roleRepository.findByAuthority("ROLE_USER"));
        user.setEnabled(false);
        user.setIsPremium(false);

        return userRepository.save(user);
    }

    @Transactional
    public User create(SignupForm signupForm) {
        User user = new User();
        Role role = roleRepository.findByAuthority("ROLE_USER");

        user.setName(signupForm.getName());
        user.setFurigana(signupForm.getFurigana());
        user.setPhoneNumber(signupForm.getPhoneNumber());
        user.setEmail(signupForm.getEmail());
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setRole(role);
        user.setEnabled(true);
        user.setIsPremium(false);

        return userRepository.save(user);
    }

    @Transactional
    public void update(UserEditForm userEditForm) {
        User user = userRepository.getReferenceById(userEditForm.getId());

        user.setName(userEditForm.getName());
        user.setFurigana(userEditForm.getFurigana());
        user.setPhoneNumber(userEditForm.getPhoneNumber());
        user.setEmail(userEditForm.getEmail());

        // isPremiumがnullの場合、falseをセット
        user.setIsPremium(userEditForm.getIsPremium() != null ? userEditForm.getIsPremium() : false);

        userRepository.save(user);
    }

    // メールアドレスが登録済みかどうかをチェックする
    public boolean isEmailRegistered(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }

    // パスワードとパスワード（確認用）の入力値が一致するかどうかをチェックする
    public boolean isSamePassword(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }

    // ユーザーを有効にする
    @Transactional
    public void enableUser(User user) {
        user.setEnabled(true);
        userRepository.save(user);
    }

    // メールアドレスが変更されたかどうかをチェックする
    public boolean isEmailChanged(UserEditForm userEditForm) {
        User currentUser = userRepository.getReferenceById(userEditForm.getId());
        return userEditForm.getEmail() != null && !userEditForm.getEmail().equals(currentUser.getEmail());
    }

    // 現在ログインしているユーザーを取得
    public User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser();
    }

    // ユーザーを有料会員にアップグレードする
    @Transactional
    public void upgradeToPremium(User user) {
        user.setIsPremium(true);
        userRepository.save(user); // 確実にデータベースに保存される
    }

    // ユーザーIDでユーザーを取得する
    public User getUserById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }

    // ユーザーを有料会員から無料会員にダウングレードする
    @Transactional
    public void downgradeToFree(User user) {
        // コメント: Stripeのサブスクリプションをキャンセルする処理が必要です
        // コメント: user.getStripeSubscriptionId() がnullでないか確認すべきです
        user.setIsPremium(false);
        // コメント: 他の関連フィールド（stripeSubscriptionIdなど）もクリアすべきです
        userRepository.save(user); // 確実にデータベースに保存される
    }
}
