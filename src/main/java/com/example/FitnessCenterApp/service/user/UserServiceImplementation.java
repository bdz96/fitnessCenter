package com.example.FitnessCenterApp.service.user;

import com.example.FitnessCenterApp.controller.login.LoginRequest;
import com.example.FitnessCenterApp.model.UserDB;
import com.example.FitnessCenterApp.repository.UserRepository;
import com.example.FitnessCenterApp.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String login(LoginRequest request) {
        UserDB user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(String.valueOf(user.getId()));
    }
}
