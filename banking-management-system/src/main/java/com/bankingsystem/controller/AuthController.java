package com.bankingsystem.controller;

import com.bankingsystem.dto.request.CustomerRegistrationRequest;
import com.bankingsystem.dto.request.EmployeeRegistrationRequest;
import com.bankingsystem.dto.request.LoginRequest;
import com.bankingsystem.dto.response.AuthResponse;
import com.bankingsystem.dto.response.MessageResponse;
import com.bankingsystem.entity.User;
import com.bankingsystem.service.AuthService;
import com.bankingsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid username or password"));
        }
    }

    @PostMapping("/register/customer")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody CustomerRegistrationRequest request) {
        try {
            User user = userService.registerCustomer(request);
            return ResponseEntity.ok(new MessageResponse("Customer registered successfully! Username: " + user.getUsername()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/register/employee")
    public ResponseEntity<?> registerEmployee(@Valid @RequestBody EmployeeRegistrationRequest request) {
        try {
            User user = userService.registerEmployee(request);
            return ResponseEntity.ok(new MessageResponse("Employee registered successfully! Username: " + user.getUsername()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}