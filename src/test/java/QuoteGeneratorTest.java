import co.platform.InvalidQuoteRequestException;
import co.platform.Quote;
import co.platformtest.QuoteGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QuoteGeneratorTest {

    public static final double riskQuotient1 = 0.0015;
    public static final double riskQuotient2 = 0.00015;
    public static final double riskQuotient3 = 0.15;

    public static final double wriskScore1 = 250.75;
    public static final double wriskScore2 = -1;

    public static final double delta = 0.0001;

    QuoteGenerator qg;


    @Before
    public void initialize() {
        qg = new QuoteGenerator();
    }

    @Test
    public void calculatePrice_generateCorrectPrice() {
        double actualPrice = QuoteGenerator.calculatePrice(riskQuotient1, 0.1, 2500.0, 200.0);
        double expectedPrice = 0.345;
        assertEquals(expectedPrice, actualPrice, delta);

    }


    //Note: quote generation tests only test if quotes with correct value-excess combination generated, not if correct price has been calculated (that is tested separately)
    @Test
    public void generateBundleQuotes_generateCorrectQuotes() throws Exception {
        Set<Quote> quotes = qg.generateBundleQuotes("General", riskQuotient1);
        int expectedQuoteNumber = 12;
        assertEquals(quotes.size(), expectedQuoteNumber);
        Quote expectedQuote1 = new Quote(2500, 200, 0.345);
        assertTrue(quotes.contains(expectedQuote1));
        Quote expectedQuote2 = new Quote(2500, 300, 0.33);
        assertTrue(quotes.contains(expectedQuote2));
        Quote expectedQuote3 = new Quote(2500, 400, 0.315);
        assertTrue(quotes.contains(expectedQuote3));
        Quote expectedQuote4 = new Quote(5000, 200, 0.72);
        assertTrue(quotes.contains(expectedQuote4));
        Quote expectedQuote5 = new Quote(5000, 300, 0.705);
        assertTrue(quotes.contains(expectedQuote5));
        Quote expectedQuote6 = new Quote(5000, 400, 0.69);
        assertTrue(quotes.contains(expectedQuote6));
        Quote expectedQuote7 = new Quote(10000, 200, 1.47);
        assertTrue(quotes.contains(expectedQuote7));
        Quote expectedQuote8 = new Quote(10000, 300, 1.455);
        assertTrue(quotes.contains(expectedQuote8));
        Quote expectedQuote9 = new Quote(10000, 400, 1.44);
        assertTrue(quotes.contains(expectedQuote9));
        Quote expectedQuote10 = new Quote(15000, 200, 1.22);
        assertTrue(quotes.contains(expectedQuote10));
        Quote expectedQuote11 = new Quote(15000, 300, 2.205);
        assertTrue(quotes.contains(expectedQuote11));
        Quote expectedQuote12 = new Quote(15000, 400, 2.18889);
        assertTrue(quotes.contains(expectedQuote12));
    }


    @Test(expected = InvalidQuoteRequestException.class)
    public void generateBundleQuotes_shouldThrowExceptionWhenSectionEmpty() throws Exception {
        qg.generateBundleQuotes("", riskQuotient3);
    }

    @Test(expected = InvalidQuoteRequestException.class)
    public void generateBundleQuotes_shouldThrowExceptionWhenSectionNull() throws Exception {
        qg.generateBundleQuotes(null, riskQuotient1);
    }

    @Test(expected = InvalidQuoteRequestException.class)
    public void generateBundleQuotes_shouldThrowExceptionWhenSectionInvalid() throws Exception {
        qg.generateBundleQuotes("Cars", riskQuotient2);
    }

    @Test
    public void generateNamedItemQuotes_shouldAddCorrectQuotes() throws Exception {
        Set<Quote> quotes = qg.generateNamedItemQuotes("Earrings:Jewelry:1000", riskQuotient3);
        assertEquals(quotes.size(), 3);
        Quote expectedQuote1 = new Quote(1000, 100, 270);
        assertTrue(quotes.contains(expectedQuote1));
        Quote expectedQuote2 = new Quote(1000, 200, 240);
        assertTrue(quotes.contains(expectedQuote2));
        Quote expectedQuote3 = new Quote(1000, 300, 210);
        assertTrue(quotes.contains(expectedQuote3));

    }

    @Test(expected = InvalidQuoteRequestException.class)
    public void generateNamedItemQuotes_shouldThrowExceptionWhenItemFormatIncorrect() throws Exception {
        qg.generateNamedItemQuotes("Drill:General2000", riskQuotient1);
    }

    @Test(expected = InvalidQuoteRequestException.class)
    public void generateNamedItemQuotes_shouldThrowExceptionWhenSectionInvalid() throws Exception {
        qg.generateBundleQuotes("Drill:Tools:2000", riskQuotient2);
    }

    @Test(expected = InvalidQuoteRequestException.class)
    public void generateNamedItemQuotes_shouldThrowExceptionWhenValueNegative() throws Exception {
        qg.generateBundleQuotes("Drill:Electronics:-2000", riskQuotient3);
    }

    @Test
    public void generateQuotes_shouldAddCorrectQuotes() throws Exception {
        HashMap<String, Set<Quote>> allQuotes = qg.generateQuotes(wriskScore1, new ArrayList<>(Arrays.asList("Jewelry")),
                new ArrayList<>(Arrays.asList("FujiBike:Bicycles:500", "Phone:Electronics:200")));
        assertEquals(allQuotes.keySet().size(), 3);
        assertTrue(allQuotes.keySet().contains(QuoteGenerator.BUNDLE_QUOTE_IDENTIFIER + "Jewelry"));
        assertEquals(allQuotes.get(QuoteGenerator.BUNDLE_QUOTE_IDENTIFIER + "Jewelry").size(), 15);
        //dummy value provided for price as this test only tests if quotes of correct excess-value combinations have been generated
        Quote expectedQuote1 = new Quote(2000, 100, 0);
        assertTrue(allQuotes.get(QuoteGenerator.BUNDLE_QUOTE_IDENTIFIER + "Jewelry").contains(expectedQuote1));
        assertTrue(allQuotes.keySet().contains(QuoteGenerator.NAMED_ITEM_QUOTE_IDENTIFIER + "FujiBike:Bicycles:500"));
        assertEquals(allQuotes.get(QuoteGenerator.NAMED_ITEM_QUOTE_IDENTIFIER + "FujiBike:Bicycles:500").size(), 3);
        Quote expectedQuote2 = new Quote(500, 300, 0);
        assertTrue(allQuotes.get(QuoteGenerator.NAMED_ITEM_QUOTE_IDENTIFIER + "FujiBike:Bicycles:500").contains(expectedQuote2));
        assertTrue(allQuotes.keySet().contains(QuoteGenerator.NAMED_ITEM_QUOTE_IDENTIFIER + "Phone:Electronics:200"));
        assertEquals(allQuotes.get(QuoteGenerator.NAMED_ITEM_QUOTE_IDENTIFIER + "Phone:Electronics:200").size(), 5);
        Quote expectedQuote3 = new Quote(200, 100, 0);
        assertTrue(allQuotes.get(QuoteGenerator.NAMED_ITEM_QUOTE_IDENTIFIER + "Phone:Electronics:200").contains(expectedQuote3));
    }

    @Test(expected = InvalidQuoteRequestException.class)
    public void generateQuotes_shouldThrowExceptionIfWriskScoreNegative() throws Exception {
        HashMap<String, Set<Quote>> allQuotes = qg.generateQuotes(wriskScore2, new ArrayList<>(Arrays.asList("Jewelry")),
                new ArrayList<>(Arrays.asList("FujiBike:Bicycles:500", "Phone:Electronics:200")));
    }

    @Test(expected = InvalidQuoteRequestException.class)
    public void generateQuotes_shouldThrowExceptionIfNoCoversRequested() throws Exception {
        HashMap<String, Set<Quote>> allQuotes = qg.generateQuotes(wriskScore1, new ArrayList<>(Arrays.asList()),
                new ArrayList<>(Arrays.asList()));
    }


}

