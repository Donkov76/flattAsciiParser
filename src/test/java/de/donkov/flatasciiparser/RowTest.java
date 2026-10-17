package de.donkov.flatasciiparser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.donkov.flatasciiparser.model.RecordType;
import de.donkov.flatasciiparser.model.row.AlcRow;
import de.donkov.flatasciiparser.model.row.HeadRow;
import de.donkov.flatasciiparser.model.row.PosRow;
import de.donkov.flatasciiparser.model.row.Row;
import de.donkov.flatasciiparser.model.row.RowField;
import de.donkov.flatasciiparser.model.row.TextRow;
import de.donkov.flatasciiparser.service.parser.FlatfileParserService;
import de.donkov.flatasciiparser.service.parser.RowParser;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for the field access guard of {@link Row}: a field of one record
 * type must not be used to read another record type.
 */
class RowTest {

    private final FlatfileParserService flatfileParserService = new FlatfileParserService(new RowParser());

    /**
     * @return the first row of each record type, taken from the sample
     *         flatfile that contains all four record types
     */
    private Map<RecordType, Row> firstRowOfEachType() throws IOException, URISyntaxException {
        var url = getClass().getClassLoader().getResource("flatfile/sample-pos-alc.txt");
        if (url == null) {
            throw new IllegalStateException("Sample flatfile not found in test resources");
        }
        Map<RecordType, Row> rowsByType = new EnumMap<>(RecordType.class);
        for (Row row : flatfileParserService.parseRecords(Path.of(url.toURI()))) {
            rowsByType.putIfAbsent(row.getType(), row);
        }
        return rowsByType;
    }

    private List<RowField> allFields() {
        List<RowField> fields = new ArrayList<>();
        fields.addAll(List.of(HeadRow.Field.values()));
        fields.addAll(List.of(PosRow.Field.values()));
        fields.addAll(List.of(TextRow.Field.values()));
        fields.addAll(List.of(AlcRow.Field.values()));
        return fields;
    }

    @Test
    void rejectsFieldOfForeignRecordType() throws IOException, URISyntaxException {
        Map<RecordType, Row> rowsByType = firstRowOfEachType();

        for (Row row : rowsByType.values()) {
            for (RowField field : allFields()) {
                if (field.getRecordType() == row.getType()) {
                    continue;
                }
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                        () -> row.getFieldValue(field),
                        "%s must not accept field %s of type %s"
                                .formatted(row.getType(), field, field.getRecordType()));
                assertTrue(exception.getMessage().contains(field.toString()),
                        "The message names the rejected field");
                assertTrue(exception.getMessage().contains(row.getType().toString()),
                        "The message names the row type");
            }
        }
    }

    @Test
    void acceptsFieldOfOwnRecordType() throws IOException, URISyntaxException {
        Map<RecordType, Row> rowsByType = firstRowOfEachType();

        for (Row row : rowsByType.values()) {
            for (RowField field : allFields()) {
                if (field.getRecordType() != row.getType()) {
                    continue;
                }
                assertDoesNotThrow(() -> row.getFieldValue(field),
                        "%s must accept its own field %s".formatted(row.getType(), field));
            }
        }
    }

    @Test
    void guardIsConsistentWithEnumDeclaration() {
        // Each Field enum reports the record type of its declaring row class
        for (HeadRow.Field field : HeadRow.Field.values()) {
            assertEquals(RecordType.HEAD, field.getRecordType());
        }
        for (PosRow.Field field : PosRow.Field.values()) {
            assertEquals(RecordType.POS, field.getRecordType());
        }
        for (TextRow.Field field : TextRow.Field.values()) {
            assertEquals(RecordType.TEXT, field.getRecordType());
        }
        for (AlcRow.Field field : AlcRow.Field.values()) {
            assertEquals(RecordType.ALC, field.getRecordType());
        }
    }
}
