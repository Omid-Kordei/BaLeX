package ir.BaleX;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ProfileCache {
    private static final String PREFS = "balex_profile_cache";

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static String getPhotoFileId(Context context, long chatId) {
        return prefs(context).getString("photo_" + chatId, "");
    }

    public static void setPhotoFileId(Context context, long chatId, String fileId) {
        prefs(context).edit().putString("photo_" + chatId, fileId).apply();
    }

    public static String getBio(Context context, long chatId) {
        return prefs(context).getString("bio_" + chatId, "");
    }

    public static void setBio(Context context, long chatId, String bio) {
        prefs(context).edit().putString("bio_" + chatId, bio).apply();
    }

    private static File avatarFile(Context context, long chatId) {
        File dir = new File(context.getFilesDir(), "avatars");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, chatId + ".png");
    }

    public static Bitmap loadAvatarBitmap(Context context, long chatId) {
        try {
            File f = avatarFile(context, chatId);
            if (!f.exists()) return null;
            FileInputStream fis = new FileInputStream(f);
            Bitmap bmp = BitmapFactory.decodeStream(fis);
            fis.close();
            return bmp;
        } catch (Exception e) {
            return null;
        }
    }

    public static void saveAvatarBitmap(Context context, long chatId, Bitmap bitmap) {
        try {
            File f = avatarFile(context, chatId);
            FileOutputStream fos = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}