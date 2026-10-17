package de.donkov.flatasciiparser.service.parser;

/**
 * Thrown when a line of the flatfile does not conform to the file structure
 * specification (unknown record ID or wrong number of fields).
 */
public class FlatfileFormatException extends RuntimeException {

    private final int lineNumber;

    public FlatfileFormatException(int lineNumber, String message) {
        super("Line %d: %s".formatted(lineNumber, message));
        this.lineNumber = lineNumber;
    }

    /**
     * @return the 1-based line number in the input file where the error occurred
     */
    public int getLineNumber() {
        return lineNumber;
    }
}
