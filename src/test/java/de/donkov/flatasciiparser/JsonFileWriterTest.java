package de.donkov.flatasciiparser;

import de.donkov.flatasciiparser.model.json.JsonDocument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.donkov.flatasciiparser.model.Document;
import de.donkov.flatasciiparser.service.output.DocumentJsonConverterService;
import de.donkov.flatasciiparser.service.output.EmptyValueMode;
import de.donkov.flatasciiparser.service.output.JsonFileWriter;
import de.donkov.flatasciiparser.service.parser.DocumentParser;
import de.donkov.flatasciiparser.service.parser.FlatfileParserService;
import de.donkov.flatasciiparser.service.parser.RowParser;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link JsonFileWriter} and the output file naming schema.
 */
class JsonFileWriterTest {

    private final DocumentJsonConverterService documentJsonConverter = new DocumentJsonConverterService(EmptyValueMode.EMPTY_STRING);
    private final JsonFileWriter jsonFileWriter = new JsonFileWriter(new ObjectMapper());

    @TempDir
    Path tempDir;

    @Test
    void writesDocumentAccordingToNamingSchema() throws IOException, URISyntaxException {
        Document firstDocument = parseSampleFile().getFirst();
        String ohtdon = firstDocument.getHead().getFieldValue(de.donkov.flatasciiparser.model.row.HeadRow.Field.OHTDON);

        Path outputFile = tempDir.resolve(
                "4311560000000_IV_4260043070002_20200527133435" + "_" + ohtdon + ".json");
        jsonFileWriter.write(documentJsonConverter.convert(firstDocument), outputFile);

        assertTrue(Files.exists(outputFile), "The output file is written next to the input file");

        JsonNode json = new ObjectMapper().readTree(outputFile.toFile());
        assertEquals("HEAD", json.get("RECID").asText());
        assertEquals("IX169381", json.get("OHTDON").asText());
        assertEquals("19,00", json.get("OHTMWS").asText());
        assertEquals(5, json.get("HEAD_TEXT").size());
        assertEquals(1, json.get("HEAD_ALC").size());
        assertEquals(1, json.get("POS").size());
        assertEquals("MERRYCHEF OFENREINIGER Deutsches Et",
                json.get("POS").get(0).get("OPOALT").asText());
    }

    @Test
    void overwritesExistingFile() throws IOException, URISyntaxException {
        Document firstDocument = parseSampleFile().getFirst();

        Path outputFile = tempDir.resolve("output.json");
        Files.writeString(outputFile, "old content");
        jsonFileWriter.write(documentJsonConverter.convert(firstDocument), outputFile);

        JsonNode json = new ObjectMapper().readTree(outputFile.toFile());
        assertEquals("HEAD", json.get("RECID").asText(),
                "An existing file is replaced by the new content");
    }

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
}
