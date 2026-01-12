package com.barca.fanclub_api.auth;

import com.barca.fanclub_api.auth.dto.AuthResponse;
import com.barca.fanclub_api.auth.dto.LoginRequest;
import com.barca.fanclub_api.auth.dto.RegisterRequest;
import com.barca.fanclub_api.model.UserRole;
import com.barca.fanclub_api.model.User;
import com.barca.fanclub_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.emailExists(req.email().toLowerCase())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User u = new User(req.email().toLowerCase(), req.name(), passwordEncoder.encode(req.password()), UserRole.USER);

        u = userRepository.save(u);

        String token = jwtService.generateToken(u.getId(), u.getEmail(), u.getRole());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest req) {
        User u = userRepository.findUserByEmail(req.email().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), u.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtService.generateToken(u.getId(), u.getEmail(), u.getRole());
        return new AuthResponse(token);
    }
}
