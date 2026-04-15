package commonutils;

import com.google.gson.Gson;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSAEncrypter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for generating JWE tokens for OTP service authentication.
 * Uses RSA-OAEP-256 for key encryption and A256GCM for content encryption.
 */
public class JweTokenUtil {
    private static final Logger logger = LoggerFactory.getLogger(JweTokenUtil.class.getName());
    
    // Cache the public key after first load
    private static RSAPublicKey cachedPublicKey = null;

    // Initialize BouncyCastle provider for RSA-OAEP support
    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * Generate JWE token for phone authentication
     * @param phoneNumber Phone number (e.g., "9999999999")
     * @param phoneCountryCode Country code (e.g., "+91")
     * @return JWE token string generated using public key
     * @throws IllegalStateException if public key is not configured
     * @throws RuntimeException if encryption fails
     */
    public static String generateJweToken(String phoneNumber, String phoneCountryCode) {

        Map<String, String> payload = new HashMap<>();
        payload.put("phone", phoneNumber);
        payload.put("phoneCountryCode", phoneCountryCode);

        return generateJweTokenFromPayload(payload);
    }

    /**
     * Generate JWE token for phone authentication
     * @param email Phone number (e.g., "abc@xyz.com")
     * @return JWE token string generated using public key
     * @throws IllegalStateException if public key is not configured
     * @throws RuntimeException if encryption fails
     */
    public static String generateJweToken(String email) {

        Map<String, String> payload = new HashMap<>();
        payload.put("email", email);

        return generateJweTokenFromPayload(payload);
    }

    /**
     * Generate JWE token with custom payload
     * @param payload Map containing phone and phoneCountryCode
     * @return JWE token string
     * @throws IllegalStateException if public key is not configured
     * @throws RuntimeException if encryption fails
     */
    public static String generateJweTokenFromPayload(Map<String, String> payload) {
        try {
            // Convert payload to JSON
            Gson gson = new Gson();
            String payloadJson = gson.toJson(payload);

            // Load public key
            RSAPublicKey publicKey = loadPublicKeyFromConfig();
            
            // Encrypt payload
            return encryptPayload(payloadJson, publicKey);
        } catch (Exception e) {
            logger.error("Failed to generate JWE token", e);
            throw new RuntimeException("Failed to generate JWE token: " + e.getMessage(), e);
        }
    }

    /**
     * Load and parse RSA public key from config
     * @return RSAPublicKey object
     * @throws IllegalStateException if public key is not configured
     * @throws IllegalArgumentException if public key format is invalid
     */
    private static RSAPublicKey loadPublicKeyFromConfig() {
        // Return cached key if available
        if (cachedPublicKey != null) {
            return cachedPublicKey;
        }

        // Load encryption key from GitHub secrets (through config for environment selection)
        String keyName = ConfigRead.getPropertyValue("jwe_encryption_public_key");
        String base64Key = System.getenv(keyName);
        
        if (base64Key == null || base64Key.trim().isEmpty()) {
            throw new IllegalStateException(
                    "JWE encryption public key is not configured for the selected environment." +
                            "Please set it for '" + keyName.toUpperCase() + "' in GitHub secrets.");
        }
        
        // Parse and cache the key
        cachedPublicKey = parsePublicKey(base64Key);
        return cachedPublicKey;
    }

    /**
     * Parse base64-encoded public key to RSAPublicKey
     * Handles both PEM format (with BEGIN/END headers) and raw DER format
     * @param base64Key Base64-encoded public key (can be PEM format or X.509 DER format)
     * @return RSAPublicKey object
     * @throws IllegalArgumentException if key format is invalid
     */
    private static RSAPublicKey parsePublicKey(String base64Key) {
        try {
            // First, decode the base64 to get the actual content
            byte[] decodedBytes = Base64.getDecoder().decode(base64Key.trim());
            String decodedString = new String(decodedBytes);
            
            byte[] keyBytes;
            
            // Check if it's PEM format (contains BEGIN PUBLIC KEY)
            if (decodedString.contains("BEGIN PUBLIC KEY") || decodedString.contains("BEGIN RSA PUBLIC KEY")) {
                logger.debug("Detected PEM format public key");
                // Extract the base64 content between headers
                String pemContent = decodedString
                    .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll("-----END PUBLIC KEY-----", "")
                    .replaceAll("-----BEGIN RSA PUBLIC KEY-----", "")
                    .replaceAll("-----END RSA PUBLIC KEY-----", "")
                    .replaceAll("\\s", ""); // Remove all whitespace
                
                // Decode the PEM content to get DER bytes
                keyBytes = Base64.getDecoder().decode(pemContent);
            } else {
                // Assume it's already DER format (raw base64-encoded DER)
                logger.debug("Assuming raw DER format public key");
                keyBytes = decodedBytes;
            }
            
            // Try to parse as X.509 encoded public key (most common format)
            try {
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
                logger.debug("Successfully parsed RSA public key (X.509 format)");
                return publicKey;
            } catch (Exception e) {
                // If X.509 fails, try PKCS#8 format
                logger.debug("X.509 format failed, trying PKCS#8 format");
                try {
                    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
                    logger.debug("Successfully parsed RSA public key (PKCS#8 format)");
                    return publicKey;
                } catch (Exception e2) {
                    throw new IllegalArgumentException(
                        "Failed to parse public key. Expected base64-encoded RSA public key " +
                        "(PEM format with BEGIN/END headers or X.509 DER format). " +
                        "Error: " + e2.getMessage(), e2
                    );
                }
            }
        } catch (IllegalArgumentException e) {
            throw e; // Re-throw our custom exceptions
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid public key format. Expected base64-encoded RSA public key " +
                "(PEM format with BEGIN/END headers or X.509 DER format). " +
                "Error: " + e.getMessage(), e
            );
        }
    }

    /**
     * Encrypt payload using RSA-OAEP-256 + A256GCM
     * @param payloadJson JSON string to encrypt
     * @param publicKey RSA public key
     * @return JWE token string in compact format
     * @throws RuntimeException if encryption fails
     */
    private static String encryptPayload(String payloadJson, RSAPublicKey publicKey) {
        try {
            // Create JWE header with RSA-OAEP-256 and A256GCM
            JWEHeader header = new JWEHeader.Builder(
                JWEAlgorithm.RSA_OAEP_256,  // Key encryption algorithm
                EncryptionMethod.A256GCM     // Content encryption algorithm
            ).build();
            
            // Create JWE object
            JWEObject jweObject = new JWEObject(header, new Payload(payloadJson));
            
            // Create encrypter with public key
            RSAEncrypter encrypter = new RSAEncrypter(publicKey);
            
            // Encrypt
            jweObject.encrypt(encrypter);
            
            // Serialize to compact format
            String jweToken = jweObject.serialize();
            
            logger.debug("Successfully generated JWE token");
            return jweToken;
        } catch (Exception e) {
            logger.error("Failed to encrypt payload", e);
            throw new RuntimeException("Failed to encrypt JWE payload: " + e.getMessage(), e);
        }
    }
}
