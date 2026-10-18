package de.donkov.flatasciiparser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.donkov.flatasciiparser.model.Document;
import de.donkov.flatasciiparser.model.json.JsonDocument;
import de.donkov.flatasciiparser.model.row.AlcRow;
import de.donkov.flatasciiparser.model.row.HeadRow;
import de.donkov.flatasciiparser.model.row.PosRow;
import de.donkov.flatasciiparser.model.row.Row;
import de.donkov.flatasciiparser.model.row.RowField;
import de.donkov.flatasciiparser.model.row.TextRow;
import de.donkov.flatasciiparser.service.output.DocumentJsonConverterService;
import de.donkov.flatasciiparser.service.output.EmptyValueMode;
import de.donkov.flatasciiparser.service.parser.DocumentParser;
import de.donkov.flatasciiparser.service.parser.FlatfileParserService;
import de.donkov.flatasciiparser.service.parser.RowParser;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.StringJoiner;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Not a classical test case: this class loads the documents of the sample
 * flatfile, logs every document in a readable form, and then logs the JSON
 * structure it has been converted into. It serves as visual verification of
 * the conversion.
 */
class DocumentConversionLogTest {

    private static final Logger log = LoggerFactory.getLogger(DocumentConversionLogTest.class);

    private final DocumentJsonConverterService documentJsonConverterService = new DocumentJsonConverterService(EmptyValueMode.EMPTY_STRING);
    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    void logDocumentsAndTheirJsonConversion() throws IOException, URISyntaxException {
        List<Document> documents = parseSampleFile();

        for (int i = 0; i < documents.size(); i++) {
            log.info("{}  Document {} of {}  {}", "=".repeat(20), i + 1, documents.size(), "=".repeat(20));
            log.info("{}", formatDocument(documents.get(i)));

            JsonDocument json = documentJsonConverterService.convert(documents.get(i));
            log.info("JSON:\n{}", objectMapper.writeValueAsString(json));
        }
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

    private String formatDocument(Document document) {
        StringJoiner lines = new StringJoiner("\n");
        lines.add("Document %s".formatted(document.getHead().getFieldValue(HeadRow.Field.OHTDON)));
        lines.add("  HEAD        " + formatFields(document.getHead(), HeadRow.Field.values()));
        document.getHeadTexts().forEach(text ->
                lines.add("  HEAD_TEXT   " + formatFields(text, TextRow.Field.values())));
        document.getHeadAllowances().forEach(alc ->
                lines.add("  HEAD_ALC    " + formatFields(alc, AlcRow.Field.values())));
        for (int i = 0; i < document.getLineItems().size(); i++) {
            var item = document.getLineItems().get(i);
            lines.add("  POS %d       ".formatted(i + 1) + formatFields(item.getPos(), PosRow.Field.values()));
            item.getTexts().forEach(text ->
                    lines.add("    POS_TEXT  " + formatFields(text, TextRow.Field.values())));
            item.getAllowances().forEach(alc ->
                    lines.add("    POS_ALC   " + formatFields(alc, AlcRow.Field.values())));
        }
        return lines.toString();
    }

    private <F extends Enum<F> & RowField> String formatFields(Row row, F[] fields) {
        StringJoiner joiner = new StringJoiner(", ");
        joiner.add("RECID=" + row.getRecId());
        for (F field : fields) {
            String value = row.getFieldValue(field);
            if (!value.isEmpty()) {
                joiner.add(field.name() + "=" + value);
            }
        }
        return joiner.toString();
    }
}
