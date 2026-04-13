package com.faceid.config;

import ai.djl.MalformedModelException;
import ai.djl.modality.cv.Image;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Path;

@Configuration
public class DjlModelConfig {

    private static final Logger log = LoggerFactory.getLogger(DjlModelConfig.class);

    @Value("${faceid.model-path}")
    private String modelPath;

    private ZooModel<Image, float[]> model;

    @Bean
    public ZooModel<Image, float[]> faceModel() throws ModelNotFoundException, MalformedModelException, IOException {
        log.info("Loading FaceNet TorchScript model from: {}", modelPath);

        Criteria<Image, float[]> criteria = Criteria.builder()
                .setTypes(Image.class, float[].class)
                .optModelPath(Path.of(modelPath))
                .optTranslator(new FaceFeatureTranslator())
                .optEngine("PyTorch")
                .build();

        model = criteria.loadModel();
        log.info("FaceNet model loaded successfully. Ready for inference.");
        return model;
    }

    @PreDestroy
    public void cleanup() {
        if (model != null) {
            model.close();
            log.info("FaceNet model resources released.");
        }
    }
}
