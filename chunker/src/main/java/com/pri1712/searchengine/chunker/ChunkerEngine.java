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

    ObjectMapper mapper = new ObjectMapper().configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)
            .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);

    public ChunkerEngine(int chunkSize, int chunkOverlap) {
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
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
                chunkText(wikiDocument.getTitle());
                chunkText(wikiDocument.getText());
            }
        }
    }

    private void chunkText(String text) {

    }
}
