package com.faceid.dto;

public record AuthResponse(
        String token,
        String username,
        String message
) {}
