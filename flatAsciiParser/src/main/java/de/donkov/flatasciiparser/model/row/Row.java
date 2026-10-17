package de.donkov.flatasciiparser.model.row;

import de.donkov.flatasciiparser.model.RecordType;
import java.util.List;

/**
 * A single record of the flatfile in its abstract form: the record type plus
 * the raw fields of the line, in the order defined by the file structure.
 *
 * <p>Rows are immutable. The hierarchy is sealed: the four record types of
 * the file structure are the only possible specializations.
 */
public abstract sealed class Row permits HeadRow, PosRow, TextRow, AlcRow {

    private final RecordType type;
    private final List<String> fields;

    protected Row(RecordType type, List<String> fields) {
        this.type = type;
        this.fields = List.copyOf(fields);
    }

    public static Row of(RecordType type, List<String> fields) {
        if (fields.size() != type.getFieldCount()) {
            throw new IllegalArgumentException(
                    "Record type %s expects %d fields, but got %d".formatted(type, type.getFieldCount(), fields.size()));
        }
        return switch (type) {
            case HEAD -> new HeadRow(fields);
            case POS -> new PosRow(fields);
            case TEXT -> new TextRow(fields);
            case ALC -> new AlcRow(fields);
        };
    }

    public RecordType getType() {
        return type;
    }

    /**
     * @return the raw fields of the record, including the record ID
     */
    public List<String> getFields() {
        return fields;
    }

    protected String getField(int index) {
        return fields.get(index);
    }

    public String getRecId() {
        return getField(0);
    }

    @Override
    public String toString() {
        return String.join("|", fields);
    }
}
