package commonutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility class for time conversion operations
 * Provides methods to convert Unix timestamps to various time formats
 */
public class TimeConverter {

    private static final Logger logger = LoggerFactory.getLogger(TimeConverter.class);

    /**
     * Convert Unix timestamp (milliseconds) to IST format
     * @param unixTimestamp Unix timestamp in milliseconds
     * @return Formatted IST time string (yyyy-MM-dd HH:mm:ss)
     */
    public static String convertUnixToIST(long unixTimestamp) {
        try {
            SimpleDateFormat istFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            istFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            return istFormat.format(new Date(unixTimestamp));
        } catch (Exception e) {
            logger.error("Error converting Unix timestamp to IST: {}", e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Convert Unix timestamp (milliseconds) to IST format with custom pattern
     * @param unixTimestamp Unix timestamp in milliseconds
     * @param pattern Custom date pattern (e.g., "yyyy-MM-dd HH:mm:ss", "dd/MM/yyyy HH:mm")
     * @return Formatted IST time string
     */
    public static String convertUnixToIST(long unixTimestamp, String pattern) {
        try {
            SimpleDateFormat istFormat = new SimpleDateFormat(pattern);
            istFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            return istFormat.format(new Date(unixTimestamp));
        } catch (Exception e) {
            logger.error("Error converting Unix timestamp to IST with pattern {}: {}", pattern, e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Convert Unix timestamp (milliseconds) to any timezone format
     * @param unixTimestamp Unix timestamp in milliseconds
     * @param timezone Timezone (e.g., "Asia/Kolkata", "America/New_York", "UTC")
     * @param pattern Date pattern (e.g., "yyyy-MM-dd HH:mm:ss")
     * @return Formatted time string in specified timezone
     */
    public static String convertUnixToTimezone(long unixTimestamp, String timezone, String pattern) {
        try {
            SimpleDateFormat timezoneFormat = new SimpleDateFormat(pattern);
            timezoneFormat.setTimeZone(TimeZone.getTimeZone(timezone));
            return timezoneFormat.format(new Date(unixTimestamp));
        } catch (Exception e) {
            logger.error("Error converting Unix timestamp to timezone {}: {}", timezone, e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Get current IST time
     * @return Current time in IST format (yyyy-MM-dd HH:mm:ss)
     */
    public static String getCurrentISTTime() {
        return convertUnixToIST(System.currentTimeMillis());
    }

    /**
     * Get current IST time with custom pattern
     * @param pattern Custom date pattern
     * @return Current time in IST format
     */
    public static String getCurrentISTTime(String pattern) {
        return convertUnixToIST(System.currentTimeMillis(), pattern);
    }

    /**
     * Format time conversion result for reports
     * @param unixTimestamp Unix timestamp in milliseconds
     * @param label Label for the timestamp (e.g., "Start", "End")
     * @return Formatted string with IST time and Unix timestamp
     */
    public static String formatTimeForReport(long unixTimestamp, String label) {
        if (unixTimestamp == 0) {
            return String.format("%s: No Restriction (Unix: %d)", label, unixTimestamp);
        }
        String istTime = convertUnixToIST(unixTimestamp);
        return String.format("%s: %s IST (Unix: %d)", label, istTime, unixTimestamp);
    }

    /**
     * Format multiple timestamps for report
     * @param timestamps Array of Unix timestamps
     * @param labels Array of labels corresponding to timestamps
     * @return Formatted string with all timestamps
     */
    public static String formatMultipleTimesForReport(long[] timestamps, String[] labels) {
        if (timestamps.length != labels.length) {
            logger.error("Timestamps and labels arrays must have the same length");
            return "Error: Array length mismatch";
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < timestamps.length; i++) {
            result.append(formatTimeForReport(timestamps[i], labels[i]));
            if (i < timestamps.length - 1) {
                result.append("\n");
            }
        }
        return result.toString();
    }

    /**
     * Validate if timestamp is valid (zero is valid meaning no restriction, negative is invalid)
     * @param unixTimestamp Unix timestamp to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidTimestamp(long unixTimestamp) {
        return unixTimestamp >= 0;
    }

    /**
     * Get time difference between two timestamps in IST
     * @param startTimestamp Start timestamp in milliseconds
     * @param endTimestamp End timestamp in milliseconds
     * @return Time difference in IST format
     */
    public static String getTimeDifference(long startTimestamp, long endTimestamp) {
        if (!isValidTimestamp(startTimestamp) || !isValidTimestamp(endTimestamp)) {
            return "Error: Invalid timestamps";
        }

        // Handle special case where either timestamp is 0 (no restriction)
        if (startTimestamp == 0 || endTimestamp == 0) {
            return "No time restriction (0 value indicates always available)";
        }

        long difference = endTimestamp - startTimestamp;
        long hours = difference / (1000 * 60 * 60);
        long minutes = (difference % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (difference % (1000 * 60)) / 1000;

        return String.format("%d hours, %d minutes, %d seconds", hours, minutes, seconds);
    }

    /**
     * Convert Unix timestamp to LocalTime in IST
     * @param unixTimestamp Unix timestamp in milliseconds
     * @return LocalTime in IST timezone
     */
    public static LocalTime convertUnixToLocalTime(long unixTimestamp) {
        try {
            Date date = new Date(unixTimestamp);
            return date.toInstant()
                    .atZone(ZoneId.of("Asia/Kolkata"))
                    .toLocalTime();
        } catch (Exception e) {
            logger.error("Error converting Unix timestamp to LocalTime: {}", e.getMessage());
            return LocalTime.MIDNIGHT; // Return midnight as fallback
        }
    }
}
