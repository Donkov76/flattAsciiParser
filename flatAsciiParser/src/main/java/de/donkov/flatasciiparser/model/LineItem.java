package de.donkov.flatasciiparser.model;

import de.donkov.flatasciiparser.model.row.AlcRow;
import de.donkov.flatasciiparser.model.row.PosRow;
import de.donkov.flatasciiparser.model.row.TextRow;
import java.util.List;

/**
 * A line item of a document: one POS record with the TEXT and ALC records
 * that belong to it.
 *
 * <p>Line items are immutable.
 */
public class LineItem {

    private final PosRow pos;
    private final List<TextRow> texts;
    private final List<AlcRow> allowances;

    public LineItem(PosRow pos, List<TextRow> texts, List<AlcRow> allowances) {
        this.pos = pos;
        this.texts = List.copyOf(texts);
        this.allowances = List.copyOf(allowances);
    }

    public PosRow getPos() {
        return pos;
    }

    public List<TextRow> getTexts() {
        return texts;
    }

    public List<AlcRow> getAllowances() {
        return allowances;
    }
}
