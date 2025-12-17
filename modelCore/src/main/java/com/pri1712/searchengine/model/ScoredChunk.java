package com.pri1712.searchengine.model;

public class ScoredChunk {
    private long score;
    private String chunkId;
    public ScoredChunk(long score, String chunkId) {
        this.score = score;
        this.chunkId = chunkId;
    }
}
