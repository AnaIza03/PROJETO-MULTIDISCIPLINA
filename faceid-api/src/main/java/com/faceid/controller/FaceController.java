package com.faceid.controller;

import com.faceid.dto.FaceVerifyResponse;
import com.faceid.service.AuthService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/face")
public class FaceController {

    private final AuthService authService;

    public FaceController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FaceVerifyResponse> verifyFace(
            @RequestParam("faceImage") MultipartFile faceImage,
            Authentication authentication
    ) {
        String username = authentication.getName();
        FaceVerifyResponse response = authService.verifyFace(username, faceImage);
        return ResponseEntity.ok(response);
    }
}
