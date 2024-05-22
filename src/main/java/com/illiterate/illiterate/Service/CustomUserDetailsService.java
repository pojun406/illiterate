package com.illiterate.illiterate.Service;

import com.illiterate.illiterate.DTO.CustomUserDetails;
import com.illiterate.illiterate.Entity.User;
import com.illiterate.illiterate.Repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User userData = userRepository.findByUserid(username);

        if (userData != null) {

            return new CustomUserDetails(userData);
        }


        return null;
    }


}