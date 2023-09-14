import java.util.concurrent.*;

class Solution {
    public static void main(String[] args) throws InterruptedException {

        NotificationServiceImpl service = new NotificationServiceImpl(new Gateway());
        while(true) {
            service.send("news", "user", "news 1");
            service.send("news", "user", "news 2");
            service.send("news", "user", "news 3");
            service.send("news", "another user", "news 1");
            service.send("update", "user", "update 1");
            service.send("news", "another user", "news 2");
            service.send("status", "user", "status 1");
            service.send("status", "user", "status 2");
            service.send("status", "another user", "status 1");
            service.send("marketing", "another user", "marketing 1");
            service.send("marketing", "another user", "marketing 2");
            System.out.println("Waiting 60 secs");
            TimeUnit.SECONDS.sleep(60);
        }
    }

}
