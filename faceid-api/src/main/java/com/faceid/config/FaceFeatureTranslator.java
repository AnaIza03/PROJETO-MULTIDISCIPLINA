package com.faceid.config;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

/**
 * Translates a DJL Image into a 512-dimensional face embedding float array.
 * Preprocessing mirrors the FaceNet/InceptionResnetV1 pipeline:
 * resize to 160x160, normalize pixel values to [-1, 1].
 */
public class FaceFeatureTranslator implements Translator<Image, float[]> {

    private static final int IMAGE_SIZE = 160;

    @Override
    public NDList processInput(TranslatorContext ctx, Image input) {
        NDArray array = input.toNDArray(ctx.getNDManager(), Image.Flag.COLOR);
        array = NDImageUtils.resize(array, IMAGE_SIZE, IMAGE_SIZE);
        array = array.transpose(2, 0, 1).div(255.0f);
        array = array.sub(0.5f).div(0.5f);
        array = array.expandDims(0);
        return new NDList(array);
    }

    @Override
    public float[] processOutput(TranslatorContext ctx, NDList list) {
        NDArray embedding = list.singletonOrThrow();
        if (embedding.getShape().dimension() > 1) {
            embedding = embedding.squeeze(0);
        }
        return embedding.toFloatArray();
    }

    @Override
    public Batchifier getBatchifier() {
        return null;
    }
}
