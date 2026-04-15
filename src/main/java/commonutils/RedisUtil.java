package commonutils;

import redis.clients.jedis.*;
import java.util.*;

public class RedisUtil {
    private static JedisSentinelPool jedisSentinelPool;

    static {
        try {
            // Load environment variables
            String masterName = System.getenv("redis_master_name");
            String sentinelNodes = System.getenv("redis_sentinels");
            int database = Integer.parseInt(System.getenv().getOrDefault("redis_db", "0"));
            int timeout = Integer.parseInt(System.getenv().getOrDefault("redis_timeout", "2000"));

            // Parse sentinel nodes into a Set
            Set<String> sentinels = new HashSet<>();
            for (String node : sentinelNodes.split(",")) {
                sentinels.add(node.trim());
            }

            // Pool configuration
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(10); // Maximum number of total Redis connections (active + idle)
            poolConfig.setMaxIdle(5); // Maximum number of idle connections (kept ready for reuse)
            poolConfig.setMinIdle(1); // Minimum number of idle connections (always kept open)

            // Initialize Sentinel Pool (no username/password)
            jedisSentinelPool = new JedisSentinelPool(
                    masterName, sentinels, poolConfig, timeout, null, database
            );

            System.out.println("Redis Sentinel initialized successfully");
            System.out.println("Master: " + masterName);
            System.out.println("Sentinels: " + sentinels);

        } catch (Exception e) {
            System.err.println("Redis Sentinel initialization failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Redis Sentinel initialization failed", e);
        }
    }

    // Get connection
    public static Jedis getResource() {
        if (jedisSentinelPool == null) {
            throw new IllegalStateException("Redis Sentinel pool not initialized.");
        }
        return jedisSentinelPool.getResource();
    }

    // Ping
    public static String ping() {
        try (Jedis jedis = getResource()) {
            return jedis.ping();
        }
    }

    // Get all fields from hash
    public static Map<String, String> hgetAll(String key) {
        try (Jedis jedis = getResource()) {
            return jedis.hgetAll(key);
        }
    }

    // Close pool
    public static void closePool() {
        if (jedisSentinelPool != null && !jedisSentinelPool.isClosed()) {
            jedisSentinelPool.close();
            System.out.println("Redis Sentinel pool closed.");
        }
    }

//    public static String getValue(String key) {
//        try (Jedis jedis = getResource()) {
//            return jedis.get(key);
//        }
//    }

//    public static void setValue(String key, String value) {
//        try (Jedis jedis = getResource()) {
//            jedis.set(key, value);
//        }
//    }
}