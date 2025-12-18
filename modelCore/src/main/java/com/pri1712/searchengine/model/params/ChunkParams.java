package com.pri1712.searchengine.model.params;

public class ChunkParams {
    private static int CHUNK_SIZE;
    private static int CHUNK_OVERLAP;
    private static int MIN_CHUNK_LENGTH;
    public ChunkParams(int chunkSize, int chunkOverlap, int minChunkLength) {
        CHUNK_SIZE = chunkSize;
        CHUNK_OVERLAP = chunkOverlap;
        MIN_CHUNK_LENGTH = minChunkLength;
    }
    public static int getChunkSize() {
        return CHUNK_SIZE;
    }

    public static int getChunkOverlap() {
        return CHUNK_OVERLAP;
    }

    public static int getMinChunkLength() {
        return MIN_CHUNK_LENGTH;
    }
}
