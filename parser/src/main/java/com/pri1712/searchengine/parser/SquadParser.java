package com.pri1712.searchengine.parser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pri1712.searchengine.model.ParsedDocument;
import com.pri1712.searchengine.model.params.ParsingParams;
import com.pri1712.searchengine.utils.BatchFileWriter;
import com.pri1712.searchengine.utils.TextUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/***
 * Class for parsing squad json format.
 */
public class SquadParser implements DocumentParser {

    private String dataFilePath;
    private final String outputDir;
    private final String parserBatchCheckpointFile = "squadParserCheckpoint.txt";
    private final CheckpointManager checkpointManager;
    private int MAX_DOCS_TO_PROCESS;
    private int MAX_BATCH_SIZE;
    ObjectMapper mapper = new ObjectMapper()
            .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)
            .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
    private boolean enableCheckpoint;
    private BatchFileWriter batchWriter;

    public SquadParser(String dataFilePath, boolean enableCheckpoint, String outputDir) {
        this.dataFilePath = dataFilePath;
        this.enableCheckpoint = enableCheckpoint;
        this.outputDir = outputDir;
        this.MAX_DOCS_TO_PROCESS = ParsingParams.getMaxDocsToProcess();
        this.MAX_BATCH_SIZE = ParsingParams.getMaxBatchSize();
        this.batchWriter = new BatchFileWriter(outputDir);

        this.checkpointManager = enableCheckpoint ?
                new CheckpointManager(parserBatchCheckpointFile) : null;
    }

    @Override
    public List<ParsedDocument> parse() throws IOException {
        List<ParsedDocument> writeBuffer = new ArrayList<>();

        if (dataFilePath == null || dataFilePath.isEmpty()) {
            throw new FileNotFoundException();
        }

        int parseBatchCounter = 0;
        int previousParseBatchCounter = -1;
        if (enableCheckpoint) {
            previousParseBatchCounter = checkpointManager.readCheckpointBatch();
            System.out.println("Resuming from batch: " + previousParseBatchCounter);
        }

        try (Reader reader = new FileReader(dataFilePath)) {
            JsonNode root = mapper.readTree(reader);
            JsonNode data = root.get("data");

            Integer docId = 0;
            for (JsonNode article : data) {
                String title = article.get("title").asText();
                JsonNode paragraphs = article.get("paragraphs");

                for (JsonNode para : paragraphs) {
                    String context = para.get("context").asText();

                    StringBuilder cleanTitle = TextUtils.lowerCaseText(new StringBuilder(title));
                    StringBuilder cleanContext = TextUtils.lowerCaseText(new StringBuilder(context));

                    if (cleanContext.isEmpty()) {
                        docId++;
                        continue;
                    }

                    ParsedDocument doc = new ParsedDocument(
                            docId.toString(),
                            cleanTitle.toString(),
                            cleanContext.toString()
                    );
                    doc.addMetadata("source", "squad");
                    doc.addMetadata("article_title", cleanTitle.toString());

                    writeBuffer.add(doc);
                    if (writeBuffer.size() >= MAX_BATCH_SIZE) {
                        List<ParsedDocument> batchToWrite = new ArrayList<>(writeBuffer);
                        writeBuffer.clear();
                        if (!enableCheckpoint || parseBatchCounter > previousParseBatchCounter) {
                            batchWriter.writeBatch(batchToWrite, parseBatchCounter);
                            System.out.println("Written batch " + parseBatchCounter + " to disk");
                        } else {
                            System.out.println("Skipping already processed batch " + parseBatchCounter);
                        }
                        if (enableCheckpoint) {
                            checkpointManager.writeCheckpointBatch(parseBatchCounter);
                            System.out.println("Checkpoint updated to batch: " + parseBatchCounter);
                        }

                        parseBatchCounter++;
                    }
                    if (MAX_DOCS_TO_PROCESS >= 0 && docId >= MAX_DOCS_TO_PROCESS) {
                        // Write remaining buffer to disk
                        if (!writeBuffer.isEmpty()) {
                            if (!enableCheckpoint || parseBatchCounter > previousParseBatchCounter) {
                                batchWriter.writeBatch(writeBuffer, parseBatchCounter);
                                System.out.println("Written final batch " + parseBatchCounter + " to disk");
                            }

                            if (enableCheckpoint) {
                                checkpointManager.writeCheckpointBatch(parseBatchCounter);
                                System.out.println("Final checkpoint updated to batch: " + parseBatchCounter);
                            }
                        }
                        System.out.println("Reached max docs limit: " + MAX_DOCS_TO_PROCESS);
                        return new ArrayList<>();
                    }

                    docId++;
                }
            }
            if (!writeBuffer.isEmpty()) {
                if (!enableCheckpoint || parseBatchCounter > previousParseBatchCounter) {
                    batchWriter.writeBatch(writeBuffer, parseBatchCounter);
                    System.out.println("Written final batch " + parseBatchCounter + " to disk");
                }

                if (enableCheckpoint) {
                    checkpointManager.writeCheckpointBatch(parseBatchCounter);
                    System.out.println("Final checkpoint updated to batch: " + parseBatchCounter);
                }
            }
        }

        System.out.println("Parsing completed. All data written to: " + outputDir);
        return new ArrayList<>();
    }

    @Override
    public String getParserName() {
        return "SQuAD JSON Parser";
    }
}