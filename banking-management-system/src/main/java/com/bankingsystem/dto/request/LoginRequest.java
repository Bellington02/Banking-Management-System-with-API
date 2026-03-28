package com.bankingsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank (message = "Username is required")
    private String username;

    @NotBlank (message = "Password is required")
    private String password;
}
