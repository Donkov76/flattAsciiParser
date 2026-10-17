package de.donkov.flatasciiparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.donkov.flatasciiparser.model.Document;
import de.donkov.flatasciiparser.output.DocumentJsonConverter;
import de.donkov.flatasciiparser.output.EmptyValueMode;
import de.donkov.flatasciiparser.output.JsonDocument;
import de.donkov.flatasciiparser.output.JsonLineItem;
import de.donkov.flatasciiparser.service.DocumentParser;
import de.donkov.flatasciiparser.service.FlatfileParserService;
import de.donkov.flatasciiparser.service.RowParser;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link DocumentJsonConverter} against the sample flatfile.
 */
class DocumentJsonConverterTest {

    private final DocumentJsonConverter documentJsonConverter = new DocumentJsonConverter(EmptyValueMode.EMPTY_STRING);

    private List<Document> documents;

    @BeforeEach
    void parseSampleFile() throws IOException, URISyntaxException {
        var url = getClass().getClassLoader().getResource(
                "flatfile/4311560000000_IV_4260043070002_20200527133435");
        if (url == null) {
            throw new IllegalStateException("Sample flatfile not found in test resources");
        }
        FlatfileParserService flatfileParserService = new FlatfileParserService(new RowParser());
        documents = new DocumentParser()
                .parse(flatfileParserService.parseRecords(Path.of(url.toURI())));
    }

    @Test
    void mapsHeadFieldsInSpecificationOrder() {
        JsonDocument json = documentJsonConverter.convert(documents.getFirst());

        List<String> keys = List.copyOf(json.keySet());
        assertEquals("RECID", keys.getFirst());
        assertEquals("OHTRCP", keys.get(1));
        assertEquals("OHTPDF", keys.get(47));
        assertEquals(JsonDocument.HEAD_TEXT, keys.get(48));
        assertEquals(JsonDocument.HEAD_ALC, keys.get(49));
        assertEquals(JsonDocument.POS, keys.get(50));
        assertEquals(51, keys.size());
    }

    @Test
    void mapsHeadFieldValuesAsStrings() {
        JsonDocument json = documentJsonConverter.convert(documents.getFirst());

        assertEquals("HEAD", json.get("RECID"));
        assertEquals("4311560000000", json.get("OHTRCP"));
        assertEquals("IX169381", json.get("OHTDON"));
        assertEquals("19,00", json.get("OHTMWS"),
                "Decimal values are taken over unchanged as strings");
        assertEquals("", json.get("OHTPDF"));
    }

    @Test
    void mapsNestedRecords() {
        JsonDocument json = documentJsonConverter.convert(documents.getFirst());

        @SuppressWarnings("unchecked")
        List<Map<String, String>> headTexts = (List<Map<String, String>>) json.get(JsonDocument.HEAD_TEXT);
        assertEquals(5, headTexts.size());
        assertEquals("Kunde:", headTexts.getFirst().get("OTXTXT1"));
        assertEquals("GE", headTexts.getFirst().get("OTXLNG"));

        @SuppressWarnings("unchecked")
        List<Map<String, String>> headAllowances = (List<Map<String, String>>) json.get(JsonDocument.HEAD_ALC);
        assertEquals(1, headAllowances.size());
        assertEquals("C", headAllowances.getFirst().get("OZAKZA"));

        List<JsonLineItem> lineItems = (List<JsonLineItem>) json.get(JsonDocument.POS);
        assertEquals(1, lineItems.size());
        assertEquals("1", lineItems.getFirst().get("OPOLDF"));
        assertEquals("MERRYCHEF OFENREINIGER Deutsches Et", lineItems.getFirst().get("OPOALT"));
        assertEquals(0, ((List<?>) lineItems.getFirst().get(JsonLineItem.POS_TEXT)).size());
    }

    @Test
    void mapsLineItemWithTexts() {
        Document document = documents.stream()
                .filter(d -> "IX169481".equals(d.getHead().getFieldValue(de.donkov.flatasciiparser.model.row.HeadRow.Field.OHTDON)))
                .findFirst()
                .orElseThrow();
        JsonDocument json = documentJsonConverter.convert(document);

        List<JsonLineItem> lineItems = (List<JsonLineItem>) json.get(JsonDocument.POS);
        JsonLineItem firstItem = lineItems.getFirst();

        List<?> texts = (List<?>) firstItem.get(JsonLineItem.POS_TEXT);
        assertEquals(4, texts.size(),
                "The 4 TEXT records after the POS record belong to the line item");
    }

    @Test
    void mapsEmptyValuesAsNullWhenConfigured() {
        DocumentJsonConverter nullModeConverter = new DocumentJsonConverter(EmptyValueMode.NULL);
        JsonDocument json = nullModeConverter.convert(documents.getFirst());

        assertNull(json.get("OHTPDF"), "Empty HEAD field becomes JSON null in NULL mode");
        assertNull(json.get("OHTLN2"), "Empty name field becomes JSON null in NULL mode");
        assertEquals("HEAD", json.get("RECID"), "Non-empty values are unaffected");
    }
}
