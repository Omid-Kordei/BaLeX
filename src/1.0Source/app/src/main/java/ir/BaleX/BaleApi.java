package ir.BaleX;

import android.app.Activity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BaleApi {

    private static final String BASE        = "https://tapi.bale.ai/bot";
    private static final int    MAX_RETRY   = 3;
    private static final int    RETRY_DELAY = 1500;

    public interface GetMeCallback {
        void onSuccess(long id, String firstName, String username);
        void onError(String message);
    }

    public interface SendCallback {
        void onSuccess(long messageId);
        void onError(String message);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface ChatInfoCallback {
        void onSuccess(JSONObject chat);
        void onError(String message);
    }

    public interface CountCallback {
        void onSuccess(int count);
        void onError(String message);
    }

    public static void getMe(final Activity activity, final String token,
                             final GetMeCallback cb) {
        getMeWithRetry(activity, token, cb, 0);
    }

    private static void getMeWithRetry(final Activity activity, final String token,
            final GetMeCallback cb, final int attempt) {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection)
                        new URL(BASE + token + "/getMe").openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);

                    int code = conn.getResponseCode();
                    if (code != 200 && attempt < MAX_RETRY) {
                        conn.disconnect();
                        Thread.sleep(RETRY_DELAY);
                        getMeWithRetry(activity, token, cb, attempt + 1);
                        return;
                    }

                    JSONObject json = readJson(conn, code);
                    if (json.optBoolean("ok", false)) {
                        final JSONObject r     = json.getJSONObject("result");
                        final long id          = r.optLong("id", 0);
                        final String firstName = r.optString("first_name", "ربات");
                        final String username  = r.optString("username", "");
                        activity.runOnUiThread(new Runnable() {
                            @Override public void run() { cb.onSuccess(id, firstName, username); }
                        });
                    } else {
                        final String desc = json.optString("description", "توکن نامعتبر است");
                        activity.runOnUiThread(new Runnable() {
                            @Override public void run() { cb.onError(desc); }
                        });
                    }
                } catch (final Exception e) {
                    if (attempt < MAX_RETRY) {
                        try { Thread.sleep(RETRY_DELAY); } catch (Exception ignored) {}
                        getMeWithRetry(activity, token, cb, attempt + 1);
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override public void run() { cb.onError("خطای اتصال: " + e.getMessage()); }
                        });
                    }
                }
            }
        }).start();
    }

    public static void sendMessage(final Activity activity, final String token,
            final long chatId, final String text, final SendCallback cb) {
        sendMessage(activity, token, chatId, text, 0, null, cb);
    }

    public static void sendMessage(final Activity activity, final String token,
            final long chatId, final String text, final long replyToMessageId,
            final JSONArray inlineKeyboard, final SendCallback cb) {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection)
                        new URL(BASE + token + "/sendMessage").openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);

                    JSONObject body = new JSONObject();
                    body.put("chat_id", chatId);
                    body.put("text", text);
                    if (replyToMessageId > 0) {
                        body.put("reply_to_message_id", replyToMessageId);
                    }
                    if (inlineKeyboard != null && inlineKeyboard.length() > 0) {
                        JSONObject markup = new JSONObject();
                        markup.put("inline_keyboard", inlineKeyboard);
                        body.put("reply_markup", markup);
                    }

                    OutputStream os = conn.getOutputStream();
                    os.write(body.toString().getBytes("UTF-8"));
                    os.close();

                    int code = conn.getResponseCode();
                    JSONObject json = readJson(conn, code);
                    if (json.optBoolean("ok", false)) {
                        final long msgId = json.getJSONObject("result").optLong("message_id", 0);
                        activity.runOnUiThread(new Runnable() {
                            @Override public void run() { cb.onSuccess(msgId); }
                        });
                    } else {
                        final String err = json.optString("description", "خطا");
                        activity.runOnUiThread(new Runnable() {
                            @Override public void run() { cb.onError(err); }
                        });
                    }
                } catch (final Exception e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override public void run() { cb.onError(e.getMessage()); }
                    });
                }
            }
        }).start();
    }

    public static void sendLocation(final Activity activity, final String token,
            final long chatId, final double latitude, final double longitude,
            final SendCallback cb) {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection)
                        new URL(BASE + token + "/sendLocation").openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);

                    JSONObject body = new JSONObject();
                    body.put("chat_id", chatId);
                    body.put("latitude", latitude);
                    body.put("longitude", longitude);

                    OutputStream os = conn.getOutputStream();
                    os.write(body.toString().getBytes("UTF-8"));
                    os.close();

                    int code = conn.getResponseCode();
                    JSONObject json = readJson(conn, code);
                    if (json.optBoolean("ok", false)) {
                        final long msgId = json.getJSONObject("result").optLong("message_id", 0);
                        activity.runOnUiThread(new Runnable() {
                            @Override public void run() { cb.onSuccess(msgId); }
                        });
                    } else {
                        final String err = json.optString("description", "خطا");
                        activity.runOnUiThread(new Runnable() {
                            @Override public void run() { cb.onError(err); }
                        });
                    }
                } catch (final Exception e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override public void run() { cb.onError(e.getMessage()); }
                    });
                }
            }
        }).start();
    }

    public static void sendMediaFile(final Activity activity, final String token,
            final long chatId, final String filePath, final String mediaType,
            final String caption, final SendCallback cb) {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    String method;
                    String fieldName;
                    if (mediaType.equals("photo"))      { method = "sendPhoto";    fieldName = "photo"; }
                    else if (mediaType.equals("audio"))  { method = "sendAudio";    fieldName = "audio"; }
                    else if (mediaType.equals("video"))  { method = "sendVideo";    fieldName = "video"; }
                    else                                  { method = "sendDocument"; fieldName = "document"; }

                    File file = new File(filePath);
                    if (!file.exists()) {
                        activity.runOnUiThread(new Runnable() {
                            @Override public void run() { cb.onError("فایل پیدا نشد"); }
                        });
                        return;
                    }

                    String boundary = "----BaleXBoundary" + System.currentTimeMillis();
                    HttpURLConnection conn = (HttpURLConnection)
                        new URL(BASE + token + "/" + method).openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(20000);
                    conn.setReadTimeout(30000);
                    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());

                    out.writeBytes("--" + boundary + "\r\n");
                    out.writeBytes("Content-Disposition: form-data; name=\"chat_id\"\r\n\r\n");
                    out.writeBytes(String.valueOf(chatId) + "\r\n");

                    if (caption != null && !caption.isEmpty()) {
                        out.writeBytes("--" + boundary + "\r\n");
                        out.writeBytes("Content-Disposition: form-data; name=\"caption\"\r\n\r\n");
                        out.writeBytes(caption + "\r\n");
                    }

                    out.writeBytes("--" + boundary + "\r\n");
                    out.writeBytes("Content-Disposition: form-data; name=\"" + fieldName +
                        "\"; filename=\"" + file.getName() + "\"\r\n");
                    out.writeBytes("Content-Type: application/octet-stream\r\n\r\n");

                    FileInputStream fis = new FileInputStream(file);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    fis.close();

                    out.writeBytes("\r\n--" + boundary + "--\r\n");
                    out.flush();
                    out.close();

                    int code = conn.getResponseCode();
                    JSONObject json = readJson(conn, code);
                    if (json.optBoolean("ok", false)) {
                        final long msgId = json.getJSONObject("result").optLong("message_id", 0);
                        activity.runOnUiThread(new Runnable() {
                            @Override public void run() { cb.onSuccess(msgId); }
                        });
                    } else {
                        final String err = json.optString("description", "خطا در ارسال فایل");
                        activity.runOnUiThread(new Runnable() {
                            @Override public void run() { cb.onError(err); }
                        });
                    }
                } catch (final Exception e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override public void run() { cb.onError(e.getMessage()); }
                    });
                }
            }
        }).start();
    }

    public static void answerCallbackQuery(final Activity activity, final String token,
            final String callbackQueryId, final String text, final SimpleCallback cb) {
        JSONObject body = new JSONObject();
        try {
            body.put("callback_query_id", callbackQueryId);
            if (text != null && !text.isEmpty()) body.put("text", text);
        } catch (Exception e) {}
        postAction(activity, token, "answerCallbackQuery", body, cb);
    }

    public static void deleteMessage(final Activity activity, final String token,
            final long chatId, final long messageId, final SimpleCallback cb) {
        JSONObject body = new JSONObject();
        try { body.put("chat_id", chatId); body.put("message_id", messageId); } catch (Exception e) {}
        postAction(activity, token, "deleteMessage", body, cb);
    }

    public static void pinChatMessage(final Activity activity, final String token,
            final long chatId, final long messageId, final SimpleCallback cb) {
        JSONObject body = new JSONObject();
        try { body.put("chat_id", chatId); body.put("message_id", messageId); } catch (Exception e) {}
        postAction(activity, token, "pinChatMessage", body, cb);
    }

    public static void unpinChatMessage(final Activity activity, final String token,
            final long chatId, final long messageId, final SimpleCallback cb) {
        JSONObject body = new JSONObject();
        try { body.put("chat_id", chatId); body.put("message_id", messageId); } catch (Exception e) {}
        postAction(activity, token, "unpinChatMessage", body, cb);
    }

    public static void editMessageText(final Activity activity, final String token,
            final long chatId, final long messageId, final String newText,
            final SimpleCallback cb) {
        JSONObject body = new JSONObject();
        try {
            body.put("chat_id", chatId);
            body.put("message_id", messageId);
            body.put("text", newText);
        } catch (Exception e) {}
        postAction(activity, token, "editMessageText", body, cb);
    }

    // ══ اطلاعات کامل یک چت/کاربر ══
    public static void getChat(final Activity activity, final String token,
            final long chatId, final ChatInfoCallback cb) {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection)
                        new URL(BASE + token + "/getChat?chat_id=" + chatId).openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int code = conn.getResponseCode();
                    final JSONObject json = readJson(conn, code);
                    if (json.optBoolean("ok", false)) {
                        final JSONObject result = json.getJSONObject("result");
                        activity.runOnUiThread(new Runnable() {
                            @Override public void run() { cb.onSuccess(result); }
                        });
                    } else {
                        final String err = json.optString("description", "خطا در دریافت اطلاعات");
                        activity.runOnUiThread(new Runnable() {
                            @Override public void run() { cb.onError(err); }
                        });
                    }
                } catch (final Exception e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override public void run() { cb.onError(e.getMessage()); }
                    });
                }
            }
        }).start();
    }

    // ══ تعداد اعضای گروه/کانال ══
    public static void getChatMembersCount(final Activity activity, final String token,
            final long chatId, final CountCallback cb) {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection)
                        new URL(BASE + token + "/getChatMembersCount?chat_id=" + chatId).openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int code = conn.getResponseCode();
                    final JSONObject json = readJson(conn, code);
                    if (json.optBoolean("ok", false)) {
                        final int count = json.optInt("result", 0);
                        activity.runOnUiThread(new Runnable() {
                            @Override public void run() { cb.onSuccess(count); }
                        });
                    } else {
                        final String err = json.optString("description", "خطا");
                        activity.runOnUiThread(new Runnable() {
                            @Override public void run() { cb.onError(err); }
                        });
                    }
                } catch (final Exception e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override public void run() { cb.onError(e.getMessage()); }
                    });
                }
            }
        }).start();
    }

    private static void postAction(final Activity activity, final String token,
            final String method, final JSONObject body, final SimpleCallback cb) {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection)
                        new URL(BASE + token + "/" + method).openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);

                    OutputStream os = conn.getOutputStream();
                    os.write(body.toString().getBytes("UTF-8"));
                    os.close();

                    int code = conn.getResponseCode();
                    JSONObject json = readJson(conn, code);
                    if (json.optBoolean("ok", false)) {
                        activity.runOnUiThread(new Runnable() {
                            @Override public void run() { if (cb != null) cb.onSuccess(); }
                        });
                    } else {
                        final String err = json.optString("description", "خطا");
                        activity.runOnUiThread(new Runnable() {
                            @Override public void run() { if (cb != null) cb.onError(err); }
                        });
                    }
                } catch (final Exception e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override public void run() { if (cb != null) cb.onError(e.getMessage()); }
                    });
                }
            }
        }).start();
    }

    private static JSONObject readJson(HttpURLConnection conn, int code) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(
            code == 200 ? conn.getInputStream() : conn.getErrorStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        conn.disconnect();
        return new JSONObject(sb.toString());
    }
}