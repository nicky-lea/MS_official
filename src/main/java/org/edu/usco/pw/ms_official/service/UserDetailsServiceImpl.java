package org.edu.usco.pw.ms_official.service;


import org.edu.usco.pw.ms_official.model.UserEntity;
import org.edu.usco.pw.ms_official.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + email + " no existe."));

        List<SimpleGrantedAuthority> roleList = new ArrayList<>();

        roleList.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

        return new User(user.getEmail(),
                user.getPassword(),
                roleList);
    }

}
