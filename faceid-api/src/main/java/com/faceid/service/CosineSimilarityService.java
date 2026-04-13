package com.faceid.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Manual implementation of Cosine Similarity for educational clarity.
 * Formula: cos(A, B) = (A . B) / (||A|| * ||B||)
 */
@Service
public class CosineSimilarityService {

    @Value("${faceid.similarity-threshold}")
    private double threshold;

    public double calculate(float[] vectorA, float[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException(
                    "Vectors must have the same dimensions. Got %d and %d".formatted(vectorA.length, vectorB.length)
            );
        }

        double dotProduct = 0.0;
        double magnitudeA = 0.0;
        double magnitudeB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += (double) vectorA[i] * vectorB[i];
            magnitudeA += (double) vectorA[i] * vectorA[i];
            magnitudeB += (double) vectorB[i] * vectorB[i];
        }

        magnitudeA = Math.sqrt(magnitudeA);
        magnitudeB = Math.sqrt(magnitudeB);

        if (magnitudeA == 0.0 || magnitudeB == 0.0) {
            return 0.0;
        }

        return dotProduct / (magnitudeA * magnitudeB);
    }

    public boolean isMatch(float[] vectorA, float[] vectorB) {
        return calculate(vectorA, vectorB) >= threshold;
    }

    public double getThreshold() {
        return threshold;
    }
}
