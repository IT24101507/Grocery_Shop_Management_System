package Ravindra.Stores.Ravindra_Stores_backend.services;

import Ravindra.Stores.Ravindra_Stores_backend.User;
import Ravindra.Stores.Ravindra_Stores_backend.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String gmail) throws UsernameNotFoundException {
        User user = userRepository.findByGmail(gmail).orElseThrow(() -> new UsernameNotFoundException("User not found with gmail: " + gmail));
        if (!user.isEnabled()) {
            throw new UsernameNotFoundException("User is not enabled");
        }
        if (!user.isVerified()) {
            throw new UsernameNotFoundException("User is not verified");
        }
        return new org.springframework.security.core.userdetails.User(user.getGmail(), user.getPassword() != null ? user.getPassword() : "", Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
    }
}
