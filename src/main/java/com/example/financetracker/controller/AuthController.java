package com.example.financetracker.controller;

import com.example.financetracker.exception.ErrorResponse;
import com.example.financetracker.model.User;
import com.example.financetracker.security.JwtResponse;
import com.example.financetracker.security.JwtService;
import com.example.financetracker.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Operation(
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = JwtResponse.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody AuthRequest request) {
        User user = userService.register(request.getUsername(), request.getPassword());
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @Operation(
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = JwtResponse.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody AuthRequest request) {
        User user = userService.authenticate(request.getUsername(), request.getPassword());
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(token));
    }


    @Data
    public static class AuthRequest {
        @NotBlank(message = "Username must not be blank")
        @Pattern(
                regexp = "^[A-Za-z\\d@$!%*?&]{6,20}$",
                message = "Username must be 6-20 characters long and may contain letters, digits, and special characters"
        )
        private String username;

        @NotBlank(message = "Password must not be blank")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,20}$",
                message = "Password must be 6-20 characters, with at least one uppercase letter, one digit, and one special character"
        )
        private String password;
    }
}
