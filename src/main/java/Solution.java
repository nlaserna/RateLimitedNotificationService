public class Solution {
    public static void main(String[] args) {
        NotificationServiceImpl service = new NotificationServiceImpl(new Gateway());

        service.send("news", "user", "news 1");
        service.send("news", "user", "news 2");
        service.send("news", "user", "news 3");
        service.send("news", "another user", "news 1");
        service.send("update", "user", "update 1");
    }
}
