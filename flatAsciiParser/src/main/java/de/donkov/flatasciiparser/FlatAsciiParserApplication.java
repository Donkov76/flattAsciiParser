package de.donkov.flatasciiparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.AccessDeniedException;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the flatfile parser.
 *
 * <p>The application is started from the command line with the name of the
 * input file as the only passing parameter. It reads the ASCII flatfile record
 * by record: a record is complete as soon as the record delimiter CRLF
 * (x'0D0A') has occurred.
 */
@SpringBootApplication
public class FlatAsciiParserApplication {

    private static final int EXIT_OK = 0;
    private static final int EXIT_READ_ERROR = 1;
    private static final int EXIT_USAGE_ERROR = 2;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java flatAsciiParser <input-file>");
            System.exit(EXIT_USAGE_ERROR);
        }

        Path inputFile = Path.of(args[0]);

        try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.ISO_8859_1)) {
            String record;
            long recordCount = 0;
            while ((record = readRecord(reader)) != null) {
                recordCount++;
                System.out.println("Record " + recordCount + ": " + record);
            }
            System.out.println("Records read: " + recordCount);
        } catch (NoSuchFileException e) {
            System.err.println("Error: input file not found: " + e.getFile());
            System.exit(EXIT_READ_ERROR);
        } catch (AccessDeniedException e) {
            System.err.println("Error: access to input file denied: " + e.getFile());
            System.exit(EXIT_READ_ERROR);
        } catch (IOException e) {
            System.err.println("Error while reading input file '" + inputFile + "': " + e.getMessage());
            System.exit(EXIT_READ_ERROR);
        }
    }

    /**
     * Reads a single record. Characters are read until the record delimiter
     * CRLF has occurred; the returned record does not contain the delimiter.
     *
     * @return the next record, or {@code null} if the end of the file has
     *         been reached
     */
    static String readRecord(BufferedReader reader) throws IOException {
        StringBuilder record = new StringBuilder();
        int c;
        while ((c = reader.read()) != -1) {
            if (c == '\r') {
                int next = reader.read();
                if (next == '\n') {
                    return record.toString();
                }
                record.append('\r');
                if (next == -1) {
                    return record.toString();
                }
                record.append((char) next);
            } else {
                record.append((char) c);
            }
        }
        return record.isEmpty() ? null : record.toString();
    }
}
