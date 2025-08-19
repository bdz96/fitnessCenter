package com.example.FitnessCenterApp.service.user;

import com.example.FitnessCenterApp.controller.login.LoginRequest;
import com.example.FitnessCenterApp.controller.login.TokenResponse;

public interface UserService {

    TokenResponse login(LoginRequest request);
}
