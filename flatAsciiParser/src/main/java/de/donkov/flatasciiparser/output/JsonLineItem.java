package de.donkov.flatasciiparser.output;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The JSON structure of one line item: the flat POS fields followed by the
 * texts and discounts/allowances that belong to the line item.
 *
 * <p>Extends {@link LinkedHashMap} so the field order of the file structure
 * specification is preserved in the JSON output.
 */
public class JsonLineItem extends LinkedHashMap<String, Object> {

    public static final String POS_TEXT = "POS_TEXT";
    public static final String POS_ALC = "POS_ALC";

    public JsonLineItem(Map<String, String> posFields,
            List<Map<String, String>> texts,
            List<Map<String, String>> allowances) {
        putAll(posFields);
        put(POS_TEXT, texts);
        put(POS_ALC, allowances);
    }
}
