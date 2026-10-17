package de.donkov.flatasciiparser.model.row;

import de.donkov.flatasciiparser.model.RecordType;
import java.util.List;

/**
 * A POS record: a single line item of a document.
 */
public non-sealed class PosRow extends Row {

    public enum Field {
        OPOLDF(1, "Line item counter"),
        OPOEAN(2, "EAN article number"),
        OPOSA(3, "Supplier article number"),
        OPOZTN(4, "Customs tariff number"),
        OPOALT(5, "Item description"),
        OPOBEM(6, "Invoiced quantity"),
        OPOBEE(7, "Quantity unit"),
        OPOMOB(8, "Free quantity"),
        OPOMOE(9, "Quantity unit"),
        OPOPOB(10, "Total line item amount"),
        OPOGZA(11, "Total discount/allowance"),
        OPOEIP(12, "Base price"),
        OPOEIPE(13, "Price unit"),
        OPOEIPB(14, "Price unit base"),
        OPOMWS(15, "Tax rate");

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
    }

    PosRow(List<String> fields) {
        super(RecordType.POS, fields);
    }

    public String getFieldValue(Field field) {
        return getField(field.pos);
    }
}
