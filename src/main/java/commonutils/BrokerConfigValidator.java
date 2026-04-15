package commonutils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BrokerConfigValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(BrokerConfigValidator.class);
    private static final String BROKER_CONFIG_PATH = "src/main/java/resource/testData/broker_config.json";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");
    
    private List<Map<String, Object>> brokerConfigs;
    
    public BrokerConfigValidator() {
        loadBrokerConfigs();
    }
    
    private void loadBrokerConfigs() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            brokerConfigs = mapper.readValue(new File(BROKER_CONFIG_PATH), new TypeReference<List<Map<String, Object>>>() {});
            logger.info("Loaded {} broker configurations", brokerConfigs.size());
        } catch (IOException e) {
            logger.error("Error loading broker configurations: {}", e.getMessage());
            brokerConfigs = new ArrayList<>();
        }
    }
    
    /**
     * Validates AMO window timings for a specific broker
     * @param brokerName The broker name to validate
     * @param placeStart Unix timestamp for place order start
     * @param placeEnd Unix timestamp for place order end
     * @param cancelStart Unix timestamp for cancel order start
     * @param cancelEnd Unix timestamp for cancel order end
     * @return ValidationResult containing validation status and details
     */
    public ValidationResult validateAmoWindow(String brokerName, long placeStart, long placeEnd, 
                                           long cancelStart, long cancelEnd) {
        ValidationResult result = new ValidationResult();
        result.brokerName = brokerName;
        
        // Find broker configuration
        Map<String, Object> brokerConfig = findBrokerConfig(brokerName);
        if (brokerConfig == null) {
            result.isValid = false;
            result.errorMessage = "Broker configuration not found for: " + brokerName;
            return result;
        }
        
        // Check if AMO is allowed for this broker
        boolean amoAllowed = (Boolean) brokerConfig.get("amoAllowed");
        if (!amoAllowed) {
            result.isValid = true;
            result.message = "AMO not allowed for " + brokerName + " - No validation required";
            return result;
        }
        
        // Get expected AMO window from configuration
        String expectedAmoWindow = (String) brokerConfig.get("AMO Window");
        if ("N/A".equals(expectedAmoWindow)) {
            result.isValid = true;
            result.message = "AMO window not defined for " + brokerName + " - No validation required";
            return result;
        }
        
        // Check if it's weekend (Saturday or Sunday)
        LocalDateTime now = LocalDateTime.now();
        boolean isWeekend = now.getDayOfWeek() == DayOfWeek.SATURDAY || now.getDayOfWeek() == DayOfWeek.SUNDAY;
        
        if (isWeekend) {
            result.isValid = true;
            result.message = "Weekend detected - AMO window open entire day for " + brokerName;
            return result;
        }
        
        // Validate timings for working days (Monday to Friday)
        result = validateWorkingDayTimings(brokerName, expectedAmoWindow, placeStart, placeEnd, cancelStart, cancelEnd);
        
        return result;
    }
    
    private ValidationResult validateWorkingDayTimings(String brokerName, String expectedAmoWindow, 
                                                     long placeStart, long placeEnd, 
                                                     long cancelStart, long cancelEnd) {
        ValidationResult result = new ValidationResult();
        result.brokerName = brokerName;
        
        try {
            // Parse expected AMO window times
            List<TimeRange> expectedRanges = parseAmoWindow(expectedAmoWindow);
            
            // Convert Unix timestamps to LocalTime
            LocalTime placeStartTime = TimeConverter.convertUnixToLocalTime(placeStart);
            LocalTime placeEndTime = TimeConverter.convertUnixToLocalTime(placeEnd);
            LocalTime cancelStartTime = TimeConverter.convertUnixToLocalTime(cancelStart);
            LocalTime cancelEndTime = TimeConverter.convertUnixToLocalTime(cancelEnd);
            
            // Validate place order timings
            boolean placeValid = validateTimeRange(placeStartTime, placeEndTime, expectedRanges);
            
            // Validate cancel order timings
            boolean cancelValid = validateTimeRange(cancelStartTime, cancelEndTime, expectedRanges);
            
            if (placeValid && cancelValid) {
                result.isValid = true;
                result.message = String.format("AMO window validation passed for %s. Place: %s-%s, Cancel: %s-%s", 
                    brokerName, placeStartTime, placeEndTime, cancelStartTime, cancelEndTime);
            } else {
                result.isValid = false;
                result.errorMessage = String.format("AMO window validation failed for %s. Expected: %s, " +
                    "Actual Place: %s-%s, Actual Cancel: %s-%s", 
                    brokerName, expectedAmoWindow, placeStartTime, placeEndTime, cancelStartTime, cancelEndTime);
            }
            
        } catch (Exception e) {
            result.isValid = false;
            result.errorMessage = "Error validating AMO window for " + brokerName + ": " + e.getMessage();
        }
        
        return result;
    }
    
    private List<TimeRange> parseAmoWindow(String amoWindow) {
        List<TimeRange> ranges = new ArrayList<>();
        
        if (amoWindow == null || amoWindow.trim().isEmpty() || "N/A".equals(amoWindow)) {
            return ranges;
        }
        
        // Split by semicolon to get multiple time ranges
        String[] timeRanges = amoWindow.split(";");
        
        for (String timeRange : timeRanges) {
            timeRange = timeRange.trim();
            if (timeRange.contains(" - ")) {
                String[] times = timeRange.split(" - ");
                if (times.length == 2) {
                    try {
                        LocalTime start = parseTime(times[0].trim());
                        LocalTime end = parseTime(times[1].trim());
                        ranges.add(new TimeRange(start, end));
                    } catch (Exception e) {
                        logger.warn("Error parsing time range: {}", timeRange);
                    }
                }
            }
        }
        
        return ranges;
    }
    
    private LocalTime parseTime(String timeStr) {
        // Handle different time formats
        timeStr = timeStr.trim();
        
        // Convert to 24-hour format for easier parsing
        if (timeStr.contains("PM") || timeStr.contains("AM")) {
            // Already in 12-hour format
            return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("h:mm a"));
        } else {
            // Assume 24-hour format
            return LocalTime.parse(timeStr);
        }
    }
    
    private boolean validateTimeRange(LocalTime startTime, LocalTime endTime, List<TimeRange> expectedRanges) {
        for (TimeRange range : expectedRanges) {
            if (isTimeInRange(startTime, range) && isTimeInRange(endTime, range)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isTimeInRange(LocalTime time, TimeRange range) {
        // Handle cases where end time is next day (e.g., 6:00 PM - 9:00 AM)
        if (range.start.isAfter(range.end)) {
            return time.isAfter(range.start) || time.isBefore(range.end) || time.equals(range.start) || time.equals(range.end);
        } else {
            return !time.isBefore(range.start) && !time.isAfter(range.end);
        }
    }
    
    private Map<String, Object> findBrokerConfig(String brokerName) {
        for (Map<String, Object> config : brokerConfigs) {
            String configBrokerName = (String) config.get("Broker Name");
            if (brokerName.equalsIgnoreCase(configBrokerName)) {
                return config;
            }
        }
        return null;
    }
    
    public static class ValidationResult {
        public String brokerName;
        public boolean isValid;
        public String message;
        public String errorMessage;
        
        public String getDisplayMessage() {
            return isValid ? message : errorMessage;
        }
    }
    
    private static class TimeRange {
        public LocalTime start;
        public LocalTime end;
        
        public TimeRange(LocalTime start, LocalTime end) {
            this.start = start;
            this.end = end;
        }
    }
}
