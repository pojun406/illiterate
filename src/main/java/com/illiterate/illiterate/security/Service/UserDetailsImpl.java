package com.illiterate.illiterate.security.Service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.illiterate.illiterate.member.Entity.Member;
import com.illiterate.illiterate.member.enums.StatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private Long id;
    private String userId;
    @JsonIgnore
    private String password;
    private List<GrantedAuthority> authorities;

    public static UserDetails from(Member member) {
        List<GrantedAuthority> authorities = member.getRoles() != null ?
                List.of(new SimpleGrantedAuthority(member.getRoles().name())): null;

        return new UserDetailsImpl(
                member.getId(),
                member.getUserId(),
                member.getPassword(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
