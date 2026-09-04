package ir.BaleX;

public class ReplyState {
    private static long replyToId = -1;
    private static String replyToText = "";
    private static String replyToName = "";

    public static void set(long messageId, String text, String senderName) {
        replyToId = messageId;
        replyToText = text;
        replyToName = senderName;
    }

    public static void clear() {
        replyToId = -1;
        replyToText = "";
        replyToName = "";
    }

    public static boolean hasPending() { return replyToId > 0; }
    public static long getId() { return replyToId; }
    public static String getText() { return replyToText; }
    public static String getName() { return replyToName; }
}