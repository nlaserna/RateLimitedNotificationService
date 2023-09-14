import java.io.*;
import java.util.*;

interface NotificationService {
    void send(String type, String userId, String message);
}

class NotificationServiceImpl implements NotificationService {
    long statusLimit = 60_000; // 1 min
    long newsLimit = 86_400_000; // 1 day
    long marketingLimit = 3_600_000; // 1 hour
    private Gateway gateway;
    private Map<String, Queue<Notification>> rateLimits;

    public NotificationServiceImpl(Gateway gateway) {
        this.gateway = gateway;
        this.rateLimits = new HashMap<>();
    }

    @Override
    public void send(String type, String userId, String message) {
        int maxAllowed = getMaxAllowed(type); // Get the maximum allowed notifications for the given type
        Queue<Notification> userQueue = rateLimits.computeIfAbsent(type, k -> new LinkedList<>());

        // Removes notifications older than the rate limit window
        long currentTime = System.currentTimeMillis();
        while (!userQueue.isEmpty() && currentTime - userQueue.peek().timestamp >= getRateLimitInterval(type)) {
            userQueue.poll();
        }

        if (userQueue.size() < maxAllowed) {
            gateway.send(userId, message);
            userQueue.offer(new Notification(currentTime));
        } else {
            System.out.println("Rate limit exceeded for user " + userId + " and type " + type);
        }

    }

    private long getRateLimitInterval(String type) {
        switch (type) {
            case "status":
               return statusLimit;
            case "news":
                return newsLimit;
            case "marketing":
                return marketingLimit;
            default:
                return Long.MAX_VALUE; // Default rate limit interval (no limit)
        }
    }

    private int getMaxAllowed(String type) {
        switch (type) {
            case "status":
                return 2; // not more than 2 per minute
            case "news":
                return 1; // not more than 1 per day
            case "marketing":
                return 3; // not more than 3 per hour
            default:
                return 0; // Default rate limit
        }
    }


}



