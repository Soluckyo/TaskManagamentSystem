package org.lib.taskmanagamentsystem.service;

import lombok.RequiredArgsConstructor;
import org.lib.taskmanagamentsystem.dto.JwtRequestDTO;
import org.lib.taskmanagamentsystem.dto.JwtResponseDTO;
import org.lib.taskmanagamentsystem.dto.RefreshTokenDTO;
import org.lib.taskmanagamentsystem.dto.RegistrationUserDTO;
import org.lib.taskmanagamentsystem.entity.Role;
import org.lib.taskmanagamentsystem.entity.User;
import org.lib.taskmanagamentsystem.jwt.JwtUtils;
import org.lib.taskmanagamentsystem.repository.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public JwtResponseDTO signIn(JwtRequestDTO jwtRequestDTO) throws AuthenticationException {
        User user = findByCredentials(jwtRequestDTO);
        return jwtUtils.generateAuthToken(user);
    }

    public JwtResponseDTO refreshToken(RefreshTokenDTO refreshTokenDTO) throws Exception {
        String refreshToken = refreshTokenDTO.getRefreshToken();
        if(refreshToken == null && jwtUtils.validateJwtToken(refreshToken)){
            User user = findByEmail(jwtUtils.getEmailFromToken(refreshToken));
            return jwtUtils.refreshBaseToken(user, refreshToken);
        }throw new AuthenticationException("Invalid refresh token");
    }

    private User findByCredentials(JwtRequestDTO jwtRequestDTO) throws AuthenticationException {
        Optional<User> userOptional = userRepo.findByEmail(jwtRequestDTO.getEmail());
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            if(passwordEncoder.matches(jwtRequestDTO.getPassword(), user.getPassword())) {
                return user;
            }
        } throw new AuthenticationException("Email or password is not correct");
    }

    private User findByEmail(String email) throws Exception {
        return userRepo.findByEmail(email).orElseThrow(()-> new Exception(String.format("Email %s not found", email)));
    }

    public void setRole(Long userId, Role role){
        userRepo.findById(userId).ifPresent(user -> user.setRole(role));
    }

    public void addUser(RegistrationUserDTO registrationUserDTO) {
        User user = new User();
        user.setUsername(registrationUserDTO.getUsername());
        user.setEmail(registrationUserDTO.getEmail());
        user.setPassword(registrationUserDTO.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepo.save(user);
    }
}
