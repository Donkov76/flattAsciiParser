package de.donkov.flatasciiparser.output;

import org.springframework.util.StringUtils;

/**
 * How empty field values of the flatfile appear in the JSON output. The task
 * does not specify this, so it is configurable.
 */
public enum EmptyValueMode {

    /** Empty fields are written as empty strings, exactly as read. */
    EMPTY_STRING,

    /** Empty fields are written as JSON null. */
    NULL;

    /**
     * @return the value to write for the given raw field value
     */
    public String applyTo(String value) {
        if (StringUtils.hasLength(value)) {
            return value;
        } else {
            return this == NULL ? null : "";
        }
    }
}
