package co.platform;

import java.util.*;

public class QuoteGenerator {
    public static final double WMAX = 1000.0;
    public static final double RISK_CALC_CONSTANT = 0.0015;
    public static final String BUNDLE_QUOTE_IDENTIFIER = "Bundle:";
    public static final String NAMED_ITEM_QUOTE_IDENTIFIER = "NamedItem:";

    //constant map holding the possible values of cover in a list (values) for each section type where bundles are allowed (key)
    public static final HashMap<String, List<Integer>> BUNDLE_VALUE_OPTIONS;

    static {
        BUNDLE_VALUE_OPTIONS = new HashMap<>();
        BUNDLE_VALUE_OPTIONS.put("General", new ArrayList<>(Arrays.asList(2500, 5000, 10000, 15000)));
        BUNDLE_VALUE_OPTIONS.put("Jewelry", new ArrayList<>(Arrays.asList(1000, 2000, 3000, 4000, 5000)));
    }

    //constant map holding the possible values of excess in a list (values) for each section type (key)
    public static final HashMap<String, List<Integer>> EXCESS_OPTIONS;

    static {
        EXCESS_OPTIONS = new HashMap<>();
        EXCESS_OPTIONS.put("General", new ArrayList<>(Arrays.asList(200, 300, 400)));
        EXCESS_OPTIONS.put("Jewelry", new ArrayList<>(Arrays.asList(100, 200, 300)));
        EXCESS_OPTIONS.put("Electronics", new ArrayList<>(Arrays.asList(100, 200, 300, 400, 500)));
        EXCESS_OPTIONS.put("Bicycles", new ArrayList<>(Arrays.asList(300, 400, 500)));
    }

    //constant map holding the multipliers(values) for each section type (key)
    public static final HashMap<String, Double> MULTIPLIER_FOR_SECTION;

    static {
        MULTIPLIER_FOR_SECTION = new HashMap<>();
        MULTIPLIER_FOR_SECTION.put("General", 0.1);
        MULTIPLIER_FOR_SECTION.put("Jewelry", 2.0);
        MULTIPLIER_FOR_SECTION.put("Electronics", 1.0);
        MULTIPLIER_FOR_SECTION.put("Bicycles", 0.8);

    }


    /**
     * Method to generate price matrix for covers requested. Calls functions to generate named item
     * and bundle quotes, returns map containing list of quotes for each cover requested with key being section name
     * cover requested for (for bundle covers) or name of item(for named item covers) and the value being the set of
     * possible quotes (containing excess, value and price).
     *
     * @param wriskScore int
     * @param bundles    List<String>
     * @param namedItems List<String>
     * @return HashMap<String       ,       Set       <       Quote>>  all possible options for the covers requested
     * @throws Exception
     */
    public HashMap<String, Set<Quote>> generateQuotes(double wriskScore, List<String> bundles, List<String> namedItems)
            throws Exception {
        if (wriskScore <= 0)
            throw new InvalidQuoteRequestException("Invalid request: wrisk score has to be greater than zero!");
        if (bundles.isEmpty() || namedItems.isEmpty())
            throw new InvalidQuoteRequestException("Invalid request: no cover has been requested!");
        HashMap<String, Set<Quote>> quotes = new HashMap<>();
        //performs calculation common for all price calculations of request for efficiency
        double riskQuotient = (wriskScore / WMAX) * RISK_CALC_CONSTANT;
        //generates quotes for bundle of each section requested for
        for (String s : bundles) {
            Set<Quote> bundleQuotes = generateBundleQuotes(s, riskQuotient);
            quotes.put((BUNDLE_QUOTE_IDENTIFIER + s), bundleQuotes);
        }
        //generates quotes for each named item section requested
        for (String item : namedItems) {
            Set<Quote> namedItemQuotes = generateNamedItemQuotes(item, riskQuotient);
            quotes.put((NAMED_ITEM_QUOTE_IDENTIFIER + item), namedItemQuotes);
        }
        return quotes;
    }

    /**
     * Generates quotes for all possible value-excess combinations of a bundle cover requested
     *
     * @param s            String section cover requested for
     * @param riskQuotient double parameter used in price calculation
     * @return quotes Set<Quote> set of all possible quotes for request
     * @throws Exception
     */
    public Set<Quote> generateBundleQuotes(String s, double riskQuotient) throws Exception {
        Set<Quote> bundleQuotes = new HashSet<>();
        if (!BUNDLE_VALUE_OPTIONS.keySet().contains(s)) {
            throw new InvalidQuoteRequestException("Invalid bundle request: can only request bundle for General or Jewelry sections!");
        }
        double multiplier = MULTIPLIER_FOR_SECTION.get(s);
        for (int value : BUNDLE_VALUE_OPTIONS.get(s)) {
            for (int excess : EXCESS_OPTIONS.get(s)) {
                Quote qu = new Quote(value, excess);
                qu.setPrice(calculatePrice(riskQuotient, multiplier, (double) value, (double) excess));
                bundleQuotes.add(qu);
            }
        }
        return bundleQuotes;
    }

    /**
     * Generates quotes for all excess values of a named item cover requested
     *
     * @param item         String of following format: item name:section:value
     * @param riskQuotient double parameter used in price calculation
     * @return quotes Set<Quote> set of all quotes for request
     * @throws Exception
     */
    public Set<Quote> generateNamedItemQuotes(String item, double riskQuotient) throws Exception {
        //parsing item information to establish item name, section and value of named item cover requested for
        String s;
        int value;
        try {
            String[] pItem = item.split(":");
            s = pItem[1];
            value = Integer.valueOf(pItem[2]);
        } catch (Exception e) {
            throw new InvalidQuoteRequestException("Invalid named item request: item name is expected to be in format [Name]:[Category]:[Value(integer)]," +
                    "actual item name: " + item + "!");
        }
        if (value <= 0) {
            throw new InvalidQuoteRequestException("Invalid named item request: item value has to be greater than zero!");
        }
        //also need to check if s empty or null?
        if (!MULTIPLIER_FOR_SECTION.keySet().contains(s)) {
            throw new InvalidQuoteRequestException("Invalid named item request: section [" + s + "] invalid!");
        }
        //generating quote for each excess-value combination, calculating its price, adding it to set of all quotes
        Set<Quote> namedItemQuotes = new HashSet<>();
        double multiplier = MULTIPLIER_FOR_SECTION.get(s);
        for (int excess : EXCESS_OPTIONS.get(s)) {
            Quote qu = new Quote(value, excess);
            qu.setPrice(calculatePrice(riskQuotient, multiplier, (double) value, (double) excess));
            namedItemQuotes.add(qu);
        }
        return namedItemQuotes;


    }

    /**
     * @param riskQuotient
     * @param multiplier
     * @param value
     * @param excess
     * @return
     */

    public static double calculatePrice(double riskQuotient, double multiplier, double value, double excess) {
        return riskQuotient * multiplier * value * (1.0 - (excess / value));
    }


}