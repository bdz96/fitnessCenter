package com.example.FitnessCenterApp.controller;

import com.example.FitnessCenterApp.controller.login.LoginRequest;
import com.example.FitnessCenterApp.controller.login.TokenResponse;
import com.example.FitnessCenterApp.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request);
        return ResponseEntity.ok(new TokenResponse(token));
    }
}