package com.portifolio.api.security;

import com.portifolio.api.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final AdminUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .map(admin -> new User(
                        admin.getUsername(),
                        admin.getPasswordHash(),
                        List.of(new SimpleGrantedAuthority(ROLE_ADMIN))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}