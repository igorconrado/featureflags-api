package com.featureflags.config;

import com.featureflags.auth.User;
import com.featureflags.auth.UserRole;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthHelper {

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public UserRole getCurrentUserRole() {
        return getCurrentUser().getRole();
    }
}
