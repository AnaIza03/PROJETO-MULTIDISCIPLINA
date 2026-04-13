package com.faceid.service;

import com.faceid.dto.AuthResponse;
import com.faceid.dto.FaceVerifyResponse;
import com.faceid.entity.User;
import com.faceid.exception.FaceVerificationException;
import com.faceid.exception.ResourceNotFoundException;
import com.faceid.repository.UserRepository;
import com.faceid.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final FaceEmbeddingService faceEmbeddingService;
    private final CosineSimilarityService cosineSimilarityService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       FaceEmbeddingService faceEmbeddingService,
                       CosineSimilarityService cosineSimilarityService,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.faceEmbeddingService = faceEmbeddingService;
        this.cosineSimilarityService = cosineSimilarityService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(String username, String email, String password, MultipartFile faceImage) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username '%s' is already taken.".formatted(username));
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email '%s' is already registered.".formatted(email));
        }

        float[] embedding = faceEmbeddingService.extractEmbedding(faceImage);

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();
        user.setFaceVector(embedding);

        userRepository.save(user);
        log.info("User registered successfully: {}", username);

        String token = jwtService.generateToken(username);
        return new AuthResponse(token, username, "Registration successful. Welcome, %s!".formatted(username));
    }

    public AuthResponse login(String username, String password, MultipartFile faceImage) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password for user: " + username);
        }

        float[] liveEmbedding = faceEmbeddingService.extractEmbedding(faceImage);
        float[] storedEmbedding = user.getFaceVector();

        double similarity = cosineSimilarityService.calculate(storedEmbedding, liveEmbedding);
        log.info("Face verification for '{}': similarity = {}, threshold = {}",
                username, String.format("%.4f", similarity), cosineSimilarityService.getThreshold());

        if (similarity < cosineSimilarityService.getThreshold()) {
            throw new FaceVerificationException(
                    "Face verification failed. Similarity: %.4f (required: >= %.2f)"
                            .formatted(similarity, cosineSimilarityService.getThreshold())
            );
        }

        String token = jwtService.generateToken(username);
        return new AuthResponse(token, username,
                "Login successful. Face similarity: %.4f".formatted(similarity));
    }

    public FaceVerifyResponse verifyFace(String username, MultipartFile faceImage) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        float[] liveEmbedding = faceEmbeddingService.extractEmbedding(faceImage);
        float[] storedEmbedding = user.getFaceVector();

        double similarity = cosineSimilarityService.calculate(storedEmbedding, liveEmbedding);
        boolean match = similarity >= cosineSimilarityService.getThreshold();

        return new FaceVerifyResponse(
                similarity,
                match,
                match ? "Face verified successfully." : "Face does not match."
        );
    }
}
