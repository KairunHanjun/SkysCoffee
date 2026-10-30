package org.kelompokwira.wirakopi.wirakopi.Service;

import org.kelompokwira.wirakopi.wirakopi.Entity.User;
import org.kelompokwira.wirakopi.wirakopi.Repository.WiraRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// AUTO GENERATED DO NOT EDIT (EDIT AT YOUR OWN RISK)

@org.springframework.context.annotation.Profile("!demo & !test")
@Service
@Transactional(readOnly = true)
public class UserDetailServicesImpl implements UserDetailsService{

    @Autowired
    private WiraRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = new User();
        if(repo.findByUsername(username).isEmpty()) throw new UsernameNotFoundException(username);
        user = repo.findByUsername(username).getFirst();
        System.out.println(user.getUsername());
        return user;
    }
}
