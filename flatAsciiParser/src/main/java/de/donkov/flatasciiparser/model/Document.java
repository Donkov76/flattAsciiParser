package de.donkov.flatasciiparser.model;

import de.donkov.flatasciiparser.model.row.AlcRow;
import de.donkov.flatasciiparser.model.row.HeadRow;
import de.donkov.flatasciiparser.model.row.TextRow;
import java.util.List;

/**
 * A document of the flatfile: one HEAD record with the records that belong to
 * it, grouped into header texts, header discounts/allowances, and line items.
 *
 * <p>Documents are immutable.
 */
public class Document {

    private final HeadRow head;
    private final List<TextRow> headTexts;
    private final List<AlcRow> headAllowances;
    private final List<LineItem> lineItems;

    public Document(HeadRow head, List<TextRow> headTexts, List<AlcRow> headAllowances,
            List<LineItem> lineItems) {
        this.head = head;
        this.headTexts = List.copyOf(headTexts);
        this.headAllowances = List.copyOf(headAllowances);
        this.lineItems = List.copyOf(lineItems);
    }

    public HeadRow getHead() {
        return head;
    }

    public List<TextRow> getHeadTexts() {
        return headTexts;
    }

    public List<AlcRow> getHeadAllowances() {
        return headAllowances;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }
}
