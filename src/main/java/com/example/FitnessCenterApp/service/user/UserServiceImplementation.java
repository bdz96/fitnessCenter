package com.example.FitnessCenterApp.service.user;

import com.example.FitnessCenterApp.controller.login.LoginRequest;
import com.example.FitnessCenterApp.controller.login.TokenResponse;
import com.example.FitnessCenterApp.model.UserDB;
import com.example.FitnessCenterApp.repository.UserRepository;
import com.example.FitnessCenterApp.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImplementation(UserRepository userRepository,
                                     JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        UserDB user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtUtil.generateToken(Long.valueOf(String.valueOf(user.getId())));
        return new TokenResponse(token);
    }

    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
