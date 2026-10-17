package de.donkov.flatasciiparser.cli;

import de.donkov.flatasciiparser.io.FlatFileReader;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Command line entry point. The application is started from the command line
 * with the name of the input file as the only passing parameter.
 */
@Component
public class FlatFileCliRunner implements ApplicationRunner, ExitCodeGenerator {

    static final int EXIT_OK = 0;
    static final int EXIT_USAGE_ERROR = 2;
    static final int EXIT_READ_ERROR = 1;

    private final FlatFileReader flatFileReader;
    private int exitCode = EXIT_OK;

    public FlatFileCliRunner(FlatFileReader flatFileReader) {
        this.flatFileReader = flatFileReader;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<String> nonOptionArgs = args.getNonOptionArgs();
        if (nonOptionArgs.size() != 1) {
            System.err.println("Usage: flatAsciiParser <input-file>");
            System.err.println("The application expects exactly one parameter: the name of the input file.");
            exitCode = EXIT_USAGE_ERROR;
            return;
        }

        Path inputFile;
        try {
            inputFile = Path.of(nonOptionArgs.get(0));
        } catch (InvalidPathException e) {
            System.err.println("Error: invalid file path '" + nonOptionArgs.get(0) + "': " + e.getMessage());
            exitCode = EXIT_READ_ERROR;
            return;
        }

        try {
            process(inputFile);
        } catch (NoSuchFileException e) {
            System.err.println("Error: input file not found: " + e.getFile());
            exitCode = EXIT_READ_ERROR;
        } catch (java.nio.file.AccessDeniedException e) {
            System.err.println("Error: access to input file denied: " + e.getFile());
            exitCode = EXIT_READ_ERROR;
        } catch (IOException e) {
            System.err.println("Error while reading input file '" + inputFile + "': " + e.getMessage());
            exitCode = EXIT_READ_ERROR;
        }
    }

    private void process(Path inputFile) throws IOException {
        if (!Files.exists(inputFile)) {
            System.err.println("Error: input file not found: " + inputFile);
            exitCode = EXIT_READ_ERROR;
            return;
        }
        if (!Files.isReadable(inputFile)) {
            System.err.println("Error: input file is not readable: " + inputFile);
            exitCode = EXIT_READ_ERROR;
            return;
        }

        List<String> records = flatFileReader.readRecords(inputFile);
        Map<String, Long> recordTypeCounts = new LinkedHashMap<>();
        recordTypeCounts.put(FlatFileReader.RECORD_TYPE_HEAD, 0L);
        recordTypeCounts.put(FlatFileReader.RECORD_TYPE_TEXT, 0L);
        recordTypeCounts.put(FlatFileReader.RECORD_TYPE_ALC, 0L);
        recordTypeCounts.put(FlatFileReader.RECORD_TYPE_POS, 0L);

        for (String record : records) {
            String recordType = flatFileReader.splitFields(record)[0];
            recordTypeCounts.merge(recordType, 1L, Long::sum);
        }

        System.out.println("Input file:        " + inputFile);
        System.out.println("Records read:      " + records.size());
        recordTypeCounts.forEach((type, count) -> System.out.printf("  %-6s %6d%n", type, count));
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
