package com.pri1712.searchengine.model;

public class ChunkMetaData {
    private long dataOffset;
    private int dataLength;
    private int docId;
    public ChunkMetaData(long dataOffset, int dataLength, int docId) {
        this.dataOffset = dataOffset;
        this.dataLength = dataLength;
        this.docId = docId;
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
}
