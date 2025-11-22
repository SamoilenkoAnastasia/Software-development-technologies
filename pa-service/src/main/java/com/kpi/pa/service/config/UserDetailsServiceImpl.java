package com.kpi.pa.service.config;

import com.kpi.pa.service.repo.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepo userRepo;

    public UserDetailsServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String idOrUsername) throws UsernameNotFoundException {
        
        
        try {
            Long userId = Long.valueOf(idOrUsername);
            return userRepo.findById(userId)
                    .map(user -> new org.springframework.security.core.userdetails.User(
                            String.valueOf(user.getId()), 
                            user.getPassword(), 
                            new ArrayList<>())) 
                    .orElseThrow(() -> new UsernameNotFoundException("Користувача з ID " + userId + " не знайдено"));
        } catch (NumberFormatException e) {
            
             return userRepo.findByUsername(idOrUsername)
                    .map(user -> new org.springframework.security.core.userdetails.User(
                            user.getUsername(),
                            user.getPassword(),
                            new ArrayList<>()))
                    .orElseThrow(() -> new UsernameNotFoundException("Користувача з іменем " + idOrUsername + " не знайдено"));
        }
    }
}