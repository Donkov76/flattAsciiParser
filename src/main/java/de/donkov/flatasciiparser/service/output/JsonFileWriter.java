package de.donkov.flatasciiparser.service.output;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.donkov.flatasciiparser.model.json.JsonDocument;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Component;

/**
 * Writes a {@link JsonDocument} to an output file as JSON.
 *
 * <p>The input flatfile is read as ISO-8859-1, but the JSON output is written
 * as UTF-8, the encoding of the JSON specification.
 */
@Component
public class JsonFileWriter {

    private final ObjectMapper objectMapper;

    public JsonFileWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Writes the document to the given output file, replacing an existing
     * file.
     *
     * @param outputFile the file to write
     * @throws IOException if the file cannot be written
     */
    public void write(JsonDocument document, Path outputFile) throws IOException {
        try (var writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, document);
        }
    }
}
