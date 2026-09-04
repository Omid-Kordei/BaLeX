package ir.BaleX;

public class PendingUpload {
    private static long chatId = 0;
    private static String chatName = "";
    private static String type = "";

    public static void set(long id, String name, String t) {
        chatId = id;
        chatName = name;
        type = t;
    }

    public static long getChatId() { return chatId; }
    public static String getChatName() { return chatName; }
    public static String getType() { return type; }

    public static void clear() {
        chatId = 0;
        chatName = "";
        type = "";
    }
}