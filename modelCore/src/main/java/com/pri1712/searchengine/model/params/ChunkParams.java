package com.pri1712.searchengine.model.params;

public class ChunkParams {
    private static int CHUNK_SIZE;
    private static int CHUNK_OVERLAP;
    public ChunkParams(int chunkSize, int chunkOverlap) {
        CHUNK_SIZE = chunkSize;
        CHUNK_OVERLAP = chunkOverlap;
    }
    public static int getChunkSize() {
        return CHUNK_SIZE;
    }

    public static int getChunkOverlap() {
        return CHUNK_OVERLAP;
    }
}
