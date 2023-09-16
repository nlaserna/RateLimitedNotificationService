import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceImplTest {

    private GatewayMock gateway;
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        gateway = new GatewayMock();
        notificationService = new NotificationServiceImpl(gateway);
    }

    @Test
    void testRateLimitExceeded() {
        System.out.println("Test Rate Limit exceeded:");
        // Sending more notifications than the rate limit allows
        for (int i = 0; i < 5; i++) {
            notificationService.send("marketing", "user", "marketing " + i);
        }

        for (int i = 0; i < 2; i++) {
            notificationService.send("news", "user", "news " + i);
        }

        for (int i = 0; i < 3; i++) {
            notificationService.send("status", "user", "status " + i);
        }

        // Ensure that only the 6 were sent, and rate limit exceeded for the rest
        // should send 3 marketing emails, 1 news email, and 2 status emails based on rate limits
        assertEquals(6, gateway.getSentMessagesCount());
        System.out.println("");
    }

    @Test
    void testRateLimitResetForMarketing() {
        System.out.println("Testing Marketing emails rate limit.");
        // Sending 3 marketing notifications (within rate limit)
        for (int i = 0; i < 3; i++) {
            notificationService.send("marketing", "user", "marketing " + i);
        }

        System.out.println("Updated current time to show as if one hour has already elapsed.");

        // Set the current time to simulate more than an hour passed
        notificationService.setCurrentTime(notificationService.getCurrentTime() + 3600001);

        // Sending 2 more marketing notifications (within rate limit)
        for (int i = 3; i < 5; i++) {
            notificationService.send("marketing", "user", "marketing " + i);
        }

        // Ensure that all 5 were sent (rate limit reset after an hour)
        assertEquals(5, gateway.getSentMessagesCount());
        System.out.println("");
    }

    @Test
    void testRateLimitResetForNews() {
        System.out.println("Testing News emails rate limit.");

        // Sending 1 news notifications (within rate limit)
        for (int i = 0; i < 3; i++) {
            notificationService.send("news", "user", "news " + i);
        }

        System.out.println("Updated current time to show as if one DAY has already elapsed.");

        // Set the current time to simulate more than an hour passed
        notificationService.setCurrentTime(notificationService.getCurrentTime() + 86400001);

        // Sending 1 more news notifications (within rate limit)
        for (int i = 1; i < 3; i++) {
            notificationService.send("news", "user", "news " + i);
        }

        // Ensure that all 2 were sent (rate limit reset after a day)
        assertEquals(2, gateway.getSentMessagesCount());
        System.out.println("");
    }
    @Test
    void testRateLimitResetForStatus() {
        System.out.println("Testing Status emails rate limit.");

        // Sending 2 status notifications (within rate limit)
        for (int i = 0; i < 4; i++) {
            notificationService.send("status", "user", "status " + i);
        }

        System.out.println("Updated current time to show as if one MINUTE has already elapsed.");

        // Set the current time to simulate more than a minute passed
        notificationService.setCurrentTime(notificationService.getCurrentTime() + 86400001);

        // Sending 2 more status notifications (within rate limit)
        for (int i = 2; i < 5; i++) {
            notificationService.send("status", "user", "status " + i);
        }

        // Ensure that all 4 were sent (rate limit reset after a minute)
        assertEquals(4, gateway.getSentMessagesCount());
        System.out.println("");
    }


    @Test
    void testDifferentTypes() {
        // Sending notifications of different types
        System.out.println("Testing notifications of different types");
        notificationService.send("status", "user", "status 1");
        notificationService.send("news", "user", "news 1");
        notificationService.send("marketing", "user", "marketing 1");

        // Ensure that all were sent without rate limit issues
        assertEquals(3, gateway.getSentMessagesCount());
        System.out.println("");
    }

    private static class GatewayMock implements Gateway {
        private int sentMessagesCount = 0;

        @Override
        public void send(String userId, String message) {
            // Simulate sending the message
            System.out.println("Sending message to user " + userId + ": " + message);
            sentMessagesCount++;
        }

        public int getSentMessagesCount() {
            return sentMessagesCount;
        }
    }
}
