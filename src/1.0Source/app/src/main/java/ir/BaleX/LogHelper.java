package ir.BaleX;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogHelper {

    private static final String PREFS = "balex_logs";
    private static final String KEY   = "logs";
    private static final int MAX_LOGS = 100;

    public static void log(Context context, String message) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            JSONArray logs = new JSONArray(prefs.getString(KEY, "[]"));

            JSONObject entry = new JSONObject();
            entry.put("msg", message);
            entry.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.US).format(new Date()));
            logs.put(entry);

            // نگه داشتن آخرین MAX_LOGS لاگ
            if (logs.length() > MAX_LOGS) {
                JSONArray trimmed = new JSONArray();
                for (int i = logs.length() - MAX_LOGS; i < logs.length(); i++) {
                    trimmed.put(logs.get(i));
                }
                logs = trimmed;
            }

            prefs.edit().putString(KEY, logs.toString()).apply();
        } catch (Exception ignored) {}
    }

    public static JSONArray getLogs(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            return new JSONArray(prefs.getString(KEY, "[]"));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public static int getCount(Context context) {
        return getLogs(context).length();
    }

    public static void clear(Context context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().remove(KEY).apply();
    }
}
