package com.pri1712.searchengine.wikiquerying;

import com.pri1712.searchengine.indexreader.IndexData;
import com.pri1712.searchengine.utils.TextUtils;
import com.pri1712.searchengine.indexreader.IndexReader;
import com.pri1712.searchengine.model.ChunkMetaData;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.min;

public class QueryEngine {
    private static final Logger LOGGER = Logger.getLogger(String.valueOf(QueryEngine.class));
    private String invertedIndex;
    private String docStats;
    private String tokenIndexOffset;
    private IndexReader indexReader;
    private Path indexedFilePath;
    private final int TOP_K;
    private String chunkDataFilePath;
    private String chunkIndexFilePath;
    private final int RECORD_SIZE;
    private final RandomAccessFile chunkIndexFile;
    private final RandomAccessFile chunkDataFile;

    public QueryEngine(String invertedIndex, String docStats, String tokenIndexOffset, int TOP_K, String chunkDataFilePath, String chunkIndexFilePath, int RECORD_SIZE) throws IOException {
        this.invertedIndex = invertedIndex;
        this.docStats = docStats;
        this.tokenIndexOffset = tokenIndexOffset;
        this.TOP_K = TOP_K;
        Path indexDirectory = Paths.get(invertedIndex);
        this.indexedFilePath = Files.list(indexDirectory)
                .filter(p -> p.getFileName().toString().endsWith("_delta_encoded.json"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("no inverted index found"));
        this.chunkDataFilePath = chunkDataFilePath;
        this.chunkIndexFilePath = chunkIndexFilePath;
        this.RECORD_SIZE = RECORD_SIZE;
        this.chunkIndexFile = new RandomAccessFile(chunkIndexFilePath, "r");
        this.chunkDataFile = new RandomAccessFile(chunkDataFilePath, "r");
    }

    public void start(String line) throws IOException {
        //tokenize and normalize the query
        List<String> tokens = preprocessQuery(line);
        this.indexReader = new IndexReader(invertedIndex,tokenIndexOffset);
        //returns a list of {chunkId,frequencies,token} objects.
        List<IndexData> queryIndexData = indexReader.readTokenIndex(tokens);
        //from the chunkID now we have to access the chunk metadata to get to the actual chunk.
        String firstToken = queryIndexData.get(0).getToken();
        List<Integer> firstChunkIDList = queryIndexData.get(0).getIds();
        List<Integer> firstFreqList = queryIndexData.get(0).getFreqs();
        LOGGER.fine("First Token: " + firstToken);
        LOGGER.fine("First Chunk ID: " + firstChunkIDList);
        LOGGER.fine("First Freq: " + firstFreqList);
        try {
            getChunk(firstToken,firstChunkIDList,firstFreqList);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,e.getMessage(),e);
        }

    }

    public List<String> preprocessQuery(String line) throws IOException {
        List<String> tokens = Arrays.asList(line.split(" "));
        return TextUtils.tokenizeQuery(tokens);
    }

    private void getChunk(String token,List<Integer> firstChunkIDList,List<Integer> firstFreqList) throws IOException {
        try {
            List<ChunkMetaData> chunkMetadata = getChunkMetadata(firstChunkIDList);
            List<String> chunks = getChunkData(chunkMetadata);
            LOGGER.fine("chunkMetadata data offset: " + chunkMetadata.get(0).getDataOffset());
            LOGGER.fine("chunkMetadata data length: " + chunkMetadata.get(0).getDataLength());
            LOGGER.info("first chunk :" + chunks.get(0));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,e.getMessage(),e);
        }
    }

    private List<ChunkMetaData> getChunkMetadata(List<Integer> chunkIdList) throws IOException {
        //read the chunk_index.bin file, get the length, and offset in the data file
        List<ChunkMetaData> chunkMetaData = new ArrayList<>();
        for (int i = 0; i < min(TOP_K,chunkIdList.size()) ; i++) {
            //get the details for the top k in the chunk ID list.
            int currentChunkID = chunkIdList.get(i);
            long positionInIndex = (long) currentChunkID * RECORD_SIZE;
            if (positionInIndex >= chunkIndexFile.length()) {
                LOGGER.warning("Unable to access the metadata in the chunk index due to mismatch in sizing");
                continue;
            }
            chunkIndexFile.seek(positionInIndex);
            long dataOffset = chunkIndexFile.readLong();
            int dataLength = chunkIndexFile.readInt();
            int docId = chunkIndexFile.readInt();
            int tokenCount = chunkIndexFile.readInt();
            chunkMetaData.add(new ChunkMetaData(dataOffset,dataLength,docId,tokenCount));
        }
        return chunkMetaData;
    }

    private List<String> getChunkData(List<ChunkMetaData> chunkMetaData) throws IOException {
        List<String> chunks = new ArrayList<>();
        for (ChunkMetaData chunkMetaDataData : chunkMetaData) {
            long dataOffset = chunkMetaDataData.getDataOffset();
            int dataLength = chunkMetaDataData.getDataLength();
            chunkDataFile.seek(dataOffset);
            byte[] buffer = new byte[dataLength];
            //read data into the buffer.
            try {
                chunkDataFile.readFully(buffer);
            } catch (EOFException e) {
                LOGGER.warning(String.format("Unable to read all the chunk data: " + e.getMessage()));
            }
            String chunkText = new String(buffer, StandardCharsets.UTF_8);
            chunks.add(chunkText);
        }
        return chunks;
    }
}
