package com.pri1712.searchengine.model.params;

public class ParsingParams {
    private static int MAX_DOCS_TO_PROCESS;
    private static int MAX_BATCH_SIZE;
    public ParsingParams(int MAX_DOCS_TO_PROCESS, int MAX_BATCH_SIZE) {
        ParsingParams.MAX_DOCS_TO_PROCESS = MAX_DOCS_TO_PROCESS;
        ParsingParams.MAX_BATCH_SIZE = MAX_BATCH_SIZE;
    }

    public static int getMaxBatchSize() {
        System.out.println("Max batch size in class: " + ParsingParams.MAX_BATCH_SIZE);
        return MAX_BATCH_SIZE;
    }

    public static int getMaxDocsToProcess() {
        System.out.println("Max docs to process in class: " + ParsingParams.MAX_DOCS_TO_PROCESS);
        return MAX_DOCS_TO_PROCESS;
    }
}
