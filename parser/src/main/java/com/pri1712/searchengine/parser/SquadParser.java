package com.pri1712.searchengine.parser;

import com.pri1712.searchengine.model.ParsedDocument;

import java.io.IOException;
import java.util.List;

/***
 * Class for parsing squad json format.
 */
public class SquadParser implements DocumentParser {

    @Override
    public List<ParsedDocument> parse(String filePath,int MAX_DOCS_TO_PROCESS) throws IOException {

    }

    @Override
    public String getParserName() {
        return "";
    }
}
