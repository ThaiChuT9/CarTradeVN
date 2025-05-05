package com.cartradevn.cartradevn.administration.services;

import com.cartradevn.cartradevn.administration.entity.User;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cartradevn.cartradevn.administration.respository.UserRepo;

@Service
public class CustomUserDetailsService implements UserDetailsService  {
    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        // Thêm prefix ROLE_ nếu chưa có
        String roleStr = user.getRole().toString().startsWith("ROLE_") ? 
                        user.getRole().toString() : 
                        "ROLE_" + user.getRole().toString();
        authorities.add(new SimpleGrantedAuthority(roleStr));
        
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                authorities
        );
    }
}
