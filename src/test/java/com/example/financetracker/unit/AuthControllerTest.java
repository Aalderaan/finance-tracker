package com.example.financetracker.unit;

import com.example.financetracker.model.User;
import com.example.financetracker.repository.UserRepository;
import com.example.financetracker.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AuthControllerTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void authenticate_shouldReturnUser_whenCredentialsAreValid() {
        String username = "testuser";
        String rawPassword = "password";
        String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword);
        User user = User.builder().username(username).password(encodedPassword).build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        User result = userService.authenticate(username, rawPassword);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    void authenticate_shouldThrowException_whenPasswordIsInvalid() {
        String username = "testuser";
        String encodedPassword = new BCryptPasswordEncoder().encode("correct_password");
        User user = User.builder().username(username).password(encodedPassword).build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_password", encodedPassword)).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> {
            userService.authenticate(username, "wrong_password");
        });
    }
}
