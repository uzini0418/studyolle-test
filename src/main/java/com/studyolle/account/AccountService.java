package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;
	private final JavaMailSender javaMailSender;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public Account processNewAccount(SignUpForm signUpForm) {
		Account newAccount = saveNewAccount(signUpForm); // --> transaction 일어난 상태 (save)
		newAccount.generateEmailCheckToken(); // --> 토큰 생성 후는 따로 transaction이 일어나지 않는다. 메서드에 @Transaction 추가 필요
		sendSignUpConfirmMail(newAccount);
		return newAccount;
	}

	private Account saveNewAccount(@Valid SignUpForm signUpForm) {
		Account account = Account.builder()
				.email(signUpForm.getEmail())
				.nickname(signUpForm.getNickname())
				.password(passwordEncoder.encode(signUpForm.getPassword()))
				.studyCreatedByWeb(true)
				.studyEnrollmentResultByWeb(true)
				.studyUpdatedByWeb(true)
				// byEamil 설정값은 기본 false
				.build();

		return accountRepository.save(account); // -->  transaction 일어남. persist 상태
	}

	private void sendSignUpConfirmMail(Account newAccount) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(newAccount.getEmail());
		mailMessage.setSubject("스터디올래, 회원 가입 인증"); // 메일 제목
		mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() + "&email=" + newAccount.getEmail()); // 메일 본문
		javaMailSender.send(mailMessage);
	}

	public void login(Account account) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken( // 변형된 방법
				//account.getNickname(),  // before
				new UserAccount(account), // after. Principal 객체 변경
				account.getPassword(), // 인코딩된 password
				List.of(new SimpleGrantedAuthority("ROLE_USER")));
		SecurityContextHolder.getContext().setAuthentication(token);

		/* 정석적인 방법
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password); // 사용자가 입력한 password
		Authentication authentication = authenticationManager.authenticate(token); // 매니저를 통해 인증을 거친 토큰을 넣어준다.
		SecurityContextHolder.getContext().setAuthentication(authentication);
		*/
	}
}
