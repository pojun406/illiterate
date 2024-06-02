package com.illiterate.illiterate.security.service;

import com.illiterate.illiterate.member.Entity.User;
import com.illiterate.illiterate.member.Repository.UserRepository;
import com.illiterate.illiterate.security.exception.CustomSecurityException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.illiterate.illiterate.common.enums.MemberErrorCode.NOT_FOUND_MEMBER_EMAIL;


@Service
@Qualifier("memberUserDetailsService")
@RequiredArgsConstructor
public class MemberUserDetailsService implements UserDetailsService {
    private final UserRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
        User user = memberRepository.findByUserid(userid)
                .orElseThrow(() -> new CustomSecurityException(NOT_FOUND_MEMBER_EMAIL));

        return UserDetailsImpl.from(user);
    }
}
