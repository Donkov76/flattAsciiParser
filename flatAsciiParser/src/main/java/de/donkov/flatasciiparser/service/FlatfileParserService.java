package de.donkov.flatasciiparser.service;

import de.donkov.flatasciiparser.model.row.Row;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Reads the ASCII flatfile record by record and parses each line into a
 * {@link Row}.
 *
 * <p>A record is complete as soon as the record delimiter CRLF (x'0D0A') has
 * occurred. Non-ASCII characters (e.g. German umlauts) are encoded as single
 * bytes, so the file is read with ISO-8859-1.
 */
@Service
public class FlatfileParserService {

    private static final Charset FILE_CHARSET = StandardCharsets.ISO_8859_1;

    private final RowParser rowParser;

    public FlatfileParserService(RowParser rowParser) {
        this.rowParser = rowParser;
    }

    /**
     * Reads all records from the given input file.
     *
     * @return the parsed rows of the file, in file order
     */
    public List<Row> parseRecords(Path inputFile) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(inputFile, FILE_CHARSET)) {
            List<Row> records = new ArrayList<>();
            String line;
            int lineNumber = 0;
            while ((line = readLine(reader)) != null) {
                lineNumber++;
                records.add(rowParser.parse(line, lineNumber));
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
    private String readLine(BufferedReader reader) throws IOException {
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
