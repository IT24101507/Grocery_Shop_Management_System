package Ravindra.Stores.Ravindra_Stores_backend.services;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import Ravindra.Stores.Ravindra_Stores_backend.User;

public class MyUserDetails implements UserDetails {

    private final String username; 
    private final String password;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Long id; 

    public MyUserDetails(User user) {
        this.username = user.getGmail();
        this.password = user.getPassword() != null ? user.getPassword() : "";
        this.enabled = user.isEnabled() && user.isVerified();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));
        this.id = user.getId(); 
    }

    
    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}