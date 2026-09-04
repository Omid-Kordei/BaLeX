package ir.BaleX;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import androidx.core.content.FileProvider;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloader {

    private static final String BASE = "https://tapi.bale.ai/bot";
    private static final String FILE_BASE = "https://tapi.bale.ai/file/bot";

    public interface BitmapCallback {
        void onSuccess(Bitmap bitmap);
        void onError(String message);
    }

    public static void downloadAndOpen(final Activity activity, final String token,
                                        final String fileId, final String fileName,
                                        final String mediaType) {

        final ProgressDialog pd = new ProgressDialog(activity);
        pd.setMessage("در حال دانلود...");
        pd.setCancelable(false);
        pd.show();

        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    HttpURLConnection conn1 = (HttpURLConnection)
                        new URL(BASE + token + "/getFile?file_id=" + fileId).openConnection();
                    conn1.setRequestMethod("GET");
                    conn1.setConnectTimeout(10000);
                    conn1.setReadTimeout(10000);

                    BufferedReader br1 = new BufferedReader(
                        new InputStreamReader(conn1.getInputStream()));
                    StringBuilder sb1 = new StringBuilder();
                    String line;
                    while ((line = br1.readLine()) != null) sb1.append(line);
                    br1.close();
                    conn1.disconnect();

                    JSONObject json = new JSONObject(sb1.toString());
                    if (!json.optBoolean("ok", false)) {
                        showError(activity, pd, "خطا در دریافت اطلاعات فایل");
                        return;
                    }
                    final String filePath = json.getJSONObject("result").optString("file_path", "");
                    if (filePath.isEmpty()) {
                        showError(activity, pd, "مسیر فایل دریافت نشد");
                        return;
                    }

                    HttpURLConnection conn2 = (HttpURLConnection)
                        new URL(FILE_BASE + token + "/" + filePath).openConnection();
                    conn2.setRequestMethod("GET");
                    conn2.setConnectTimeout(15000);
                    conn2.setReadTimeout(15000);

                    InputStream is = conn2.getInputStream();

                    File dir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);
                    if (!dir.exists()) dir.mkdirs();

                    String saveName = fileName.isEmpty() ? "balex_file_" + fileId : fileName;
                    if (!saveName.contains(".")) {
                        if (mediaType.equals("photo")) saveName += ".jpg";
                        else if (mediaType.equals("audio")) saveName += ".mp3";
                        else if (mediaType.equals("voice")) saveName += ".ogg";
                        else if (mediaType.equals("video")) saveName += ".mp4";
                        else if (mediaType.equals("animation")) saveName += ".gif";
                    }

                    final File outFile = new File(dir, saveName);
                    FileOutputStream fos = new FileOutputStream(outFile);
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = is.read(buffer)) != -1) fos.write(buffer, 0, len);
                    fos.close();
                    is.close();
                    conn2.disconnect();

                    activity.runOnUiThread(new Runnable() {
                        @Override public void run() {
                            pd.dismiss();
                            try {
                                Uri uri = Uri.fromFile(outFile);
                                String mime = getMimeType(mediaType);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(uri, mime);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                activity.startActivity(Intent.createChooser(intent, "باز کردن با..."));
                            } catch (Exception e) {
                                android.widget.Toast.makeText(activity,
                                    "فایل دانلود شد: " + outFile.getAbsolutePath(),
                                    android.widget.Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } catch (final Exception e) {
                    showError(activity, pd, "خطا: " + e.getMessage());
                    LogHelper.log(activity, "FileDownloader: " + e.getMessage());
                }
            }
        }).start();
    }

    public static void downloadBitmap(final Activity activity, final String token,
            final String fileId, final BitmapCallback cb) {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    HttpURLConnection conn1 = (HttpURLConnection)
                        new URL(BASE + token + "/getFile?file_id=" + fileId).openConnection();
                    conn1.setRequestMethod("GET");
                    conn1.setConnectTimeout(10000);
                    conn1.setReadTimeout(10000);
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                    StringBuilder sb1 = new StringBuilder();
                    String line;
                    while ((line = br1.readLine()) != null) sb1.append(line);
                    br1.close();
                    conn1.disconnect();

                    JSONObject json = new JSONObject(sb1.toString());
                    if (!json.optBoolean("ok", false)) {
                        activity.runOnUiThread(new Runnable() {
                            @Override public void run() { cb.onError("خطا در دریافت تصویر"); }
                        });
                        return;
                    }
                    String filePath = json.getJSONObject("result").optString("file_path", "");

                    HttpURLConnection conn2 = (HttpURLConnection)
                        new URL(FILE_BASE + token + "/" + filePath).openConnection();
                    conn2.setConnectTimeout(15000);
                    conn2.setReadTimeout(15000);
                    InputStream is = conn2.getInputStream();
                    final Bitmap bmp = BitmapFactory.decodeStream(is);
                    is.close();
                    conn2.disconnect();

                    activity.runOnUiThread(new Runnable() {
                        @Override public void run() {
                            if (bmp != null) cb.onSuccess(bmp);
                            else cb.onError("تصویر نامعتبر");
                        }
                    });
                } catch (final Exception e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override public void run() { cb.onError(e.getMessage()); }
                    });
                }
            }
        }).start();
    }

    // ══ تبدیل عکس مربعی به دایره‌ای واقعی (نه فقط ماسک ظاهری) ══
    public static Bitmap toCircularBitmap(Bitmap srcBitmap) {
        int size = Math.min(srcBitmap.getWidth(), srcBitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect rect = new Rect(0, 0, size, size);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        int left = (srcBitmap.getWidth() - size) / 2;
        int top = (srcBitmap.getHeight() - size) / 2;
        Rect srcRect = new Rect(left, top, left + size, top + size);
        canvas.drawBitmap(srcBitmap, srcRect, rect, paint);
        return output;
    }

    public static String prepareUploadFile(Context context, Uri uri) {
        try {
            String fileName = "upload_" + System.currentTimeMillis();
            String mime = context.getContentResolver().getType(uri);
            if (mime != null && mime.contains("/")) {
                fileName += "." + mime.substring(mime.indexOf("/") + 1);
            }
            InputStream is = context.getContentResolver().openInputStream(uri);
            File outFile = new File(context.getCacheDir(), fileName);
            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) != -1) fos.write(buffer, 0, len);
            fos.close();
            is.close();
            return outFile.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    private static String getMimeType(String mediaType) {
        if (mediaType.equals("photo"))     return "image/*";
        if (mediaType.equals("audio"))     return "audio/*";
        if (mediaType.equals("voice"))     return "audio/*";
        if (mediaType.equals("video"))     return "video/*";
        if (mediaType.equals("animation")) return "image/gif";
        if (mediaType.equals("sticker"))   return "image/*";
        return "*/*";
    }

    private static void showError(final Activity activity,
                                   final ProgressDialog pd, final String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override public void run() {
                pd.dismiss();
                android.widget.Toast.makeText(activity, msg, android.widget.Toast.LENGTH_LONG).show();
            }
        });
    }
}