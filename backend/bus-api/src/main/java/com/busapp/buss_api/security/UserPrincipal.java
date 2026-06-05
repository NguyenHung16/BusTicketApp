package com.busapp.buss_api.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements Principal {
    private Integer userId;
    private String email;
    private String role;

    @Override
    public String getName() {
        return email;
    }
}
