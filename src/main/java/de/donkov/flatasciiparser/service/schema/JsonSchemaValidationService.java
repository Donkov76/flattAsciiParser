package de.donkov.flatasciiparser.service.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.Error;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.SpecificationVersion;
import de.donkov.flatasciiparser.model.json.JsonDocument;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Validates the JSON structure of a document against the JSON schema of the
 * task specification. Can be disabled via the property
 * {@code flatasciiparser.validation.enabled}, e.g. for debugging purposes.
 */
@Service
public class JsonSchemaValidationService {

    private static final Logger log = LoggerFactory.getLogger(JsonSchemaValidationService.class);

    static final String SCHEMA_LOCATION = "/schema/document-schema.json";

    private final ObjectMapper objectMapper;
    private final boolean validationEnabled;
    private final Schema schema;

    public JsonSchemaValidationService(ObjectMapper objectMapper,
            @Value("${flatasciiparser.validation.enabled:true}") boolean validationEnabled) {
        this.objectMapper = objectMapper;
        this.validationEnabled = validationEnabled;
        this.schema = validationEnabled ? loadSchema() : null;
    }

    public boolean isEnabled() {
        return validationEnabled;
    }

    /**
     * Validates the document against the schema.
     *
     * @return the validation errors, empty if the document is valid or if
     *         validation is disabled
     */
    public List<Error> validate(JsonDocument document) {
        if (!validationEnabled) {
            return List.of();
        }
        return schema.validate(objectMapper.valueToTree(document));
    }

    private Schema loadSchema() {
        SchemaRegistry registry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);
        try (InputStream schemaStream = getClass().getResourceAsStream(SCHEMA_LOCATION)) {
            if (schemaStream == null) {
                throw new IllegalStateException("JSON schema not found: " + SCHEMA_LOCATION);
            }
            Schema loadedSchema = registry.getSchema(schemaStream);
            log.info("JSON schema loaded from {}", SCHEMA_LOCATION);
            return loadedSchema;
        } catch (IOException e) {
            throw new IllegalStateException("JSON schema could not be loaded: " + SCHEMA_LOCATION, e);
        }
    }
}
