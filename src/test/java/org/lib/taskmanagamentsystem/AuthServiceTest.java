package org.lib.taskmanagamentsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lib.taskmanagamentsystem.dto.JwtRequestDTO;
import org.lib.taskmanagamentsystem.dto.JwtResponseDTO;
import org.lib.taskmanagamentsystem.dto.RefreshTokenDTO;
import org.lib.taskmanagamentsystem.dto.RegistrationUserDTO;
import org.lib.taskmanagamentsystem.entity.Role;
import org.lib.taskmanagamentsystem.entity.User;
import org.lib.taskmanagamentsystem.jwt.JwtUtils;
import org.lib.taskmanagamentsystem.repository.UserRepo;
import org.lib.taskmanagamentsystem.service.AuthService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.AuthenticationException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    JwtUtils jwtUtils;

    @Mock
    UserRepo userRepo;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    AuthService authService;

    private User user;

    JwtRequestDTO jwtRequestDTO;
    JwtResponseDTO jwtResponseDTO;
    RefreshTokenDTO refreshTokenDTO;
    RegistrationUserDTO registrationUserDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("user@mail.com");
        user.setPassword("password");
        user.setRole(Role.USER);
        jwtRequestDTO = new JwtRequestDTO("user@mail.com", "password");
        jwtResponseDTO = new JwtResponseDTO("accessToken", "refreshToken");

        refreshTokenDTO = new RefreshTokenDTO("validRefreshToken");

        registrationUserDTO = new RegistrationUserDTO("newUser", "newUser@mail.com", "newPassword");
    }

    @Test
    void signInTestSuccessfully() throws AuthenticationException {
        when(userRepo.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", user.getPassword())).thenReturn(true);
        when(jwtUtils.generateAuthToken(user)).thenReturn(jwtResponseDTO);

        JwtResponseDTO responseDTO =authService.signIn(jwtRequestDTO);

        assertNotNull(responseDTO);
        assertEquals("accessToken", responseDTO.getAccessToken());
        assertEquals("refreshToken", responseDTO.getRefreshToken());
    }

    @Test
    void signInTestFailure() throws AuthenticationException {
        when(userRepo.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("otherPassword", user.getPassword())).thenReturn(false);

        assertThrows(AuthenticationException.class,
                () -> authService.signIn(new JwtRequestDTO("user@mail.com", "otherPassword")));
    }


    @Test
    void throwAuthenticationExceptionWhenTokenIsInvalid() {
        RefreshTokenDTO invalidToken = new RefreshTokenDTO(null);
        assertThrows(AuthenticationException.class,
                () -> authService.refreshToken(invalidToken));
    }

    @Test
    void refreshTokenSuccessfullyWhenValidToken() throws Exception {
        String validRefreshToken = "validRefreshToken";
        when(jwtUtils.validateJwtToken(validRefreshToken)).thenReturn(true);
        when(jwtUtils.getEmailFromToken(validRefreshToken)).thenReturn("user@mail.com");
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(new User()));
        when(jwtUtils.refreshBaseToken(any(User.class), eq(validRefreshToken))).thenReturn(new JwtResponseDTO());

        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO(validRefreshToken);
        JwtResponseDTO response = authService.refreshToken(refreshTokenDTO);

        assertNotNull(response);
    }


    @Test
    void addUserSuccessfully() {
        when(userRepo.findByEmail("newUser@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        authService.addUser(registrationUserDTO);

        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void throwExceptionWhenUserAlreadyExists() {
        when(userRepo.findByEmail("newUser@mail.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> authService.addUser(registrationUserDTO));
    }

}
