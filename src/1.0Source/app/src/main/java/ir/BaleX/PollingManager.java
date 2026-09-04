package ir.BaleX;

import android.app.Activity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PollingManager {

    private static final String BASE = "https://tapi.bale.ai/bot";
    private static volatile boolean running = false;
    private static long lastOffset = 0;
    private static UpdateCallback primaryCallback;
    private static UpdateCallback extraCallback;

    public interface UpdateCallback {
        void onNewMessage(long chatId, String chatName, String text, long messageId);
    }

    // ══ برای وقتی خودمون (نه پولینگ) یه پیام محلی ذخیره کردیم و می‌خوایم UI آپدیت بشه ══
    public static void notifyLocalUpdate(long chatId, String chatName, String text, long messageId) {
        if (primaryCallback != null) primaryCallback.onNewMessage(chatId, chatName, text, messageId);
        if (extraCallback != null) extraCallback.onNewMessage(chatId, chatName, text, messageId);
    }

    public static void start(final Activity activity, final String token,
                             final UpdateCallback callback) {
        if (running) return;
        running = true;
        primaryCallback = callback;

        new Thread(new Runnable() {
            @Override public void run() {
                while (running) {
                    try {
                        String url = BASE + token + "/getUpdates?timeout=30&offset=" + lastOffset;
                        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(35000);
                        conn.setReadTimeout(35000);

                        int code = conn.getResponseCode();
                        if (code != 200) {
                            conn.disconnect();
                            Thread.sleep(3000);
                            continue;
                        }

                        BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) sb.append(line);
                        br.close();
                        conn.disconnect();

                        JSONObject json = new JSONObject(sb.toString());
                        if (!json.optBoolean("ok", false)) continue;

                        JSONArray results = json.getJSONArray("result");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject update = results.getJSONObject(i);
                            lastOffset = update.getLong("update_id") + 1;

                            if (update.has("callback_query")) {
                                JSONObject cq = update.getJSONObject("callback_query");
                                String cqId = cq.optString("id", "");
                                JSONObject cqMsg = cq.optJSONObject("message");
                                long cqChatId = cqMsg != null && cqMsg.has("chat")
                                    ? cqMsg.getJSONObject("chat").optLong("id", 0) : 0;
                                String cqData = cq.optString("data", "");
                                BaleApi.answerCallbackQuery(activity, token, cqId, "", null);
                                if (cqChatId != 0) {
                                    IncomingMessage cqm = new IncomingMessage();
                                    cqm.chatId = cqChatId;
                                    cqm.chatName = StorageHelper.getChatType(activity, cqChatId).equals("private") ? "کاربر" : "گروه";
                                    cqm.chatType = StorageHelper.getChatType(activity, cqChatId);
                                    cqm.text = "🔘 دکمه زده شد: " + cqData;
                                    cqm.messageId = System.currentTimeMillis();
                                    StorageHelper.saveMessage(activity, cqm);
                                }
                                continue;
                            }

                            if (!update.has("message")) continue;

                            JSONObject message = update.getJSONObject("message");
                            JSONObject chat    = message.getJSONObject("chat");

                            IncomingMessage m = new IncomingMessage();
                            m.chatId    = chat.getLong("id");
                            m.messageId = message.getLong("message_id");
                            m.chatType  = chat.optString("type", "private");
                            boolean isGroupChat = m.chatType.equals("group")
                                                || m.chatType.equals("supergroup")
                                                || m.chatType.equals("channel");

                            if (isGroupChat) {
                                m.chatName = chat.optString("title", "گروه");
                            } else {
                                String firstName = chat.optString("first_name", "");
                                String lastName  = chat.optString("last_name", "");
                                String full = (firstName + " " + lastName).trim();
                                m.chatName = full.isEmpty() ? "کاربر" : full;
                            }

                            if (message.has("from")) {
                                JSONObject from = message.getJSONObject("from");
                                m.senderId = from.optLong("id", 0);
                                String sfn = from.optString("first_name", "");
                                String sln = from.optString("last_name", "");
                                m.senderName = (sfn + " " + sln).trim();
                                if (m.senderName.isEmpty()) m.senderName = from.optString("username", "کاربر");
                            }

                            if (message.has("reply_to_message")) {
                                JSONObject replyMsg = message.getJSONObject("reply_to_message");
                                m.replyId = replyMsg.optLong("message_id", 0);
                                m.replyText = replyMsg.optString("text", "");
                                if (m.replyText.isEmpty()) m.replyText = "[رسانه]";
                                if (replyMsg.has("from")) {
                                    JSONObject rfrom = replyMsg.getJSONObject("from");
                                    m.replySenderId = rfrom.optLong("id", 0);
                                    String rfn = rfrom.optString("first_name", "");
                                    String rln = rfrom.optString("last_name", "");
                                    m.replySenderName = (rfn + " " + rln).trim();
                                    if (m.replySenderName.isEmpty()) m.replySenderName = rfrom.optString("username", "کاربر");
                                }
                            }

                            String text = message.optString("text", "");

                            if (message.has("photo")) {
                                JSONArray photos = message.getJSONArray("photo");
                                JSONObject photo = photos.getJSONObject(photos.length() - 1);
                                m.fileId    = photo.optString("file_id", "");
                                m.mediaType = "photo";
                                m.fileName  = "تصویر.jpg";
                                m.caption   = message.optString("caption", "");
                                text = "📷 تصویر";
                            } else if (message.has("document")) {
                                JSONObject doc = message.getJSONObject("document");
                                m.fileId    = doc.optString("file_id", "");
                                m.mediaType = "document";
                                m.fileName  = doc.optString("file_name", "فایل");
                                m.caption   = message.optString("caption", "");
                                text = "📄 " + m.fileName;
                            } else if (message.has("audio")) {
                                JSONObject audio = message.getJSONObject("audio");
                                m.fileId    = audio.optString("file_id", "");
                                m.mediaType = "audio";
                                m.fileName  = audio.optString("title",
                                              audio.optString("file_name", "موسیقی.mp3"));
                                m.caption   = message.optString("caption", "");
                                text = "🎵 " + m.fileName;
                            } else if (message.has("video")) {
                                JSONObject video = message.getJSONObject("video");
                                m.fileId    = video.optString("file_id", "");
                                m.mediaType = "video";
                                m.fileName  = video.optString("file_name", "ویدیو.mp4");
                                m.caption   = message.optString("caption", "");
                                text = "🎬 " + m.fileName;
                            } else if (message.has("voice")) {
                                JSONObject voice = message.getJSONObject("voice");
                                m.fileId    = voice.optString("file_id", "");
                                m.mediaType = "voice";
                                m.fileName  = "پیام_صوتی.ogg";
                                m.caption   = message.optString("caption", "");
                                text = "🎤 پیام صوتی";
                            } else if (message.has("animation")) {
                                JSONObject anim = message.getJSONObject("animation");
                                m.fileId    = anim.optString("file_id", "");
                                m.mediaType = "animation";
                                m.fileName  = anim.optString("file_name", "animation.gif");
                                m.caption   = message.optString("caption", "");
                                text = "🎞 GIF";
                            } else if (message.has("sticker")) {
                                JSONObject sticker = message.getJSONObject("sticker");
                                m.fileId    = sticker.optString("file_id", "");
                                m.mediaType = "sticker";
                                m.fileName  = "استیکر";
                                text = "😊 استیکر";
                            } else if (message.has("location")) {
                                JSONObject loc = message.getJSONObject("location");
                                m.latitude  = loc.optDouble("latitude", 0);
                                m.longitude = loc.optDouble("longitude", 0);
                                m.mediaType = "location";
                                m.fileName  = "موقعیت مکانی";
                                text = "📍 موقعیت مکانی";
                            }

                            if (text.isEmpty() && m.mediaType.isEmpty()) continue;
                            m.text = text;

                            StorageHelper.saveMessage(activity, m);

                            final long fChatId = m.chatId;
                            final String fChatName = m.chatName;
                            final String fText = m.text;
                            final long fMsgId = m.messageId;
                            if (primaryCallback != null)
                                primaryCallback.onNewMessage(fChatId, fChatName, fText, fMsgId);
                            if (extraCallback != null)
                                extraCallback.onNewMessage(fChatId, fChatName, fText, fMsgId);
                        }

                    } catch (Exception e) {
                        LogHelper.log(activity, "Polling: " + e.getMessage());
                        try { Thread.sleep(3000); } catch (Exception ignored) {}
                    }
                }
            }
        }).start();
    }

    public static void setExtraCallback(UpdateCallback cb) { extraCallback = cb; }
    public static void stop() {
        running = false;
        primaryCallback = null;
        extraCallback   = null;
    }
}