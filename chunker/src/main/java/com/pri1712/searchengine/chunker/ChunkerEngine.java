package com.pri1712.searchengine.chunker;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pri1712.searchengine.utils.WikiDocument;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class ChunkerEngine {
    private int chunkSize;
    private int chunkOverlap;
    private String chunkDataFile;
    private String chunkIndexFile;
    ObjectMapper mapper = new ObjectMapper().configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)
            .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);

    public ChunkerEngine(int chunkSize, int chunkOverlap, String chunkDataFile, String chunkIndexFile) {
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
        this.chunkDataFile = chunkDataFile;
        this.chunkIndexFile = chunkIndexFile;
    }

    public void processFile(Path parsedFile) throws IOException {
        //process the file here. chunk it based on the params and store the chunked data in chunkId -> file offset manner.
        String file = parsedFile.toString();
        try (FileInputStream fis = new FileInputStream(parsedFile.toFile());
             GZIPInputStream gis = new GZIPInputStream(fis);
             BufferedReader buffRead = new BufferedReader(new InputStreamReader(gis))) {

            List<WikiDocument> jsonDocuments = mapper.readValue(buffRead, new TypeReference<>() {
            });
            for(WikiDocument wikiDocument : jsonDocuments) {
                chunkText(wikiDocument.getTitle(), wikiDocument.getId());
                chunkText(wikiDocument.getText(), wikiDocument.getId());
            }
        }
    }

    private void chunkText(String text, String docId) {
        //actual chunking happens here.
        //how do i store chunks?????
        String[] words = text.split("\\s+");
        int slidingWindowSize = chunkSize - chunkOverlap;

        for (int i = 0; i < words.length; i+=slidingWindowSize) {
            int end = Math.min(words.length, i + slidingWindowSize);
            String[] chunkWords = java.util.Arrays.copyOfRange(words, i, end);
            String chunkText = String.join(" ", chunkWords);

        }

    }
}
