package com.pri1712.searchengine.chunker;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class Chunker {
    private int chunkSize;
    private int chunkOverlap;
    String parsedFilePath;

    public Chunker(int chunkSize, int chunkOverlap, String parsedFilePath) {
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
        this.parsedFilePath = parsedFilePath;
    }

    public void startChunking() throws IOException {
        FileInputStream fis = new FileInputStream(parsedFilePath);
        GZIPInputStream gis = new GZIPInputStream(fis);
        BufferedInputStream bis = new BufferedInputStream(gis);

        //read from the parsed data and then chunk and store them.

    }

    public int getChunkOverlap() {
        return chunkOverlap;
    }

    public void setChunkOverlap(int chunkOverlap) {
        this.chunkOverlap = chunkOverlap;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }
}
