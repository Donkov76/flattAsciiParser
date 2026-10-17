package de.donkov.flatasciiparser.service;

import de.donkov.flatasciiparser.model.Document;
import de.donkov.flatasciiparser.model.LineItem;
import de.donkov.flatasciiparser.model.row.AlcRow;
import de.donkov.flatasciiparser.model.row.HeadRow;
import de.donkov.flatasciiparser.model.row.PosRow;
import de.donkov.flatasciiparser.model.row.Row;
import de.donkov.flatasciiparser.model.row.TextRow;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Groups the rows of a flatfile into documents.
 *
 * <p>The file structure defines a hierarchy: a HEAD record starts a new
 * document. TEXT and ALC records belong to the header part as long as no POS
 * record has occurred, afterwards they belong to the current line item. A
 * document or line item is created only when it is complete, so the model
 * classes receive fully populated lists in their constructor.
 */
@Service
public class DocumentParser {

    /**
     * Groups the given rows into documents. Because each record of the
     * flatfile is one line, the 1-based position in the list is the line
     * number in the input file.
     *
     * @return the documents in file order
     * @throws FlatfileFormatException if a record occurs before the first
     *         HEAD record
     */
    public List<Document> parse(List<Row> rows) {
        List<Document> documents = new ArrayList<>();

        HeadRow head = null;
        List<TextRow> headTexts = new ArrayList<>();
        List<AlcRow> headAllowances = new ArrayList<>();
        List<LineItem> lineItems = new ArrayList<>();
        PosRow pos = null;
        List<TextRow> itemTexts = new ArrayList<>();
        List<AlcRow> itemAllowances = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            Row row = rows.get(i);
            int lineNumber = i + 1;

            switch (row) {
                case HeadRow nextHead -> {
                    if (head != null) {
                        closeLineItem(pos, itemTexts, itemAllowances, lineItems);
                        documents.add(new Document(head, headTexts, headAllowances, lineItems));
                    }
                    head = nextHead;
                    headTexts = new ArrayList<>();
                    headAllowances = new ArrayList<>();
                    lineItems = new ArrayList<>();
                    pos = null;
                    itemTexts = new ArrayList<>();
                    itemAllowances = new ArrayList<>();
                }
                case PosRow nextPos -> {
                    closeLineItem(pos, itemTexts, itemAllowances, lineItems);
                    requireHead(head, lineNumber);
                    pos = nextPos;
                    itemTexts = new ArrayList<>();
                    itemAllowances = new ArrayList<>();
                }
                case TextRow text -> {
                    if (pos != null) {
                        itemTexts.add(text);
                    } else {
                        requireHead(head, lineNumber);
                        headTexts.add(text);
                    }
                }
                case AlcRow alc -> {
                    if (pos != null) {
                        itemAllowances.add(alc);
                    } else {
                        requireHead(head, lineNumber);
                        headAllowances.add(alc);
                    }
                }
            }
        }

        if (head != null) {
            closeLineItem(pos, itemTexts, itemAllowances, lineItems);
            documents.add(new Document(head, headTexts, headAllowances, lineItems));
        }

        return documents;
    }

    private void closeLineItem(PosRow pos, List<TextRow> itemTexts, List<AlcRow> itemAllowances,
            List<LineItem> lineItems) {
        if (pos != null) {
            lineItems.add(new LineItem(pos, itemTexts, itemAllowances));
        }
    }

    private void requireHead(HeadRow head, int lineNumber) {
        if (head == null) {
            throw new FlatfileFormatException(lineNumber,
                    "Record requires a preceding HEAD record");
        }
    }
}
