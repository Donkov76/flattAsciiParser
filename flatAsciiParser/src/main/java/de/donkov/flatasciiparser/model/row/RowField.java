package de.donkov.flatasciiparser.model.row;

/**
 * A field of a record type, identified by its name and its position in the
 * record specification. Implemented by the Field enums of the row types.
 */
public interface RowField {

    /**
     * @return the 0-based index of the field in the field list of the row
     */
    int getPos();
}
