package de.donkov.flatasciiparser;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
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

    private static final int EXIT_OK = 0;
    private static final int EXIT_READ_ERROR = 1;
    private static final int EXIT_USAGE_ERROR = 2;

    private final FlatfileParserService flatfileParserService;

    private int exitCode = EXIT_OK;

    public FlatAsciiParserApplication(FlatfileParserService flatfileParserService) {
        this.flatfileParserService = flatfileParserService;
    }

    static void main(String[] args) {
        SpringApplication.run(FlatAsciiParserApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        List<String> nonOptionArgs = args.getNonOptionArgs();
        if (nonOptionArgs.size() != 1) {
            System.err.println("Usage: java flatAsciiParser <input-file>");
            exitCode = EXIT_USAGE_ERROR;
            return;
        }

        Path inputFile = Path.of(nonOptionArgs.getFirst());
        try {
            List<String> records = flatfileParserService.parseRecords(inputFile);
            for (int i = 0; i < records.size(); i++) {
                System.out.println("Record " + (i + 1) + ": " + records.get(i));
            }
            System.out.println("Records read: " + records.size());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            exitCode = EXIT_READ_ERROR;
        }
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
