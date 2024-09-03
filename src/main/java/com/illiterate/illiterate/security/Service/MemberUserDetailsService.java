package com.illiterate.illiterate.security.Service;


import com.illiterate.illiterate.member.Entity.Member;
import com.illiterate.illiterate.member.Repository.MemberRepository;
import com.illiterate.illiterate.security.Exception.CustomSecurityException;
import com.illiterate.illiterate.security.Service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

import static com.illiterate.illiterate.common.enums.MemberErrorCode.DELETE_MEMBER;
import static com.illiterate.illiterate.common.enums.MemberErrorCode.NOT_FOUND_MEMBER_EMAIL;
import static com.illiterate.illiterate.member.enums.StatusType.INACTIVE;

@Service
@Qualifier("memberUserDetailsService")
@RequiredArgsConstructor
public class MemberUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomSecurityException(NOT_FOUND_MEMBER_EMAIL));

        if(member.getStatus().equals(INACTIVE)) {
            throw new CustomSecurityException(DELETE_MEMBER);
        }
        return UserDetailsImpl.from(member);
    }
}
