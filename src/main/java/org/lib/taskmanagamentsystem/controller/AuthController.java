package org.lib.taskmanagamentsystem.controller;


import lombok.RequiredArgsConstructor;
import org.lib.taskmanagamentsystem.dto.JwtRequestDTO;
import org.lib.taskmanagamentsystem.dto.JwtResponseDTO;
import org.lib.taskmanagamentsystem.dto.RefreshTokenDTO;
import org.lib.taskmanagamentsystem.dto.RegistrationUserDTO;
import org.lib.taskmanagamentsystem.entity.User;
import org.lib.taskmanagamentsystem.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign_in")
    public ResponseEntity<JwtResponseDTO> signIn(@RequestBody JwtRequestDTO jwtRequestDTO){
        try{
            JwtResponseDTO jwtResponseDTO = authService.signIn(jwtRequestDTO);
            return ResponseEntity.ok(jwtResponseDTO);
        }catch (AuthenticationException e){
            throw new RuntimeException("Authentication failed" + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDTO> refresh(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        try{
            JwtResponseDTO jwtResponseDTO = authService.refreshToken(refreshTokenDTO);
            return ResponseEntity.ok(jwtResponseDTO);
        }catch (Exception e){
            throw new RuntimeException("failed" + e.getMessage());
        }
    }

    @PostMapping("/add_user")
    public ResponseEntity<String> addUser(@RequestBody RegistrationUserDTO registrationUserDTO) {
        User user = new User();
        user.setUsername(registrationUserDTO.getUsername());
        user.setEmail(registrationUserDTO.getEmail());
        user.setPassword(registrationUserDTO.getPassword());

        authService.addUser(user);
        return ResponseEntity.ok("User added successfully");
    }
}

