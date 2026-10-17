package de.donkov.flatasciiparser.io;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Reads the ASCII flatfile into its raw records.
 *
 * <p>The interface file is defined as an ASCII structure with variable record
 * length and variable field length, records delimited by CRLF. Non-ASCII
 * characters (e.g. German umlauts) are encoded as single bytes, so the file
 * is read with ISO-8859-1.
 */
@Component
public class FlatFileReader {

    public static final Charset FILE_CHARSET = StandardCharsets.ISO_8859_1;
    public static final String FIELD_SEPARATOR = "|";
    public static final String RECORD_TYPE_HEAD = "HEAD";
    public static final String RECORD_TYPE_TEXT = "TEXT";
    public static final String RECORD_TYPE_ALC = "ALC";
    public static final String RECORD_TYPE_POS = "POS";

    public List<String> readRecords(Path inputFile) throws IOException {
        try (var reader = Files.newBufferedReader(inputFile, FILE_CHARSET)) {
            return reader.lines().toList();
        }
    }

    public String[] splitFields(String record) {
        return record.split("\\" + FIELD_SEPARATOR, -1);
    }
}
