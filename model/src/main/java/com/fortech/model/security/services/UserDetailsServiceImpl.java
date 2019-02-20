package com.fortech.model.security.services;

import com.fortech.model.entity.User;
import com.fortech.model.enums.Role;
import com.fortech.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository applicationUserRepository) {
        this.userRepository = applicationUserRepository;
    }

    public UserRepository getUserEntityOnSignIn() {
        return this.userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {

        User applicationUser = userRepository.findByUsername(username);

        if (applicationUser == null) {
            throw new UsernameNotFoundException(username);
        }

        return new org.springframework.security.core.userdetails.User(applicationUser.getUsername(), applicationUser.getPassword(), this.getAuthorities(applicationUser));
    }

    private Collection<GrantedAuthority> getAuthorities(User applicationUser) {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(applicationUser.getRole().name()));
        int currentUserPriority = applicationUser.getRole().getPriority();
        for (Role role : Role.values()) {
            if (currentUserPriority < role.getPriority() && currentUserPriority > 0) {
                grantedAuthorities.add(new SimpleGrantedAuthority(role.name()));
            }
        }
        return grantedAuthorities;
    }
}
