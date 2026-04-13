package com.faceid.dto;

public record FaceVerifyResponse(
        double similarity,
        boolean match,
        String message
) {}
