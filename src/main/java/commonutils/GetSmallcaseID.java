package commonutils;

/**
 * Utility class to fetch smallcase ID and name from system properties.
 * Defaults to preset values if system properties are not provided.
 */
public class GetSmallcaseID {

    /**
     * Returns the smallcase ID from system property "smallcaseID", or default "SCAW_0001".
     *
     * @return smallcase ID
     */
    public String getSCID() {
        return System.getProperty("smallcaseID", "SCAW_0001");
    }

    /**
     * Returns the smallcase name from system property "smallcaseName", or default "All Weather Investing".
     *
     * @return smallcase name
     */
    public String getSmallcaseName() {
        return System.getProperty("smallcaseName", "Timeless Asset Allocation");
    }
}