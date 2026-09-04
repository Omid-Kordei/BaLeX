package ir.BaleX;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ChatAdapter extends BaseAdapter {

    private Activity activity;
    private JSONArray chats;
    private String token;

    // ══ کش عکس در حافظه (سریع‌ترین لایه، برای همین سشن) ══
    private static final Map<Long, Bitmap> memCache = new HashMap<Long, Bitmap>();
    // ══ فقط یه‌بار هر چت رو در طول این سشن چک آپدیت کن ══
    private static final Set<Long> checkedThisSession = new HashSet<Long>();

    public ChatAdapter(Activity activity, JSONArray chats, String token) {
        this.activity = activity;
        this.chats    = chats;
        this.token    = token;
    }

    public void updateData(JSONArray newChats) {
        this.chats = newChats;
        notifyDataSetChanged();
    }

    @Override public int getCount() { return chats.length(); }
    @Override public Object getItem(int i) { return chats.optJSONObject(i); }
    @Override public long getItemId(int i) { return i; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(activity)
                .inflate(R.layout.item_chat, parent, false);
        }
        try {
            JSONObject chat = chats.getJSONObject(position);
            final long chatId = chat.optLong("chat_id", 0);
            String name    = chat.optString("chat_name", "کاربر");
            String lastMsg = chat.optString("last_msg", "");
            long time      = chat.optLong("last_time", 0);
            int unread     = chat.optInt("unread_count", 0);

            TextView txtLetter = (TextView) convertView.findViewById(R.id.txtChatAvatar);
            final ImageView imgAvatar = (ImageView) convertView.findViewById(R.id.imgChatAvatar);

            txtLetter.setText(name.length() > 0
                ? String.valueOf(name.charAt(0)).toUpperCase() : "?");
            ((TextView) convertView.findViewById(R.id.txtChatName)).setText(name);
            ((TextView) convertView.findViewById(R.id.txtChatLastMsg)).setText(lastMsg);
            ((TextView) convertView.findViewById(R.id.txtChatTime))
                .setText(time > 0
                    ? new SimpleDateFormat("HH:mm", Locale.US).format(new Date(time)) : "");

            LinearLayout badge = (LinearLayout) convertView.findViewById(R.id.badgeUnread);
            TextView txtUnread = (TextView) convertView.findViewById(R.id.txtUnreadCount);
            if (unread > 0) {
                txtUnread.setText(unread > 99 ? "99+" : String.valueOf(unread));
                badge.setVisibility(View.VISIBLE);
            } else {
                badge.setVisibility(View.GONE);
            }

            imgAvatar.setTag(chatId);

            // ══ لایه ۱: کش حافظه (فوری) ══
            if (memCache.containsKey(chatId)) {
                Bitmap cached = memCache.get(chatId);
                if (cached != null) {
                    imgAvatar.setImageBitmap(cached);
                    imgAvatar.setVisibility(View.VISIBLE);
                } else {
                    imgAvatar.setVisibility(View.GONE);
                }
            } else {
                // ══ لایه ۲: کش دیسک (نسبتاً فوری، از سشن قبل) ══
                Bitmap disk = ProfileCache.loadAvatarBitmap(activity, chatId);
                if (disk != null) {
                    Bitmap circular = FileDownloader.toCircularBitmap(disk);
                    memCache.put(chatId, circular);
                    imgAvatar.setImageBitmap(circular);
                    imgAvatar.setVisibility(View.VISIBLE);
                } else {
                    imgAvatar.setVisibility(View.GONE);
                }
            }

            // ══ لایه ۳: چک آپدیت از سرور (یه‌بار در این سشن) ══
            if (!checkedThisSession.contains(chatId)) {
                checkedThisSession.add(chatId);
                checkForUpdate(chatId, imgAvatar);
            }

        } catch (Exception e) {
            LogHelper.log(activity, "ChatAdapter: " + e.getMessage());
        }
        return convertView;
    }

    private void checkForUpdate(final long chatId, final ImageView imgAvatar) {
        BaleApi.getChat(activity, token, chatId, new BaleApi.ChatInfoCallback() {
            @Override public void onSuccess(JSONObject chat) {
                String bio = chat.optString("bio", chat.optString("description", ""));
                ProfileCache.setBio(activity, chatId, bio);

                JSONObject photo = chat.optJSONObject("photo");
                
                // ✅ راه‌حل: استفاده از آرایه به جای متغیر مستقیم
                final String[] newFileIdHolder = new String[1];
                newFileIdHolder[0] = photo != null ? photo.optString("big_file_id", "") : "";
                
                String oldFileId = ProfileCache.getPhotoFileId(activity, chatId);

                if (newFileIdHolder[0].isEmpty()) return;
                if (newFileIdHolder[0].equals(oldFileId)) return; // چیزی عوض نشده

                FileDownloader.downloadBitmap(activity, token, newFileIdHolder[0],
                    new FileDownloader.BitmapCallback() {
                        @Override public void onSuccess(Bitmap bitmap) {
                            // ✅ استفاده از آرایه در اینجا
                            ProfileCache.setPhotoFileId(activity, chatId, newFileIdHolder[0]);
                            ProfileCache.saveAvatarBitmap(activity, chatId, bitmap);
                            Bitmap circular = FileDownloader.toCircularBitmap(bitmap);
                            memCache.put(chatId, circular);
                            if (imgAvatar.getTag() != null
                                && ((Long) imgAvatar.getTag()) == chatId) {
                                imgAvatar.setImageBitmap(circular);
                                imgAvatar.setVisibility(View.VISIBLE);
                            }
                        }
                        @Override public void onError(String error) { }
                    });
            }
            @Override public void onError(String error) {
                checkedThisSession.remove(chatId);
            }
        });
    }
}