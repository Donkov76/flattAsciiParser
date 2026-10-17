package de.donkov.flatasciiparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.donkov.flatasciiparser.model.Document;
import de.donkov.flatasciiparser.output.DocumentJsonConverter;
import de.donkov.flatasciiparser.output.EmptyValueMode;
import de.donkov.flatasciiparser.output.JsonDocument;
import de.donkov.flatasciiparser.service.DocumentParser;
import de.donkov.flatasciiparser.service.FlatfileParserService;
import de.donkov.flatasciiparser.service.JsonSchemaValidationService;
import de.donkov.flatasciiparser.service.RowParser;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link JsonSchemaValidationService}.
 */
class JsonSchemaValidationServiceTest {

    private final DocumentJsonConverter documentJsonConverter = new DocumentJsonConverter(EmptyValueMode.EMPTY_STRING);
    private final JsonSchemaValidationService validationService =
            new JsonSchemaValidationService(new ObjectMapper(), true);

    private List<Document> parseSampleFile() throws IOException, URISyntaxException {
        var url = getClass().getClassLoader().getResource(
                "flatfile/4311560000000_IV_4260043070002_20200527133435");
        if (url == null) {
            throw new IllegalStateException("Sample flatfile not found in test resources");
        }
        FlatfileParserService flatfileParserService = new FlatfileParserService(new RowParser());
        return new DocumentParser()
                .parse(flatfileParserService.parseRecords(Path.of(url.toURI())));
    }

    @Test
    void allSampleDocumentsAreValid() throws IOException, URISyntaxException {
        List<Document> documents = parseSampleFile();

        for (Document document : documents) {
            JsonDocument json = documentJsonConverter.convert(document);
            assertTrue(validationService.validate(json).isEmpty(),
                    "Document " + document.getHead().getFieldValue(
                            de.donkov.flatasciiparser.model.row.HeadRow.Field.OHTDON)
                            + " must be valid against the JSON schema");
        }
    }

    @Test
    void allSampleDocumentsAreValidInNullMode() throws IOException, URISyntaxException {
        DocumentJsonConverter nullModeConverter = new DocumentJsonConverter(EmptyValueMode.NULL);
        List<Document> documents = parseSampleFile();

        for (Document document : documents) {
            JsonDocument json = nullModeConverter.convert(document);
            assertTrue(validationService.validate(json).isEmpty(),
                    "The schema also accepts null values");
        }
    }

    @Test
    void detectsInvalidValues() throws IOException, URISyntaxException {
        JsonDocument json = documentJsonConverter.convert(parseSampleFile().getFirst());

        json.put("OHTMWS", "invalid rate");
        json.put("OHTDOD", "07.05.2020");
        json.put("RECID", "XXX");

        var violations = validationService.validate(json);

        assertEquals(3, violations.size(), "Each manipulated field is reported");
        assertTrue(violations.stream().anyMatch(v -> v.getInstanceLocation().toString().contains("OHTMWS")));
        assertTrue(violations.stream().anyMatch(v -> v.getInstanceLocation().toString().contains("OHTDOD")));
        assertTrue(violations.stream().anyMatch(v -> v.getInstanceLocation().toString().contains("RECID")));
    }

    @Test
    void detectsMissingFields() throws IOException, URISyntaxException {
        JsonDocument json = documentJsonConverter.convert(parseSampleFile().getFirst());

        json.remove("OHTDON");

        assertFalse(validationService.validate(json).isEmpty(),
                "A missing required field is reported");
    }

    @Test
    void skipsValidationWhenDisabled() throws IOException, URISyntaxException {
        JsonSchemaValidationService disabledService =
                new JsonSchemaValidationService(new ObjectMapper(), false);
        JsonDocument json = documentJsonConverter.convert(parseSampleFile().getFirst());
        json.put("OHTMWS", "invalid rate");

        assertTrue(disabledService.validate(json).isEmpty(),
                "With validation disabled, no violations are reported");
        assertFalse(disabledService.isEnabled());
    }
}
