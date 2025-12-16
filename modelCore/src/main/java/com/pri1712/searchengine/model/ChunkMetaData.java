package com.pri1712.searchengine.model;

public class ChunkMetaData {
    private long dataOffset;
    private int dataLength;
    private int docId;
    private int tokenCount;
    public ChunkMetaData(long dataOffset, int dataLength, int docId, int tokenCount) {
        this.dataOffset = dataOffset;
        this.dataLength = dataLength;
        this.docId = docId;
        this.tokenCount = tokenCount;
    }

    public long getDataOffset() {
        return dataOffset;
    }

    public int getDataLength() {
        return dataLength;
    }

    public int getDocId() {
        return docId;
    }

    public int getTokenCount() {
        return tokenCount;
    }
}
