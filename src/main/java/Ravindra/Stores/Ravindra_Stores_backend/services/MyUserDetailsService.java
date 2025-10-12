package Ravindra.Stores.Ravindra_Stores_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import Ravindra.Stores.Ravindra_Stores_backend.User;
import Ravindra.Stores.Ravindra_Stores_backend.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String gmail) throws UsernameNotFoundException {
        User user = userRepository.findByGmail(gmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with gmail: " + gmail));
        
        if (!user.isEnabled() || !user.isVerified()) {
            
            throw new UsernameNotFoundException("User account is not active or verified.");
        }
        
        
        return new MyUserDetails(user);
    }
}
