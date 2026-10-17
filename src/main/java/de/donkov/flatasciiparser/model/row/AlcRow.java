package de.donkov.flatasciiparser.model.row;

import de.donkov.flatasciiparser.model.RecordType;
import java.util.List;

/**
 * An ALC record: a discount or allowance in the header part or as part of a
 * line item.
 */
public final class AlcRow extends Row {

    public enum Field implements RowField {
        OZAKZA(1, "Allowance or discount"),
        OZAART(2, "Discount type"),
        OZAPRO(3, "Percentage"),
        OZAPROB(4, "Base amount"),
        OZAPROR(5, "Amount"),
        OZAMWS(6, "Tax rate");

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
            return RecordType.ALC;
        }
    }

    AlcRow(List<String> fields) {
        super(RecordType.ALC, fields);
    }
}
