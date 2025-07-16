package com.tasktracker.notification.security;

import java.security.Principal;

public class UserPrincipal implements Principal {
    
    private final Long id;
    private final String username;
    private final String role;
    
    public UserPrincipal(Long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
    
    @Override
    public String getName() {
        return username;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getRole() {
        return role;
    }
    
    @Override
    public String toString() {
        return "UserPrincipal{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
} 