package ir.BaleX;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends BaseAdapter {

    private static final int TYPE_RECEIVED = 0;
    private static final int TYPE_SENT     = 1;
    private static final int TYPE_DATE     = 2;
    private static final int TYPE_MEDIA    = 3;

    private Activity activity;
    private JSONArray messages;
    private String token;

    public interface ReplyRequestListener {
        void onReplyRequested(String previewText);
    }
    private ReplyRequestListener replyListener;
    public void setReplyRequestListener(ReplyRequestListener l) { this.replyListener = l; }

    public interface ProfileClickListener {
        void onProfileClick(long userId, String userName);
    }
    private ProfileClickListener profileListener;
    public void setProfileClickListener(ProfileClickListener l) { this.profileListener = l; }

    public MessageAdapter(Activity activity, JSONArray messages, String token) {
        this.activity = activity;
        this.messages = messages;
        this.token    = token;
    }

    public void updateData(JSONArray newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    @Override public int getCount() { return messages.length(); }
    @Override public Object getItem(int i) { return messages.optJSONObject(i); }
    @Override public long getItemId(int i) { return i; }
    @Override public int getViewTypeCount() { return 4; }

    @Override
    public int getItemViewType(int position) {
        try {
            JSONObject msg = messages.getJSONObject(position);
            if (msg.optBoolean("is_date_divider", false)) return TYPE_DATE;
            if (msg.has("media_type") && !msg.optString("media_type").isEmpty())
                return TYPE_MEDIA;
            return msg.optBoolean("from_bot", false) ? TYPE_SENT : TYPE_RECEIVED;
        } catch (Exception e) { return TYPE_RECEIVED; }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);

        if (convertView == null) {
            int layout;
            switch (type) {
                case TYPE_SENT:  layout = R.layout.item_msg_sent;  break;
                case TYPE_DATE:  layout = R.layout.item_date_divider; break;
                case TYPE_MEDIA: layout = R.layout.item_msg_media; break;
                default:         layout = R.layout.item_msg_received; break;
            }
            convertView = LayoutInflater.from(activity).inflate(layout, parent, false);
        }

        try {
            final JSONObject msg = messages.getJSONObject(position);
            String timeStr = "";
            long time = msg.optLong("time", 0);
            if (time > 0)
                timeStr = new SimpleDateFormat("HH:mm", Locale.US).format(new Date(time));

            final long replyId          = msg.optLong("reply_id", 0);
            final String replyText      = msg.optString("reply_text", "");
            final long replySenderId    = msg.optLong("reply_sender_id", 0);
            final String replySenderName = msg.optString("reply_sender_name", "پیام");

            if (type == TYPE_DATE) {
                ((TextView) convertView.findViewById(R.id.txtDate))
                    .setText(msg.optString("text", ""));

            } else if (type == TYPE_MEDIA) {
                final String mediaType  = msg.optString("media_type", "document");
                final String fileId     = msg.optString("file_id", "");
                final String fileName   = msg.optString("file_name", "فایل");
                final String caption    = msg.optString("caption", "");
                final double latitude   = msg.optDouble("latitude", 0);
                final double longitude  = msg.optDouble("longitude", 0);
                final boolean isLocation = mediaType.equals("location");

                TextView txtName    = (TextView) convertView.findViewById(R.id.txtMediaName);
                TextView txtCaption = (TextView) convertView.findViewById(R.id.txtMediaCaption);
                TextView txtHint    = (TextView) convertView.findViewById(R.id.txtMediaSize);
                TextView txtTime    = (TextView) convertView.findViewById(R.id.txtMediaTime);
                ImageView icon      = (ImageView) convertView.findViewById(R.id.imgMediaIcon);

                txtName.setText(fileName);
                txtHint.setText(isLocation ? "ضربه بزن برای نمایش روی نقشه" : "ضربه بزن برای دانلود");
                txtTime.setText(timeStr);

                if (!caption.isEmpty()) {
                    txtCaption.setText(caption);
                    txtCaption.setVisibility(View.VISIBLE);
                } else {
                    txtCaption.setVisibility(View.GONE);
                }

                if (mediaType.equals("photo"))
                    icon.setImageResource(R.drawable.ic_photo);
                else if (mediaType.equals("audio") || mediaType.equals("voice"))
                    icon.setImageResource(R.drawable.ic_attach);
                else if (mediaType.equals("video") || mediaType.equals("animation"))
                    icon.setImageResource(R.drawable.ic_photo);
                else if (isLocation)
                    icon.setImageResource(R.drawable.ic_info);
                else
                    icon.setImageResource(R.drawable.ic_document);

                convertView.findViewById(R.id.mediaCard).setOnClickListener(
                    new View.OnClickListener() {
                        @Override public void onClick(View v) {
                            try {
                                if (isLocation) {
                                    openInMaps(latitude, longitude);
                                } else {
                                    FileDownloader.downloadAndOpen(
                                        activity, token, fileId, fileName, mediaType);
                                }
                            } catch (Exception e) {
                                LogHelper.log(activity, "media click: " + e.getMessage());
                            }
                        }
                    });

                attachLongPress(convertView, msg, false);

            } else if (type == TYPE_SENT) {
                ((TextView) convertView.findViewById(R.id.txtMsgSent))
                    .setText(msg.optString("text", ""));
                ((TextView) convertView.findViewById(R.id.txtTimeSent))
                    .setText(timeStr + " ✓");

                setupQuote(convertView, R.id.quoteBoxSent, R.id.txtQuoteNameSent, R.id.txtQuoteTextSent,
                    replyId, replyText, replySenderId, replySenderName);

                attachLongPress(convertView, msg, true);

            } else {
                TextView txtSenderName = (TextView) convertView.findViewById(R.id.txtSenderName);
                String chatTypeOfMsg = msg.optString("chat_type", "private");
                final long senderId = msg.optLong("sender_id", 0);
                final String senderName = msg.optString("sender_name", "");

                if (StorageHelper.isGroupType(chatTypeOfMsg) && !senderName.isEmpty()) {
                    txtSenderName.setText(senderName);
                    txtSenderName.setVisibility(View.VISIBLE);
                    txtSenderName.setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View v) {
                            try {
                                if (senderId != 0 && profileListener != null) {
                                    profileListener.onProfileClick(senderId, senderName);
                                }
                            } catch (Exception e) {
                                LogHelper.log(activity, "senderName click: " + e.getMessage());
                            }
                        }
                    });
                } else {
                    txtSenderName.setVisibility(View.GONE);
                }

                ((TextView) convertView.findViewById(R.id.txtMsgReceived))
                    .setText(msg.optString("text", ""));
                ((TextView) convertView.findViewById(R.id.txtTimeReceived))
                    .setText(timeStr);

                setupQuote(convertView, R.id.quoteBoxReceived, R.id.txtQuoteNameReceived, R.id.txtQuoteTextReceived,
                    replyId, replyText, replySenderId, replySenderName);

                attachLongPress(convertView, msg, false);
            }

        } catch (Exception e) {
            LogHelper.log(activity, "MessageAdapter: " + e.getMessage());
        }
        return convertView;
    }

    private void setupQuote(View convertView, int boxId, int nameId, int textId,
                            final long replyId, String replyText,
                            final long replySenderId, String replySenderName) {
        LinearLayout box = (LinearLayout) convertView.findViewById(boxId);
        if (replyId == 0) {
            box.setVisibility(View.GONE);
            return;
        }
        ((TextView) convertView.findViewById(nameId)).setText(replySenderName);
        ((TextView) convertView.findViewById(textId)).setText(replyText);
        box.setVisibility(View.VISIBLE);

        final String senderNameFinal = replySenderName;
        box.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                try {
                    if (replySenderId != 0 && profileListener != null) {
                        profileListener.onProfileClick(replySenderId, senderNameFinal);
                    }
                } catch (Exception e) {
                    LogHelper.log(activity, "quote click: " + e.getMessage());
                }
            }
        });
    }

    private void openInMaps(double lat, double lng) {
        try {
            Uri uri = Uri.parse("geo:" + lat + "," + lng + "?q=" + lat + "," + lng);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivity(intent);
            } else {
                Uri webUri = Uri.parse(
                    "https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng);
                activity.startActivity(new Intent(Intent.ACTION_VIEW, webUri));
            }
        } catch (Exception e) {
            Toast.makeText(activity, "خطا در باز کردن نقشه", Toast.LENGTH_SHORT).show();
        }
    }

    private void attachLongPress(View rootView, final JSONObject msg, final boolean isSent) {
        rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View v) {
                try {
                    final long chatId    = msg.optLong("chat_id", 0);
                    final long messageId = msg.optLong("id", 0);
                    final String currentText = msg.optString("text", "");
                    final boolean isMedia = msg.has("media_type") && !msg.optString("media_type").isEmpty();

                    String chatType = StorageHelper.getChatType(activity, chatId);
                    boolean isGroup = StorageHelper.isGroupType(chatType);
                    long pinnedId = StorageHelper.getPinnedMessage(activity, chatId);
                    final boolean isPinned = (pinnedId == messageId);

                    List<String> optionList = new ArrayList<String>();
                    optionList.add("پاسخ");
                    if (isGroup) {
                        optionList.add(isPinned ? "لغو پین" : "پین کردن");
                    }
                    if (isSent && !isMedia) optionList.add("ویرایش");
                    optionList.add("حذف");
                    final String[] options = optionList.toArray(new String[0]);

                    new AlertDialog.Builder(activity)
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {
                                String selected = options[which];
                                if (selected.equals("پاسخ")) {
                                    String preview = isMedia ? "[رسانه]" : currentText;
                                    ReplyState.set(messageId, preview, msg.optString("chat_name", ""));
                                    if (replyListener != null) {
                                        replyListener.onReplyRequested(preview);
                                    }
                                } else if (selected.equals("پین کردن")) {
                                    pinMessage(chatId, messageId);
                                } else if (selected.equals("لغو پین")) {
                                    unpinMessage(chatId, messageId);
                                } else if (selected.equals("حذف")) {
                                    deleteMessage(chatId, messageId);
                                } else if (selected.equals("ویرایش")) {
                                    showEditDialog(chatId, messageId, currentText);
                                }
                            }
                        }).show();
                } catch (Exception e) {
                    LogHelper.log(activity, "longPress: " + e.getMessage());
                }
                return true;
            }
        });
    }

    private void pinMessage(final long chatId, final long messageId) {
        BaleApi.pinChatMessage(activity, token, chatId, messageId, new BaleApi.SimpleCallback() {
            @Override public void onSuccess() {
                StorageHelper.setPinnedMessage(activity, chatId, messageId);
                notifyDataSetChanged();
                Toast.makeText(activity, "پیام پین شد", Toast.LENGTH_SHORT).show();
            }
            @Override public void onError(String error) {
                Toast.makeText(activity, "خطا: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unpinMessage(final long chatId, final long messageId) {
        BaleApi.unpinChatMessage(activity, token, chatId, messageId, new BaleApi.SimpleCallback() {
            @Override public void onSuccess() {
                StorageHelper.clearPinnedMessage(activity, chatId);
                notifyDataSetChanged();
                Toast.makeText(activity, "پین لغو شد", Toast.LENGTH_SHORT).show();
            }
            @Override public void onError(String error) {
                Toast.makeText(activity, "خطا: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteMessage(final long chatId, final long messageId) {
        BaleApi.deleteMessage(activity, token, chatId, messageId, new BaleApi.SimpleCallback() {
            @Override public void onSuccess() {
                StorageHelper.deleteMessage(activity, chatId, messageId);
                updateData(StorageHelper.loadMessages(activity, chatId));
                Toast.makeText(activity, "پیام حذف شد", Toast.LENGTH_SHORT).show();
            }
            @Override public void onError(String error) {
                Toast.makeText(activity, "خطا: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog(final long chatId, final long messageId, String currentText) {
        final EditText input = new EditText(activity);
        input.setText(currentText);
        input.setSelection(currentText.length());

        new AlertDialog.Builder(activity)
            .setTitle("ویرایش پیام")
            .setView(input)
            .setPositiveButton("ذخیره", new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                    final String newText = input.getText().toString().trim();
                    if (newText.isEmpty()) return;
                    BaleApi.editMessageText(activity, token, chatId, messageId, newText,
                        new BaleApi.SimpleCallback() {
                            @Override public void onSuccess() {
                                StorageHelper.updateMessageText(activity, chatId, messageId, newText);
                                updateData(StorageHelper.loadMessages(activity, chatId));
                                Toast.makeText(activity, "پیام ویرایش شد", Toast.LENGTH_SHORT).show();
                            }
                            @Override public void onError(String error) {
                                Toast.makeText(activity, "خطا: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            })
            .setNegativeButton("انصراف", null)
            .show();
    }
}