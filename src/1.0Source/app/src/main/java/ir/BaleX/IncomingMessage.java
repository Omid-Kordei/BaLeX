package ir.BaleX;

public class IncomingMessage {
    public long chatId;
    public String chatName = "";
    public String chatType = "private";
    public String text = "";
    public long messageId;
    public String mediaType = "";
    public String fileId = "";
    public String fileName = "";
    public String caption = "";
    public double latitude = 0;
    public double longitude = 0;

    public long senderId = 0;
    public String senderName = "";

    public long replyId = 0;
    public String replyText = "";
    public long replySenderId = 0;
    public String replySenderName = "";
}