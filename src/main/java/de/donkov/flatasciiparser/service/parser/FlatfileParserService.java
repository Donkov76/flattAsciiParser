package de.donkov.flatasciiparser.service.parser;

import de.donkov.flatasciiparser.model.row.Row;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Reads the ASCII flatfile record by record and parses each record into a
 * {@link Row}.
 *
 * <p>Records are delimited by CRLF (x'0D0A'). The file is read completely
 * and split at the record delimiter: the input files are small enough that
 * streaming is not necessary. Control characters other than the record
 * delimiter are not allowed by the file structure specification and are
 * rejected by the {@link RowParser}. Non-ASCII characters (e.g. German
 * umlauts) are encoded as single bytes, so the file is read with ISO-8859-1.
 */
@Service
public class FlatfileParserService {

    private static final Charset FILE_CHARSET = StandardCharsets.ISO_8859_1;
    private static final String RECORD_DELIMITER = "\r\n";

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
        String[] records = Files.readString(inputFile, FILE_CHARSET)
                .split(RECORD_DELIMITER, -1);

        List<Row> rows = new ArrayList<>();
        // A record is never empty: every record starts with its record ID.
        // An empty last element is the file's trailing delimiter; an empty
        // element in between is a format violation that the RowParser
        // rejects with the matching line number.
        for (int i = 0; i < records.length; i++) {
            if (i == records.length - 1 && records[i].isEmpty()) {
                break;
            }
            rows.add(rowParser.parse(records[i], i + 1));
        }
        return rows;
    }
}
