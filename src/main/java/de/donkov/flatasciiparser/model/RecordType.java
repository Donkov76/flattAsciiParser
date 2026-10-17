package de.donkov.flatasciiparser.model;

/**
 * The type of a record in the flatfile, identified by the record ID in the
 * first field of each record.
 *
 * <p>Each record type has a fixed number of fields as defined in the file
 * structure specification.
 */
public enum RecordType {

    /** Header record: must be the first record of a document, occurs once. */
    HEAD(48),

    /** Header text or line item text. */
    TEXT(7),

    /** Header discount/allowance or line item discount/allowance. */
    ALC(7),

    /** Line item record. */
    POS(16);

    private final int fieldCount;

    RecordType(int fieldCount) {
        this.fieldCount = fieldCount;
    }

    /**
     * @return the exact number of fields a record of this type consists of
     *         (including the record ID)
     */
    public int getFieldCount() {
        return fieldCount;
    }

    /**
     * Resolves the record type from the record ID.
     *
     * @param recordId the content of the first field of a record
     * @return the matching record type
     * @throws IllegalArgumentException if the record ID is unknown
     */
    public static RecordType fromRecordId(String recordId) {
        for (RecordType type : values()) {
            if (type.name().equals(recordId)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown record ID: " + recordId);
    }
}
