package de.donkov.flatasciiparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.donkov.flatasciiparser.model.row.AlcRow;
import de.donkov.flatasciiparser.model.row.PosRow;
import de.donkov.flatasciiparser.model.row.Row;
import de.donkov.flatasciiparser.model.row.TextRow;
import de.donkov.flatasciiparser.service.parser.FlatfileFormatException;
import de.donkov.flatasciiparser.service.parser.RowParser;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link RowParser}.
 */
class RowParserTest {

    private final RowParser rowParser = new RowParser();

    @Test
    void parsesPosRecordIntoPosRow() {
        Row row = rowParser.parse(
                "POS|1|4055303000147|32z4039|34029010|MERRYCHEF OFENREINIGER Deutsches Et|6,00|EA|0,00|EA|56,58|0,00|9,43|EA|1,00|19,00",
                7);

        PosRow posRow = assertInstanceOf(PosRow.class, row);
        assertEquals(16, posRow.getFields().size());
        assertEquals("1", posRow.getFieldValue(PosRow.Field.OPOLDF));
        assertEquals("4055303000147", posRow.getFieldValue(PosRow.Field.OPOEAN));
    }

    @Test
    void parsesAlcRecordIntoAlcRow() {
        Row row = rowParser.parse("ALC|C|DI|0,50|56,58|0,28|0,00", 5);

        AlcRow alcRow = assertInstanceOf(AlcRow.class, row);
        assertEquals("C", alcRow.getFieldValue(AlcRow.Field.OZAKZA));
        assertEquals("DI", alcRow.getFieldValue(AlcRow.Field.OZAART));
        assertEquals("0,50", alcRow.getFieldValue(AlcRow.Field.OZAPRO));
    }

    @Test
    void preservesEmptyFields() {
        Row row = rowParser.parse("TEXT|Kunde:|||||GE", 1);

        TextRow textRow = assertInstanceOf(TextRow.class, row);
        assertEquals(7, textRow.getFields().size());
        assertEquals("Kunde:", textRow.getFieldValue(TextRow.Field.OTXTXT1));
        assertEquals("", textRow.getFieldValue(TextRow.Field.OTXTXT2));
        assertEquals("", textRow.getFieldValue(TextRow.Field.OTXTXT5));
        assertEquals("GE", textRow.getFieldValue(TextRow.Field.OTXLNG));
    }

    @Test
    void rejectsUnknownRecordIdWithLineNumber() {
        FlatfileFormatException exception = assertThrows(FlatfileFormatException.class,
                () -> rowParser.parse("FOOT|irgendwas", 42));

        assertEquals(42, exception.getLineNumber());
    }

    @Test
    void rejectsRecordWithWrongFieldCountWithLineNumber() {
        FlatfileFormatException exception = assertThrows(FlatfileFormatException.class,
                () -> rowParser.parse("TEXT|nur|zwei|felder", 13));

        assertEquals(13, exception.getLineNumber());
    }
}
