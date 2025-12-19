package com.pri1712.searchengine.parser;

import com.pri1712.searchengine.model.ParsedDocument;

import java.io.IOException;
import java.util.List;

public interface DocumentParser {
    List<ParsedDocument> parse(String filePath, int MAX_DOCS_TO_PROCESS) throws IOException;
    String getParserName();
}
