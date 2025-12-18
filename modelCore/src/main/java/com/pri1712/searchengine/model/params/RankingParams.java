package com.pri1712.searchengine.model.params;

public class RankingParams {
    private static double TERM_FREQUENCY_SATURATION;
    private static double DOCUMENT_LENGTH_NORMALIZATION;

    public RankingParams(double termFrequency, double documentLength) {
        TERM_FREQUENCY_SATURATION = termFrequency;
        DOCUMENT_LENGTH_NORMALIZATION = documentLength;
    }

    public static double getTERM_FREQUENCY_SATURATION() {
        return TERM_FREQUENCY_SATURATION;
    }

    public static double getDOCUMENT_LENGTH_NORMALIZATION() {
        return DOCUMENT_LENGTH_NORMALIZATION;
    }
}
