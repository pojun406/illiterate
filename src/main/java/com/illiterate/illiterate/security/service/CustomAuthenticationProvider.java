package com.illiterate.illiterate.security.service;

import com.illiterate.illiterate.security.exception.CustomSecurityException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.illiterate.illiterate.common.enums.MemberErrorCode.CHECK_ID_OR_PASSWORD;

/**
 * AuthenticationManager 셋팅
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	private final PasswordEncoder passwordEncoder;
	private final MemberUserDetailsService memberUserDetailsService;

	public CustomAuthenticationProvider(
		PasswordEncoder passwordEncoder,
		@Qualifier("memberUserDetailsService") MemberUserDetailsService memberUserDetailsService
	){
		this.passwordEncoder = passwordEncoder;
		this.memberUserDetailsService = memberUserDetailsService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		System.out.println("auth : " + authentication);
		String id = authentication.getName();
		String password = authentication.getCredentials().toString();

		UserDetailsImpl userDetails;

		userDetails = (UserDetailsImpl)memberUserDetailsService.loadUserByUsername(id);

		// 비밀번호 확인
		if(!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new CustomSecurityException(CHECK_ID_OR_PASSWORD);
		}

		return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
