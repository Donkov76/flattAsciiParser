package de.donkov.flatasciiparser.model.row;

import de.donkov.flatasciiparser.model.RecordType;
import java.util.List;

/**
 * A TEXT record: a text line in the header part or as part of a line item.
 */
public final class TextRow extends Row {

    public enum Field implements RowField {
        OTXTXT1(1, "Text line 1"),
        OTXTXT2(2, "Text line 2"),
        OTXTXT3(3, "Text line 3"),
        OTXTXT4(4, "Text line 4"),
        OTXTXT5(5, "Text line 5"),
        OTXLNG(6, "Language");

        private final int pos;
        private final String description;

        Field(int pos, String description) {
            this.pos = pos;
            this.description = description;
        }

        public int getPos() {
            return pos;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public RecordType getRecordType() {
            return RecordType.TEXT;
        }
    }

    TextRow(List<String> fields) {
        super(RecordType.TEXT, fields);
    }
}
