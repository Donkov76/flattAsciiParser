package de.donkov.flatasciiparser.model.json;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The JSON structure of one output file: the flat HEAD fields at the root
 * level, followed by the header texts, the header discounts/allowances, and
 * the line items.
 *
 * <p>Extends {@link LinkedHashMap} so the field order of the file structure
 * specification is preserved in the JSON output: the keys appear in insertion
 * order.
 */
public class JsonDocument extends LinkedHashMap<String, Object> {

    public static final String RECID = "RECID";
    public static final String HEAD_TEXT = "HEAD_TEXT";
    public static final String HEAD_ALC = "HEAD_ALC";
    public static final String POS = "POS";

    public JsonDocument(Map<String, String> headFields,
            List<Map<String, String>> headTexts,
            List<Map<String, String>> headAllowances,
            List<JsonLineItem> lineItems) {
        putAll(headFields);
        put(HEAD_TEXT, headTexts);
        put(HEAD_ALC, headAllowances);
        put(POS, lineItems);
    }
}
