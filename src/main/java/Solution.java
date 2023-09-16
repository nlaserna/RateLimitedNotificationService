class Solution {

    public static void main(String[] args) {

        NotificationServiceImpl service = new NotificationServiceImpl(new GatewayImpl());

        service.send("news", "user", "news 1");
        service.send("news", "user", "news 2");
        service.send("news", "user", "news 3");
        service.send("news", "another user", "news 1");
        service.send("update", "user", "update 1");
        service.send("update", "user", "update 2");
        service.send("update", "user", "update 3");
        service.send("marketing", "user", "marketing 1");
        service.send("marketing", "user", "marketing 2");
        service.send("marketing", "user", "marketing 3");
        service.send("marketing", "user", "marketing 4");
        service.send("marketing", "user", "marketing 5");

    }

}
