package commonutils;

import java.util.Random;

public class PhoneAndEmailGenerator {
    private static final String[] DOMAINS = {"gmail.com", "yahoo.com"};
    private static final String[] PREFIXES = {"BEAutomationuser", "BETestuser"};
    private static final Random RANDOM = new Random();

    /**
     * Generates a random email address.
     *
     * @return A randomly generated email address.
     */
    public static String generateRandomEmail() {
        String prefix = PREFIXES[RANDOM.nextInt(PREFIXES.length)];
        String domain = DOMAINS[RANDOM.nextInt(DOMAINS.length)];
        String randomNumber = Integer.toString(RANDOM.nextInt(100));
        return prefix + randomNumber + "@" + domain;
    }

    /**
     * Generates a random 10-digit phone number.
     *
     * @return A randomly generated phone number.
     */
    public static String generatePhoneNumber() {
        Random random = new Random();
        int firstDigit = random.nextInt(4) + 6;
        String middleDigits = String.format("%09d", random.nextInt(1_000_000_000));
        return firstDigit + middleDigits;
    }

    /**
     * Generates a random OTP.
     *
     * @return A randomly generated 4-digit OTP.
     */
    public static String generateOtp() {
        Random random = new Random();
        return String.valueOf(1000 + random.nextInt(9000));
    }
}
