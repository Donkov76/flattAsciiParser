package de.donkov.flatasciiparser.service.parser;

import de.donkov.flatasciiparser.model.RecordType;
import de.donkov.flatasciiparser.model.row.Row;
import java.util.Arrays;
import org.springframework.stereotype.Component;

/**
 * Parses a single line of the flatfile into a {@link Row}.
 *
 * <p>The fields are separated by the pipe character. The record type is
 * derived from the record ID in the first field. Structural errors (unknown
 * record ID, wrong number of fields, control characters other than the
 * record delimiter) are reported as {@link FlatfileFormatException}
 * including the line number.
 */
@Component
public class RowParser {

    private static final String FIELD_SEPARATOR = "\\|";

    /**
     * @param line      the raw line without the record delimiter
     * @param lineNumber the 1-based line number in the input file
     */
    public Row parse(String line, int lineNumber) {
        if (line.chars().anyMatch(c -> c <= 0x1F || c == 0x7F)) {
            throw new FlatfileFormatException(lineNumber,
                    "Control characters other than the record delimiter are not allowed");
        }

        String[] fields = line.split(FIELD_SEPARATOR, -1);

        RecordType type;
        try {
            type = RecordType.fromRecordId(fields[0]);
        } catch (IllegalArgumentException e) {
            throw new FlatfileFormatException(lineNumber, e.getMessage());
        }

        try {
            return Row.of(type, Arrays.asList(fields));
        } catch (IllegalArgumentException e) {
            throw new FlatfileFormatException(lineNumber, e.getMessage());
        }
    }
}
