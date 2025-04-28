package com.example.financetracker.service;


import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.financetracker.model.User;
import com.example.financetracker.repository.UserRepository;

public class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void shouldReturnUser_whenUsernameExists() {
        String username = "testuser";
        String rawPassword = "password";
        String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword);

        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        User result = userService.authenticate(username, rawPassword);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    void shouldThrowException_whenUserNotFound() {
        when(userRepository.findByUsername("notfound")).thenReturn(Optional.empty());

        UsernameNotFoundException thrown = assertThrows(
            UsernameNotFoundException.class,
            () -> userService.authenticate("notfound", "incorrect")
        );

        assertEquals("This user: notfound is not found in db", thrown.getMessage());
    }
}
