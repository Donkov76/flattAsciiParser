package de.donkov.flatasciiparser.model.row;

import de.donkov.flatasciiparser.model.RecordType;
import java.util.List;

/**
 * A HEAD record: the header of a document with the invoice header fields.
 */
public non-sealed class HeadRow extends Row {

    public enum Field {
        OHTRCP(1, "GLN recipient"),
        OHTSND(2, "GLN originator"),
        OHTDOT(3, "Document type"),
        OHTDOD(4, "Document date"),
        OHTDON(5, "Document number"),
        OHTNRF(6, "Duplicate or original"),
        OHTDLD(7, "Delivery date"),
        OHTON(8, "Order number"),
        OHTOND(9, "Order date"),
        OHTDQ(10, "Delivery note number"),
        OHTDQD(11, "Delivery note date"),
        OHTAKN(12, "Contract number"),
        OHTUSB(13, "Original document number"),
        OHTUSBD(14, "Original document date"),
        OHTIBY(15, "GLN buyer"),
        OHTVABY(16, "VAT buyer"),
        OHTIIV(17, "GLN invoice recipient"),
        OHTVAIV(18, "VAT invoice recipient"),
        OHTIDP(19, "GLN delivery party"),
        OHTLN1(20, "Delivery name 1"),
        OHTLN2(21, "Delivery name 2"),
        OHTLN3(22, "Delivery name 3"),
        OHTLN4(23, "Delivery name 4"),
        OHTLN5(24, "Delivery name 5"),
        OHTLS1(25, "Delivery street 1"),
        OHTLS2(26, "Delivery street 2"),
        OHTLS3(27, "Delivery street 3"),
        OHTLS4(28, "Delivery street 4"),
        OHTLOR(29, "Delivery city"),
        OHTLPL(30, "Delivery ZIP code"),
        OHTLLA(31, "Delivery country code"),
        OHTISU(32, "GLN supplier"),
        OHTRSU(33, "Supplier reference"),
        OHTVASU(34, "VAT supplier"),
        OHTIPE(35, "GLN payment recipient"),
        OHTVAPE(36, "VAT payment recipient"),
        OHTIPR(37, "GLN payee"),
        OHTMWS(38, "Tax rate"),
        OHTRFW(39, "Currency"),
        OHTFOA(40, "Net due date"),
        OHTGES(41, "Total amount"),
        OHTZAB(42, "Total payment amount"),
        OHTGPB(43, "Total line item amount"),
        OHTSPB(44, "Total tax base amount"),
        OHTGSB(45, "Total tax"),
        OHTINVTYP(46, "Invoice type"),
        OHTPDF(47, "PDF filename");

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

    HeadRow(List<String> fields) {
        super(RecordType.HEAD, fields);
    }

    public String getFieldValue(Field field) {
        return getField(field.pos);
    }
}
