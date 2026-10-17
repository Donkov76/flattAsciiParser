package de.donkov.flatasciiparser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import de.donkov.flatasciiparser.service.FlatfileParserService;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link FlatfileParserService} against the sample flatfile.
 */
class FlatfileParserServiceTest {

    private static final String SAMPLE_FILE = "flatfile/4311560000000_IV_4260043070002_20200527133435";

    private final FlatfileParserService flatfileParserService = new FlatfileParserService();

    private Path sampleFile() throws URISyntaxException {
        var url = getClass().getClassLoader().getResource(SAMPLE_FILE);
        if (url == null) {
            throw new IllegalStateException("Sample flatfile not found in test resources: " + SAMPLE_FILE);
        }
        return Path.of(url.toURI());
    }

    @Test
    void readsAllRecordsFromSampleFile() throws IOException, URISyntaxException {
        List<String> records = flatfileParserService.parseRecords(sampleFile());

        assertEquals(130, records.size(),
                "The sample flatfile contains 130 records in total");
    }

    @Test
    void readsHeadRecordsFromSampleFile() throws IOException, URISyntaxException {
        List<String> records = flatfileParserService.parseRecords(sampleFile());

        long headCount = records.stream()
                .filter(record -> record.startsWith("HEAD|"))
                .count();

        assertEquals(15, headCount,
                "The sample flatfile contains 15 HEAD records");
    }
}
