package de.donkov.flatasciiparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.donkov.flatasciiparser.model.Document;
import de.donkov.flatasciiparser.model.LineItem;
import de.donkov.flatasciiparser.model.RecordType;
import de.donkov.flatasciiparser.model.row.AlcRow;
import de.donkov.flatasciiparser.model.row.HeadRow;
import de.donkov.flatasciiparser.model.row.Row;
import de.donkov.flatasciiparser.model.row.TextRow;
import de.donkov.flatasciiparser.service.parser.DocumentParser;
import de.donkov.flatasciiparser.service.parser.FlatfileFormatException;
import de.donkov.flatasciiparser.service.parser.FlatfileParserService;
import de.donkov.flatasciiparser.service.parser.RowParser;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link DocumentParser} against the sample flatfile.
 */
class DocumentParserTest {

    private final RowParser rowParser = new RowParser();
    private final FlatfileParserService flatfileParserService = new FlatfileParserService(rowParser);
    private final DocumentParser documentParser = new DocumentParser();

    private List<Document> parseSampleFile() throws IOException, URISyntaxException {
        return parseFlatfile("flatfile/4311560000000_IV_4260043070002_20200527133435");
    }

    private List<Document> parseFlatfile(String resource)
            throws IOException, URISyntaxException {
        var url = getClass().getClassLoader().getResource(resource);
        if (url == null) {
            throw new IllegalStateException("Sample flatfile not found in test resources: " + resource);
        }
        return documentParser.parse(flatfileParserService.parseRecords(Path.of(url.toURI())));
    }

    @Test
    void groupsSampleFileIntoDocuments() throws IOException, URISyntaxException {
        List<Document> documents = parseSampleFile();

        assertEquals(15, documents.size(), "The sample flatfile contains 15 documents");
    }

    @Test
    void groupsHeaderRecordsOfFirstDocument() throws IOException, URISyntaxException {
        Document document = parseSampleFile().getFirst();

        assertEquals("IX169381", document.getHead().getFieldValue(HeadRow.Field.OHTDON));
        assertEquals(5, document.getHeadTexts().size());
        assertEquals(1, document.getHeadAllowances().size());
        assertEquals(1, document.getLineItems().size());
    }

    @Test
    void groupsLineItemsOfSecondDocument() throws IOException, URISyntaxException {
        Document document = parseSampleFile().get(1);

        assertEquals("IX169408", document.getHead().getFieldValue(HeadRow.Field.OHTDON));
        assertEquals(0, document.getHeadTexts().size());
        assertEquals(1, document.getHeadAllowances().size());
        assertEquals(6, document.getLineItems().size());
    }

    @Test
    void assignsTextsAfterPosToLineItem() throws IOException, URISyntaxException {
        // Document IX169481: the first line item is followed by 4 TEXT records
        Document document = parseSampleFile().stream()
                .filter(d -> "IX169481".equals(d.getHead().getFieldValue(HeadRow.Field.OHTDON)))
                .findFirst()
                .orElseThrow();

        LineItem firstItem = document.getLineItems().getFirst();
        assertEquals(4, firstItem.getTexts().size(),
                "TEXT records after a POS record belong to the line item");
        assertEquals(4, document.getHeadTexts().size(),
                "TEXT records before the first POS record belong to the header");
    }

    @Test
    void assignsAlcRecordsAfterPosToLineItem() throws IOException, URISyntaxException {
        // The original sample flatfile contains no POS with ALC records, so a
        // separate sample flatfile covers this variant
        Document document = parseFlatfile("flatfile/sample-pos-alc.txt").getFirst();

        assertEquals(0, document.getHeadTexts().size(),
                "TEXT records after a POS record belong to the line item, not the header");
        assertEquals(0, document.getHeadAllowances().size(),
                "ALC records after a POS record belong to the line item, not the header");
        assertEquals(2, document.getLineItems().size());

        LineItem firstItem = document.getLineItems().getFirst();
        assertEquals(1, firstItem.getAllowances().size(),
                "The ALC record between the two POS records belongs to the first line item");
        assertEquals("C", firstItem.getAllowances().getFirst()
                .getFieldValue(AlcRow.Field.OZAKZA));
        assertEquals(1, firstItem.getTexts().size());

        LineItem secondItem = document.getLineItems().get(1);
        assertEquals(1, secondItem.getAllowances().size(),
                "ALC records at the end of the document belong to the last line item");
        assertEquals("A", secondItem.getAllowances().getFirst()
                .getFieldValue(AlcRow.Field.OZAKZA));
    }

    @Test
    void rejectsRecordBeforeFirstHeadRecord() {
        Row textRow = Row.of(RecordType.TEXT,
                List.of("TEXT", "Kunde:", "", "", "", "", "GE"));

        FlatfileFormatException exception = assertThrows(FlatfileFormatException.class,
                () -> documentParser.parse(List.of(textRow)));

        assertEquals(1, exception.getLineNumber());
    }
}
