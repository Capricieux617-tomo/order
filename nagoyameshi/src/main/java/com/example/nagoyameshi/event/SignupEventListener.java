package com.example.nagoyameshi.event;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.service.VerificationTokenService;

@Component
public class SignupEventListener {
	 private final VerificationTokenService verificationTokenService;    
     private final JavaMailSender javaMailSender;
     
     public SignupEventListener(VerificationTokenService verificationTokenService, JavaMailSender mailSender) {
         this.verificationTokenService = verificationTokenService;        
         this.javaMailSender = mailSender;
     }
 
     @EventListener
     // コメント: privateではなくpublicにすべきです。一般的にイベントリスナーメソッドはpublicで宣言されます
     private void onSignupEvent(SignupEvent signupEvent) {
         User user = signupEvent.getUser();
         String token = UUID.randomUUID().toString();
         verificationTokenService.create(user, token);
         
         // コメント: メール関連の文字列はプロパティファイルに外部化すべきです
         String recipientAddress = user.getEmail();
         String subject = "メール認証";
         // コメント: URLの構築方法は脆弱です。URLの妥当性チェックや、URIビルダーを使用すべきです
         String confirmationUrl = signupEvent.getRequestUrl() + "/verify?token=" + token;
         String message = "以下のリンクをクリックして会員登録を完了してください。";
         
         // コメント: メール送信処理は別のメソッドに抽出すべきです
         SimpleMailMessage mailMessage = new SimpleMailMessage(); 
         mailMessage.setTo(recipientAddress);
         mailMessage.setSubject(subject);
         mailMessage.setText(message + "\n" + confirmationUrl);
         // コメント: 例外処理が欠けています。メール送信に失敗した場合のハンドリングが必要です
         javaMailSender.send(mailMessage);
         // コメント: ログ出力が不足しています。メール送信成功や失敗のログを残すべきです
     }
     
}
