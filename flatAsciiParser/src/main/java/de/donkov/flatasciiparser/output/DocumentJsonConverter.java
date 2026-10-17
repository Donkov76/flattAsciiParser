package de.donkov.flatasciiparser.output;

import de.donkov.flatasciiparser.model.Document;
import de.donkov.flatasciiparser.model.LineItem;
import de.donkov.flatasciiparser.model.row.AlcRow;
import de.donkov.flatasciiparser.model.row.HeadRow;
import de.donkov.flatasciiparser.model.row.PosRow;
import de.donkov.flatasciiparser.model.row.Row;
import de.donkov.flatasciiparser.model.row.RowField;
import de.donkov.flatasciiparser.model.row.TextRow;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Converts a {@link Document} into the JSON structure of the output file.
 *
 * <p>All field values are taken over as strings, unchanged: the task requires
 * a conversion of the structure, not of the data types. The fields are
 * mapped by iterating over the Field enums of the row types, so the field
 * order of the file structure specification is preserved. How empty field
 * values appear in the output is configurable via the property
 * {@code flatasciiparser.output.empty-values}.
 */
@Component
public class DocumentJsonConverter {

    private final EmptyValueMode emptyValueMode;

    public DocumentJsonConverter(
            @Value("${flatasciiparser.output.empty-values:EMPTY_STRING}") EmptyValueMode emptyValueMode) {
        this.emptyValueMode = emptyValueMode;
    }

    public JsonDocument convert(Document document) {
        return new JsonDocument(
                mapFields(document.getHead(), HeadRow.Field.values()),
                document.getHeadTexts().stream()
                        .map(text -> mapFields(text, TextRow.Field.values()))
                        .toList(),
                document.getHeadAllowances().stream()
                        .map(alc -> mapFields(alc, AlcRow.Field.values()))
                        .toList(),
                document.getLineItems().stream()
                        .map(this::convert)
                        .toList());
    }

    public JsonLineItem convert(LineItem lineItem) {
        return new JsonLineItem(
                mapFields(lineItem.getPos(), PosRow.Field.values()),
                lineItem.getTexts().stream()
                        .map(text -> mapFields(text, TextRow.Field.values()))
                        .toList(),
                lineItem.getAllowances().stream()
                        .map(alc -> mapFields(alc, AlcRow.Field.values()))
                        .toList());
    }

    /**
     * Maps a row to its fields, using the record ID plus one entry per field
     * of the record type, in specification order.
     */
    private <F extends Enum<F> & RowField> Map<String, String> mapFields(Row row, F[] fields) {
        Map<String, String> fieldMap = new LinkedHashMap<>();
        fieldMap.put(JsonDocument.RECID, row.getRecId());
        for (F field : fields) {
            fieldMap.put(field.name(), emptyValueMode.applyTo(row.getFieldValue(field)));
        }
        return fieldMap;
    }
}
