package de.donkov.flatasciiparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import de.donkov.flatasciiparser.model.row.HeadRow;
import de.donkov.flatasciiparser.model.RecordType;
import de.donkov.flatasciiparser.model.row.Row;
import de.donkov.flatasciiparser.service.FlatfileParserService;
import de.donkov.flatasciiparser.service.RowParser;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link FlatfileParserService} against the sample flatfile.
 */
class FlatfileParserServiceTest {

    private static final String SAMPLE_FILE = "flatfile/4311560000000_IV_4260043070002_20200527133435";

    private final FlatfileParserService flatfileParserService = new FlatfileParserService(new RowParser());

    private Path sampleFile() throws URISyntaxException {
        var url = getClass().getClassLoader().getResource(SAMPLE_FILE);
        if (url == null) {
            throw new IllegalStateException("Sample flatfile not found in test resources: " + SAMPLE_FILE);
        }
        return Path.of(url.toURI());
    }

    private List<Row> parseSampleFile() throws IOException, URISyntaxException {
        return flatfileParserService.parseRecords(sampleFile());
    }

    @Test
    void readsAllRecordsFromSampleFile() throws IOException, URISyntaxException {
        assertEquals(130, parseSampleFile().size(),
                "The sample flatfile contains 130 records in total");
    }

    @Test
    void countsRowsByRecordType() throws IOException, URISyntaxException {
        List<Row> rows = parseSampleFile();

        assertEquals(15, countRowsOfType(rows, RecordType.HEAD));
        assertEquals(49, countRowsOfType(rows, RecordType.TEXT));
        assertEquals(15, countRowsOfType(rows, RecordType.ALC));
        assertEquals(51, countRowsOfType(rows, RecordType.POS));
    }

    private long countRowsOfType(List<Row> rows, RecordType type) {
        return rows.stream()
                .filter(row -> row.getType() == type)
                .count();
    }

    @Test
    void parsesFirstHeadRecordWithAllFields() throws IOException, URISyntaxException {
        Row firstRow = parseSampleFile().getFirst();

        HeadRow headRow = assertInstanceOf(HeadRow.class, firstRow);
        assertEquals(48, headRow.getFields().size(),
                "A HEAD record consists of 48 fields");
        assertEquals("HEAD", headRow.getRecId());
        assertEquals("IX169381", headRow.getFieldValue(HeadRow.Field.OHTDON),
                "Field 6 (OHTDON) is the document number");
        assertEquals("E-Mail M. Kniering", headRow.getFieldValue(HeadRow.Field.OHTON),
                "Field 9 (OHTON) contains spaces and must not be split");
        assertEquals("", headRow.getFieldValue(HeadRow.Field.OHTPDF),
                "The last HEAD field (OHTPDF) is empty and must be preserved");
    }
}
