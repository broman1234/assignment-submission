package com.manmo.assignmentsubmissionapp.util;

import com.manmo.assignmentsubmissionapp.domain.User;

public class AuthorityUtil {
    public static Boolean hasRole(String role, User user) {
        return user.getAuthorities()
                .stream()
                .filter(grantedAuthority -> grantedAuthority.getAuthority().equals(role))
                .count() > 0;
    }
}
