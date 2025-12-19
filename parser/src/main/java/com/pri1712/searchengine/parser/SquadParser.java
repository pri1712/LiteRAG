package com.pri1712.searchengine.parser;

import com.pri1712.searchengine.model.ParsedDocument;
import com.pri1712.searchengine.utils.BatchFileWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/***
 * Class for parsing squad json format.
 */
public class SquadParser implements DocumentParser {

    private String dataFilePath;
    private final BatchFileWriter batchFileWriter = new BatchFileWriter("data/parsed-data/");
    private int previousParseBatchCounter = 0;
    private final String parserBatchCheckpointFile = "parserCheckpoint.txt";
    private final CheckpointManager checkpointManager = new CheckpointManager(parserBatchCheckpointFile);
    private int MAX_DOCS_TO_PROCESS;
    public SquadParser(String dataFilePath,int MAX_DOCS_TO_PROCESS) {
        this.dataFilePath = dataFilePath;
        this.MAX_DOCS_TO_PROCESS = MAX_DOCS_TO_PROCESS;
        this.previousParseBatchCounter = checkpointManager.readCheckpointBatch();
    }
    @Override
    public List<ParsedDocument> parse() throws IOException {
        if (dataFilePath == null || dataFilePath.isEmpty()) {
            throw new FileNotFoundException();
        }
    }

    @Override
    public String getParserName() {
        return "";
    }
}
