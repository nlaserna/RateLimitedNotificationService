import java.util.*;

class NotificationServiceImpl implements NotificationService {
    private Gateway gateway;
    private Map<String, Map<String, Long>> userNotificationTimestamps;
    private Map<String, Map<String, Integer>> userNotificationCounts;
    private long currentTime;

    public NotificationServiceImpl(Gateway gateway) {
        this.gateway = gateway;
        this.userNotificationTimestamps = new HashMap<>();
        this.userNotificationCounts = new HashMap<>();
        this.currentTime = System.currentTimeMillis();
    }

    @Override
    public void send(String type, String userId, String message) {

        // Ensure the user has an entry in the timestamp and count maps
        userNotificationTimestamps.putIfAbsent(userId, new HashMap<>());
        userNotificationCounts.putIfAbsent(userId, new HashMap<>());

        // Get the user's timestamp and count maps
        Map<String, Long> userTimestamps = userNotificationTimestamps.get(userId);
        Map<String, Integer> userCounts = userNotificationCounts.get(userId);

        // Calculate the rate limit based on the notification type
        int rateLimit = getRateLimit(type);

        // Calculate the rate limit duration (in milliseconds)
        long rateLimitDuration = getRateLimitDuration(type);

        // Check if the user has exceeded the rate limit for this type
        if (!hasExceededRateLimit(userTimestamps, userCounts, type, rateLimit, rateLimitDuration, currentTime)) {
            gateway.send(userId, message);
            userTimestamps.put(type, currentTime);
            userCounts.put(type, userCounts.getOrDefault(type, 0) + 1);
        } else {
            System.out.println("Rate limit exceeded for user " + userId + " and type " + type);
        }
    }

    private int getRateLimit(String type) {
        switch (type) {
            case "status":
                return 2; // 2 per minute
            case "news":
                return 1; // 1 per day
            case "marketing":
                return 3; // 3 per hour
            default:
                return 0; // No rate limit by default
        }
    }

    private long getRateLimitDuration(String type) {
        // Define the rate limit duration (in milliseconds) based on the notification type
        switch (type) {
            case "status":
                return 60000L; // 1 minute
            case "news":
                return 86400000L; // 1 day
            case "marketing":
                return 3600000L; // 1 hour
            default:
                return 0L; // No rate limit for unknown types
        }
    }

    private boolean hasExceededRateLimit(
            Map<String, Long> userTimestamps,
            Map<String, Integer> userCounts,
            String type,
            int rateLimit,
            long rateLimitDuration,
            long currentTime
    ) {
        if (!userTimestamps.containsKey(type)) {
            return false; // No previous notifications of this type
        }

        long lastTimestamp = userTimestamps.get(type);
        int count = userCounts.getOrDefault(type, 0);

        if (currentTime - lastTimestamp >= rateLimitDuration) {
            userCounts.put(type, 0);
            userTimestamps.put(type, currentTime);
            return false;
        }

        return count >= rateLimit;
    }

    //For testing purposes only
    void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    //For testing purposes only
    long getCurrentTime() {
        return this.currentTime;
    }
}

interface NotificationService {
    void send(String type, String userId, String message);
}

interface Gateway {
    void send(String userId, String message);
}

class GatewayImpl implements Gateway {
    //Creating GatewayImpl to represent the mailing service to be connected and to override send method in tests.
    @Override
    public void send(String userId, String message) {
        System.out.println("sending message to user " + userId + ": " + message);
    }
}


