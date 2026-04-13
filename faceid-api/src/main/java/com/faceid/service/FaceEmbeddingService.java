package com.faceid.service;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import com.faceid.exception.FaceProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

@Service
public class FaceEmbeddingService {

    private static final Logger log = LoggerFactory.getLogger(FaceEmbeddingService.class);

    private final ZooModel<Image, float[]> faceModel;

    public FaceEmbeddingService(ZooModel<Image, float[]> faceModel) {
        this.faceModel = faceModel;
    }

    public float[] extractEmbedding(MultipartFile file) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null) {
                throw new FaceProcessingException("Invalid image file. Could not decode the uploaded image.");
            }

            Image djlImage = ImageFactory.getInstance().fromImage(bufferedImage);

            try (Predictor<Image, float[]> predictor = faceModel.newPredictor()) {
                float[] embedding = predictor.predict(djlImage);
                log.info("Face embedding extracted successfully. Dimensions: {}", embedding.length);
                return embedding;
            }

        } catch (IOException e) {
            throw new FaceProcessingException("Failed to read the uploaded image: " + e.getMessage());
        } catch (TranslateException e) {
            throw new FaceProcessingException("Model inference failed: " + e.getMessage());
        }
    }

    public float[] extractEmbedding(byte[] imageBytes) {
        try {
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(imageBytes);
            BufferedImage bufferedImage = ImageIO.read(bais);
            if (bufferedImage == null) {
                throw new FaceProcessingException("Invalid image bytes. Could not decode.");
            }

            Image djlImage = ImageFactory.getInstance().fromImage(bufferedImage);

            try (Predictor<Image, float[]> predictor = faceModel.newPredictor()) {
                return predictor.predict(djlImage);
            }

        } catch (IOException | TranslateException e) {
            throw new FaceProcessingException("Embedding extraction failed: " + e.getMessage());
        }
    }
}
