package com.illiterate.illiterate.security.service;

import com.illiterate.illiterate.member.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private Long id;
    private String userid;
    private String password;
    private List<GrantedAuthority> authorities;
    @Getter
    private User user;

    public static UserDetails from(User member) {
        List<GrantedAuthority> authorities = member.getRoles() != null ?
                List.of(new SimpleGrantedAuthority(member.getRoles().name())): null;

        return new UserDetailsImpl(
                member.getId(),
                member.getUserid(),
                member.getPassword(),
                authorities,
                member
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
        return userid;
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