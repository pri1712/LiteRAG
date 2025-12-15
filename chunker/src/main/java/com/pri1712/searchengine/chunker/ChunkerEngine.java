package com.pri1712.searchengine.chunker;

import java.nio.file.Path;

public class ChunkerEngine {
    private int chunkSize;
    private int chunkOverlap;
    public ChunkerEngine(int chunkSize, int chunkOverlap) {
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
    }

    public void processFile(Path parsedFile) {
        //process the file here. chunk it based on the params and store the chunked data in chunkId-> file offset manner.
    }
}
