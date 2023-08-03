package com.limethecoder.data.service.impl;


import com.limethecoder.data.domain.Role;
import com.limethecoder.data.domain.User;
import com.limethecoder.data.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MyUserDetailsSevice implements UserDetailsService {
    private final static String ROLE_PREFIX = "ROLE_";

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userService.findOne(login);
        List<GrantedAuthority> authorities = new ArrayList<>();

        if(user == null) {
            throw new UsernameNotFoundException("No user with such login");
        }

        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(
                    ROLE_PREFIX + role.getName()));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(), user.getPassword(), user.isEnabled(),
                true, true, true, authorities);
    }
}
