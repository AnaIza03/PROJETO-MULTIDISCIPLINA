package com.faceid.controller;

import com.faceid.dto.AuthResponse;
import com.faceid.service.AuthService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuthResponse> register(
            @RequestParam @NotBlank @Size(min = 3, max = 50) String username,
            @RequestParam @NotBlank @Email String email,
            @RequestParam @NotBlank @Size(min = 6, max = 100) String password,
            @RequestParam("faceImage") MultipartFile faceImage
    ) {
        AuthResponse response = authService.register(username, email, password, faceImage);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuthResponse> login(
            @RequestParam @NotBlank String username,
            @RequestParam @NotBlank String password,
            @RequestParam("faceImage") MultipartFile faceImage
    ) {
        AuthResponse response = authService.login(username, password, faceImage);
        return ResponseEntity.ok(response);
    }
}
