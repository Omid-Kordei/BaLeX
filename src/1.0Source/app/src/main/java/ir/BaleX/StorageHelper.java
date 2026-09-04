package ir.BaleX;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StorageHelper {

    private static final String PREFS_UNREAD = "balex_unread";
    private static final String PREFS_PINNED = "balex_pinned";

    // ══ نسخه‌ی جدید و تمیز: ذخیره از روی IncomingMessage ══
    public static void saveMessage(Context context, IncomingMessage m) {
        try {
            File dir = new File(context.getFilesDir(), "chats");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, m.chatId + ".json");
            JSONArray messages = loadMessages(context, m.chatId);

            JSONObject msg = new JSONObject();
            msg.put("id",        m.messageId);
            msg.put("chat_id",   m.chatId);
            msg.put("chat_name", m.chatName);
            msg.put("chat_type", m.chatType == null || m.chatType.isEmpty() ? "private" : m.chatType);
            msg.put("text",      m.text);
            msg.put("time",      System.currentTimeMillis());
            msg.put("from_bot",  false);

            if (m.senderId != 0) {
                msg.put("sender_id", m.senderId);
                msg.put("sender_name", m.senderName);
            }

            if (m.mediaType != null && !m.mediaType.isEmpty()) {
                msg.put("media_type", m.mediaType);
                msg.put("file_id",    m.fileId);
                msg.put("file_name",  m.fileName);
                if (m.caption != null && !m.caption.isEmpty()) msg.put("caption", m.caption);
                if (m.mediaType.equals("location")) {
                    msg.put("latitude",  m.latitude);
                    msg.put("longitude", m.longitude);
                }
            }

            if (m.replyId != 0) {
                msg.put("reply_id", m.replyId);
                msg.put("reply_text", m.replyText);
                if (m.replySenderId != 0) {
                    msg.put("reply_sender_id", m.replySenderId);
                    msg.put("reply_sender_name", m.replySenderName);
                }
            }

            messages.put(msg);

            FileWriter fw = new FileWriter(file);
            fw.write(messages.toString());
            fw.close();

            incrementUnread(context, m.chatId);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ══ ارسال ساده (بدون ریپلای) ══
    public static void saveSentMessage(Context context, long chatId,
                                        String chatName, String text, long messageId) {
        saveSentMessage(context, chatId, chatName, text, messageId, 0, "");
    }

    // ══ ارسال به‌عنوان پاسخ ══
    public static void saveSentMessage(Context context, long chatId,
                                        String chatName, String text, long messageId,
                                        long replyId, String replyText) {
        try {
            File dir = new File(context.getFilesDir(), "chats");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, chatId + ".json");
            JSONArray messages = loadMessages(context, chatId);

            JSONObject msg = new JSONObject();
            msg.put("id",        messageId);
            msg.put("chat_id",   chatId);
            msg.put("chat_name", chatName);
            msg.put("text",      text);
            msg.put("time",      System.currentTimeMillis());
            msg.put("from_bot",  true);
            if (replyId != 0) {
                msg.put("reply_id", replyId);
                msg.put("reply_text", replyText);
            }
            messages.put(msg);

            FileWriter fw = new FileWriter(file);
            fw.write(messages.toString());
            fw.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void deleteMessage(Context context, long chatId, long messageId) {
        try {
            JSONArray messages = loadMessages(context, chatId);
            JSONArray updated = new JSONArray();
            for (int i = 0; i < messages.length(); i++) {
                JSONObject m = messages.getJSONObject(i);
                if (m.optLong("id", -1) != messageId) {
                    updated.put(m);
                }
            }
            File dir = new File(context.getFilesDir(), "chats");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, chatId + ".json");
            FileWriter fw = new FileWriter(file);
            fw.write(updated.toString());
            fw.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void updateMessageText(Context context, long chatId,
                                         long messageId, String newText) {
        try {
            JSONArray messages = loadMessages(context, chatId);
            for (int i = 0; i < messages.length(); i++) {
                JSONObject m = messages.getJSONObject(i);
                if (m.optLong("id", -1) == messageId) {
                    m.put("text", newText);
                    m.put("edited", true);
                }
            }
            File dir = new File(context.getFilesDir(), "chats");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, chatId + ".json");
            FileWriter fw = new FileWriter(file);
            fw.write(messages.toString());
            fw.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static JSONArray loadMessages(Context context, long chatId) {
        try {
            File file = new File(context.getFilesDir() + "/chats", chatId + ".json");
            if (!file.exists()) return new JSONArray();
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            br.close();
            return new JSONArray(sb.toString());
        } catch (Exception e) { return new JSONArray(); }
    }

    public static String getChatType(Context context, long chatId) {
        JSONArray messages = loadMessages(context, chatId);
        for (int i = messages.length() - 1; i >= 0; i--) {
            JSONObject m = messages.optJSONObject(i);
            if (m != null && m.has("chat_type")) {
                return m.optString("chat_type", "private");
            }
        }
        return "private";
    }

    public static boolean isGroupType(String chatType) {
        return chatType.equals("group") || chatType.equals("supergroup") || chatType.equals("channel");
    }

    public static JSONArray loadChatList(Context context) {
        return loadChatList(context, "all");
    }

    public static JSONArray loadChatList(Context context, String filter) {
        List<JSONObject> chatObjs = new ArrayList<JSONObject>();
        try {
            File dir = new File(context.getFilesDir(), "chats");
            if (!dir.exists()) return new JSONArray();
            File[] files = dir.listFiles();
            if (files == null) return new JSONArray();
            for (File f : files) {
                long chatId = Long.parseLong(f.getName().replace(".json", ""));
                JSONArray messages = loadMessages(context, chatId);
                if (messages.length() == 0) continue;
                JSONObject last = messages.getJSONObject(messages.length() - 1);

                String chatType = "private";
                for (int i = messages.length() - 1; i >= 0; i--) {
                    JSONObject m = messages.optJSONObject(i);
                    if (m != null && m.has("chat_type")) {
                        chatType = m.optString("chat_type", "private");
                        break;
                    }
                }
                boolean isGroup = isGroupType(chatType);

                if (filter.equals("private") && isGroup) continue;
                if (filter.equals("group") && !isGroup) continue;

                JSONObject chat = new JSONObject();
                chat.put("chat_id",      last.getLong("chat_id"));
                chat.put("chat_name",    last.optString("chat_name", "کاربر"));
                chat.put("chat_type",    chatType);
                chat.put("last_msg",     last.optString("text", ""));
                chat.put("last_time",    last.optLong("time", 0));
                chat.put("unread_count", getUnreadCount(context, chatId));
                chatObjs.add(chat);
            }
        } catch (Exception e) { e.printStackTrace(); }

        Collections.sort(chatObjs, new Comparator<JSONObject>() {
            @Override public int compare(JSONObject a, JSONObject b) {
                long ta = a.optLong("last_time", 0);
                long tb = b.optLong("last_time", 0);
                return tb < ta ? -1 : (tb > ta ? 1 : 0);
            }
        });

        JSONArray result = new JSONArray();
        for (JSONObject c : chatObjs) result.put(c);
        return result;
    }

    private static SharedPreferences unreadPrefs(Context context) {
        return context.getSharedPreferences(PREFS_UNREAD, Context.MODE_PRIVATE);
    }

    public static void incrementUnread(Context context, long chatId) {
        SharedPreferences prefs = unreadPrefs(context);
        int current = prefs.getInt(String.valueOf(chatId), 0);
        prefs.edit().putInt(String.valueOf(chatId), current + 1).apply();
    }

    public static int getUnreadCount(Context context, long chatId) {
        return unreadPrefs(context).getInt(String.valueOf(chatId), 0);
    }

    public static void markChatAsRead(Context context, long chatId) {
        unreadPrefs(context).edit().putInt(String.valueOf(chatId), 0).apply();
    }

    private static SharedPreferences pinnedPrefs(Context context) {
        return context.getSharedPreferences(PREFS_PINNED, Context.MODE_PRIVATE);
    }

    public static void setPinnedMessage(Context context, long chatId, long messageId) {
        pinnedPrefs(context).edit().putLong(String.valueOf(chatId), messageId).apply();
    }

    public static void clearPinnedMessage(Context context, long chatId) {
        pinnedPrefs(context).edit().remove(String.valueOf(chatId)).apply();
    }

    public static long getPinnedMessage(Context context, long chatId) {
        return pinnedPrefs(context).getLong(String.valueOf(chatId), -1);
    }
}