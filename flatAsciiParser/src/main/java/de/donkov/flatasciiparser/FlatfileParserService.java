package de.donkov.flatasciiparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Service;

/**
 * Reads the ASCII flatfile record by record.
 *
 * <p>A record is complete as soon as the record delimiter CRLF (x'0D0A') has
 * occurred. Non-ASCII characters (e.g. German umlauts) are encoded as single
 * bytes, so the file is read with ISO-8859-1.
 */
@Service
public class FlatfileParserService {

    private static final Charset FILE_CHARSET = StandardCharsets.ISO_8859_1;

    /**
     * Reads all records from the given input file.
     *
     * @return the records of the file, without the record delimiter
     */
    public List<String> parseRecords(Path inputFile) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(inputFile, FILE_CHARSET)) {
            List<String> records = new ArrayList<>();
            String record;
            while ((record = readRecord(reader)) != null) {
                records.add(record);
            }
            return records;
        }
    }

    /**
     * Reads a single record. Characters are read until the record delimiter
     * CRLF has occurred; the returned record does not contain the delimiter.
     *
     * @return the next record, or {@code null} if the end of the file has
     *         been reached
     */
    private String readRecord(BufferedReader reader) throws IOException {
        StringBuilder record = new StringBuilder();
        int c;
        while ((c = reader.read()) != -1) {
            if (c == '\r') {
                int next = reader.read();
                if (next == '\n') {
                    return record.toString();
                }
                record.append('\r');
                if (next == -1) {
                    return record.toString();
                }
                record.append((char) next);
            } else {
                record.append((char) c);
            }
        }
        return record.isEmpty() ? null : record.toString();
    }
}
