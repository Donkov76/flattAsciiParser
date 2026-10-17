package de.donkov.flatasciiparser;

import de.donkov.flatasciiparser.model.json.JsonDocument;

import java.nio.file.Path;
import java.util.List;

import de.donkov.flatasciiparser.model.Document;
import de.donkov.flatasciiparser.model.row.HeadRow;
import de.donkov.flatasciiparser.model.row.Row;
import de.donkov.flatasciiparser.service.output.DocumentJsonConverterService;
import de.donkov.flatasciiparser.service.output.JsonFileWriter;
import de.donkov.flatasciiparser.service.parser.DocumentParser;
import de.donkov.flatasciiparser.service.parser.FlatfileParserService;
import de.donkov.flatasciiparser.service.schema.JsonSchemaValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the flatfile parser.
 *
 * <p>The application is started from the command line with the name of the
 * input file as the only passing parameter. The actual reading logic lives in
 * the injected {@link FlatfileParserService}.
 */
@SpringBootApplication
public class FlatAsciiParserApplication implements ApplicationRunner, ExitCodeGenerator {

    private static final Logger log = LoggerFactory.getLogger(FlatAsciiParserApplication.class);

    private static final int EXIT_OK = 0;
    private static final int EXIT_READ_ERROR = 1;
    private static final int EXIT_USAGE_ERROR = 2;
    private static final int EXIT_VALIDATION_ERROR = 3;

    private final FlatfileParserService flatfileParserService;
    private final DocumentParser documentParser;
    private final DocumentJsonConverterService documentJsonConverter;
    private final JsonFileWriter jsonFileWriter;
    private final JsonSchemaValidationService jsonSchemaValidationService;

    private int exitCode = EXIT_OK;

    public FlatAsciiParserApplication(FlatfileParserService flatfileParserService, DocumentParser documentParser,
            DocumentJsonConverterService documentJsonConverter, JsonFileWriter jsonFileWriter,
            JsonSchemaValidationService jsonSchemaValidationService) {
        this.flatfileParserService = flatfileParserService;
        this.documentParser = documentParser;
        this.documentJsonConverter = documentJsonConverter;
        this.jsonFileWriter = jsonFileWriter;
        this.jsonSchemaValidationService = jsonSchemaValidationService;
    }

    static void main(String[] args) {
        SpringApplication.run(FlatAsciiParserApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        List<String> nonOptionArgs = args.getNonOptionArgs();
        if (nonOptionArgs.size() != 1) {
            log.error("Usage: java flatAsciiParser <input-file> - the application expects exactly one parameter: the name of the input file.");
            exitCode = EXIT_USAGE_ERROR;
            return;
        }

        Path inputFile = Path.of(nonOptionArgs.getFirst());
        try {
            List<Row> records = flatfileParserService.parseRecords(inputFile);
            List<Document> documents = documentParser.parse(records);
            for (int i = 0; i < documents.size(); i++) {
                Document document = documents.get(i);
                String ohtdon = document.getHead().getFieldValue(HeadRow.Field.OHTDON);
                Path outputFile = outputFileName(inputFile, ohtdon);
                JsonDocument json = documentJsonConverter.convert(document);
                jsonFileWriter.write(json, outputFile);
                log.info("Document {} ({}): {} line items, {} head texts, {} head allowances written to {}",
                        i + 1,
                        ohtdon,
                        document.getLineItems().size(),
                        document.getHeadTexts().size(),
                        document.getHeadAllowances().size(),
                        outputFile);

                var violations = jsonSchemaValidationService.validate(json);
                if (!violations.isEmpty()) {
                    exitCode = EXIT_VALIDATION_ERROR;
                    log.error("Output file {} violates the JSON schema:", outputFile);
                    violations.forEach(violation ->
                            log.error("  {}: {}", violation.getInstanceLocation(), violation.getMessage()));
                } else {
                    log.info("JSON of document {} successfully validated.", ohtdon);
                }
            }
            log.info("Documents written: {}", documents.size());
        } catch (Exception e) {
            log.error("Error while processing input file '{}': {}", inputFile, e.getMessage());
            exitCode = EXIT_READ_ERROR;
        }
    }

    /**
     * Builds the output file name according to the task specification:
     * input file name + "_" + ${OHTDON} + ".json". The file is written to the
     * directory the application was started from.
     */
    static Path outputFileName(Path inputFile, String ohtdon) {
        String inputFileName = inputFile.getFileName().toString();
        return Path.of(inputFileName + "_" + ohtdon + ".json");
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
