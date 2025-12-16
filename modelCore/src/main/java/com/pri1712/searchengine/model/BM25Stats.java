package com.pri1712.searchengine.model;

public class BM25Stats {
    private long totalChunks;
    private long totalTokens;
    private long averageChunkSize;
    public BM25Stats() {}

    public BM25Stats(long totalChunks, long totalTokens, long averageChunkSize) {
        this.totalChunks = totalChunks;
        this.totalTokens = totalTokens;
        this.averageChunkSize = averageChunkSize;
    }

    public long getAverageChunkSize() {
        return averageChunkSize;
    }

    public void setAverageChunkSize(long averageChunkSize) {
        this.averageChunkSize = averageChunkSize;
    }

    public long getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(long totalTokens) {
        this.totalTokens = totalTokens;
    }

    public long getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(long totalChunks) {
        this.totalChunks = totalChunks;
    }
}
