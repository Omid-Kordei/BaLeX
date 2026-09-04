package ir.BaleX;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import ir.BaleX.databinding.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;

public class HomeActivity extends AppCompatActivity {
	
	private HomeBinding binding;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		binding = HomeBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
	}
	
	private void initializeLogic() {
		// ══════════ نوار سیستمی ══════════
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		getWindow().setStatusBarColor(android.graphics.Color.parseColor("#00C853"));
		getWindow().getDecorView().setSystemUiVisibility(
		View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
		View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
		);
		
		final Activity activity = HomeActivity.this;
		final android.content.SharedPreferences prefs =
		getSharedPreferences("balex", android.content.Context.MODE_PRIVATE);
		final String token   = prefs.getString("token", "");
		final String botName = prefs.getString("bot_name", "ربات");
		final String botUser = prefs.getString("bot_username", "");
		final long   botId   = prefs.getLong("bot_id", 0);
		
		final LinearLayout footer = (LinearLayout) findViewById(R.id.linear3);
		
		final FrameLayout frameContent = (FrameLayout) findViewById(R.id.frameContent);
		final android.view.View pageChats    = getLayoutInflater().inflate(R.layout.layout_chat_list, frameContent, false);
		final android.view.View pageSettings = getLayoutInflater().inflate(R.layout.layout_settings, frameContent, false);
		frameContent.addView(pageChats);
		frameContent.addView(pageSettings);
		
		String avatarLetter = botName.length() > 0
		? String.valueOf(botName.charAt(0)).toUpperCase() : "B";
		((TextView) pageSettings.findViewById(R.id.txtAvatarLetter)).setText(avatarLetter);
		((TextView) pageSettings.findViewById(R.id.txtProfileName)).setText(botName);
		((TextView) pageSettings.findViewById(R.id.txtProfileUsername)).setText("@" + botUser);
		((TextView) pageSettings.findViewById(R.id.txtProfileId)).setText("ID: " + botId);
		
		pageSettings.findViewById(R.id.rowToken).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				android.content.ClipboardManager cm = (android.content.ClipboardManager)
				getSystemService(android.content.Context.CLIPBOARD_SERVICE);
				cm.setPrimaryClip(android.content.ClipData.newPlainText("token", token));
				android.widget.Toast.makeText(activity, "توکن کپی شد", android.widget.Toast.LENGTH_SHORT).show();
			}
		});
		
		int logCount = LogHelper.getCount(activity);
		((TextView) pageSettings.findViewById(R.id.txtLogCount)).setText(logCount + " خطا");
		
		pageSettings.findViewById(R.id.btnLogs).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				org.json.JSONArray logs = LogHelper.getLogs(activity);
				StringBuilder sb = new StringBuilder();
				if (logs.length() == 0) {
					sb.append("هیچ خطایی ثبت نشده");
				} else {
					for (int i = logs.length() - 1; i >= 0; i--) {
						try {
							org.json.JSONObject entry = logs.getJSONObject(i);
							sb.append(entry.optString("time")).append("\n")
							.append(entry.optString("msg")).append("\n\n");
						} catch (Exception ignored) {}
					}
				}
				new android.app.AlertDialog.Builder(activity)
				.setTitle("لاگ خطاها (" + logs.length() + ")")
				.setMessage(sb.toString())
				.setPositiveButton("بستن", null)
				.setNegativeButton("پاک کردن", new android.content.DialogInterface.OnClickListener() {
					@Override public void onClick(android.content.DialogInterface d, int w) {
						LogHelper.clear(activity);
						((TextView) pageSettings.findViewById(R.id.txtLogCount)).setText("۰ خطا");
					}
				})
				.show();
			}
		});
		
		pageSettings.findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				new android.app.AlertDialog.Builder(activity)
				.setTitle("خروج از حساب")
				.setMessage("آیا مطمئن هستید؟ توکن حذف می‌شود.")
				.setPositiveButton("خروج", new android.content.DialogInterface.OnClickListener() {
					@Override public void onClick(android.content.DialogInterface d, int w) {
						PollingManager.stop();
						prefs.edit().clear().apply();
						android.content.Intent intent =
						new android.content.Intent(activity, MainActivity.class);
						intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK |
						android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(intent);
						overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
					}
				})
				.setNegativeButton("انصراف", null)
				.show();
			}
		});
		
		final android.widget.ListView listChats =
		(android.widget.ListView) pageChats.findViewById(R.id.listChats);
		final android.view.View layoutEmpty = pageChats.findViewById(R.id.layoutEmpty);
		
		final org.json.JSONArray chatList = StorageHelper.loadChatList(activity);
		final ChatAdapter chatAdapter = new ChatAdapter(activity, chatList, token);
		listChats.setAdapter(chatAdapter);
		
		if (chatList.length() > 0) {
			listChats.setVisibility(View.VISIBLE);
			layoutEmpty.setVisibility(View.GONE);
		} else {
			listChats.setVisibility(View.GONE);
			layoutEmpty.setVisibility(View.VISIBLE);
		}
		
		final LinearLayout tabAll     = (LinearLayout) pageChats.findViewById(R.id.tabAll);
		final LinearLayout tabPrivate = (LinearLayout) pageChats.findViewById(R.id.tabPrivate);
		final LinearLayout tabGroup   = (LinearLayout) pageChats.findViewById(R.id.tabGroup);
		final TextView txtTabAll      = (TextView) pageChats.findViewById(R.id.txtTabAll);
		final TextView txtTabPrivate  = (TextView) pageChats.findViewById(R.id.txtTabPrivate);
		final TextView txtTabGroup    = (TextView) pageChats.findViewById(R.id.txtTabGroup);
		
		final Runnable refreshChatVisibility = new Runnable() {
			@Override public void run() {
				int count = chatAdapter.getCount();
				if (count > 0) {
					listChats.setVisibility(View.VISIBLE);
					layoutEmpty.setVisibility(View.GONE);
				} else {
					listChats.setVisibility(View.GONE);
					layoutEmpty.setVisibility(View.VISIBLE);
				}
			}
		};
		
		View.OnClickListener tabClickListener = new View.OnClickListener() {
			@Override public void onClick(View v) {
				String filter = "all";
				if (v.getId() == R.id.tabPrivate) filter = "private";
				else if (v.getId() == R.id.tabGroup) filter = "group";
				
				chatAdapter.updateData(StorageHelper.loadChatList(activity, filter));
				refreshChatVisibility.run();
				
				int activeColor   = android.graphics.Color.parseColor("#00C853");
				int inactiveColor = android.graphics.Color.parseColor("#888888");
				tabAll.setBackgroundResource(filter.equals("all") ? R.drawable.bg_footer_active : android.R.color.transparent);
				tabPrivate.setBackgroundResource(filter.equals("private") ? R.drawable.bg_footer_active : android.R.color.transparent);
				tabGroup.setBackgroundResource(filter.equals("group") ? R.drawable.bg_footer_active : android.R.color.transparent);
				txtTabAll.setTextColor(filter.equals("all") ? activeColor : inactiveColor);
				txtTabPrivate.setTextColor(filter.equals("private") ? activeColor : inactiveColor);
				txtTabGroup.setTextColor(filter.equals("group") ? activeColor : inactiveColor);
			}
		};
		tabAll.setOnClickListener(tabClickListener);
		tabPrivate.setOnClickListener(tabClickListener);
		tabGroup.setOnClickListener(tabClickListener);
		
		final LinearLayout tabChats    = (LinearLayout) findViewById(R.id.tabChats);
		final LinearLayout tabSettings = (LinearLayout) findViewById(R.id.tabSettings);
		final ImageView iconChats      = (ImageView) tabChats.findViewById(R.id.iconTabChats);
		final ImageView iconSettings   = (ImageView) tabSettings.findViewById(R.id.iconTabSettings);
		final TextView labelChats      = (TextView) tabChats.findViewById(R.id.labelTabChats);
		final TextView labelSettings   = (TextView) tabSettings.findViewById(R.id.labelTabSettings);
		
		final Runnable showChats = new Runnable() {
			@Override public void run() {
				pageChats.setVisibility(View.VISIBLE);
				pageSettings.setVisibility(View.GONE);
				tabChats.setBackgroundResource(R.drawable.bg_footer_active);
				tabSettings.setBackgroundResource(android.R.color.transparent);
				labelChats.setTextColor(android.graphics.Color.parseColor("#00C853"));
				labelSettings.setTextColor(android.graphics.Color.parseColor("#AAAAAA"));
				iconChats.setColorFilter(android.graphics.Color.parseColor("#00C853"));
				iconSettings.setColorFilter(android.graphics.Color.parseColor("#AAAAAA"));
			}
		};
		
		final Runnable showSettings = new Runnable() {
			@Override public void run() {
				pageSettings.setVisibility(View.VISIBLE);
				pageChats.setVisibility(View.GONE);
				tabSettings.setBackgroundResource(R.drawable.bg_footer_active);
				tabChats.setBackgroundResource(android.R.color.transparent);
				labelSettings.setTextColor(android.graphics.Color.parseColor("#00C853"));
				labelChats.setTextColor(android.graphics.Color.parseColor("#AAAAAA"));
				iconSettings.setColorFilter(android.graphics.Color.parseColor("#00C853"));
				iconChats.setColorFilter(android.graphics.Color.parseColor("#AAAAAA"));
			}
		};
		
		tabChats.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { showChats.run(); }
		});
		tabSettings.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { showSettings.run(); }
		});
		
		PollingManager.start(activity, token, new PollingManager.UpdateCallback() {
			@Override
			public void onNewMessage(final long chatId, final String chatName,
			final String text, final long messageId) {
				activity.runOnUiThread(new Runnable() {
					@Override public void run() {
						org.json.JSONArray updated = StorageHelper.loadChatList(activity);
						chatAdapter.updateData(updated);
						refreshChatVisibility.run();
					}
				});
			}
		});
		
		showChats.run();
		
		final android.view.View pageChatRoom = getLayoutInflater()
		.inflate(R.layout.layout_chat_room, frameContent, false);
		frameContent.addView(pageChatRoom);
		pageChatRoom.setVisibility(View.GONE);
		
		final android.view.View pageProfile = getLayoutInflater()
		.inflate(R.layout.layout_profile, frameContent, false);
		frameContent.addView(pageProfile);
		pageProfile.setVisibility(View.GONE);
		
		final android.widget.ListView listMessages =
		(android.widget.ListView) pageChatRoom.findViewById(R.id.listMessages);
		final android.widget.EditText edtMessage =
		(android.widget.EditText) pageChatRoom.findViewById(R.id.edtMessage);
		final LinearLayout layoutReplyPreview =
		(LinearLayout) pageChatRoom.findViewById(R.id.layoutReplyPreview);
		final TextView txtReplyPreview =
		(TextView) pageChatRoom.findViewById(R.id.txtReplyPreview);
		
		final MessageAdapter[] msgAdapter = { null };
		final long[] currentChatId   = { 0 };
		final String[] currentChatName = { "" };
		final ChatOpener[] openChat = new ChatOpener[1];
		
		openChat[0] = new ChatOpener() {
			@Override public void open(long chatId, String chatName) {
				try {
					currentChatId[0]   = chatId;
					currentChatName[0] = chatName;
					
					((TextView) pageChatRoom.findViewById(R.id.txtChatRoomName)).setText(chatName);
					String letter = chatName.length() > 0
					? String.valueOf(chatName.charAt(0)).toUpperCase() : "?";
					((TextView) pageChatRoom.findViewById(R.id.txtChatRoomAvatar)).setText(letter);
					
					org.json.JSONArray messages = StorageHelper.loadMessages(activity, chatId);
					msgAdapter[0] = new MessageAdapter(activity, messages, token);
					msgAdapter[0].setReplyRequestListener(new MessageAdapter.ReplyRequestListener() {
						@Override public void onReplyRequested(String previewText) {
							txtReplyPreview.setText(previewText);
							layoutReplyPreview.setVisibility(View.VISIBLE);
						}
					});
					msgAdapter[0].setProfileClickListener(new MessageAdapter.ProfileClickListener() {
						@Override public void onProfileClick(long userId, String userName) {
							openChat[0].open(userId, userName);
						}
					});
					listMessages.setAdapter(msgAdapter[0]);
					
					StorageHelper.markChatAsRead(activity, chatId);
					ReplyState.clear();
					layoutReplyPreview.setVisibility(View.GONE);
					
					pageChatRoom.setVisibility(View.VISIBLE);
					pageChats.setVisibility(View.GONE);
					pageSettings.setVisibility(View.GONE);
					pageProfile.setVisibility(View.GONE);
					footer.setVisibility(View.GONE);
					pageChats.findViewById(R.id.layoutEmpty).setVisibility(View.GONE);
					
				} catch (Exception e) {
					LogHelper.log(activity, "openChat: " + e.getMessage());
					android.widget.Toast.makeText(activity, "خطا در باز کردن چت", android.widget.Toast.LENGTH_SHORT).show();
				}
			}
		};
		
		listChats.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
				try {
					org.json.JSONObject chat = (org.json.JSONObject) chatAdapter.getItem(position);
					openChat[0].open(chat.getLong("chat_id"), chat.optString("chat_name", "کاربر"));
				} catch (Exception e) {
					LogHelper.log(activity, "chat click: " + e.getMessage());
				}
			}
		});
		
		pageChatRoom.findViewById(R.id.btnChatBack).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				pageChatRoom.setVisibility(View.GONE);
				footer.setVisibility(View.VISIBLE);
				ReplyState.clear();
				layoutReplyPreview.setVisibility(View.GONE);
				showChats.run();
				org.json.JSONArray updated = StorageHelper.loadChatList(activity);
				chatAdapter.updateData(updated);
				refreshChatVisibility.run();
			}
		});
		
		pageChatRoom.findViewById(R.id.btnCancelReply).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				ReplyState.clear();
				layoutReplyPreview.setVisibility(View.GONE);
			}
		});
		
		pageProfile.findViewById(R.id.btnProfileBack).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				pageProfile.setVisibility(View.GONE);
				pageChatRoom.setVisibility(View.VISIBLE);
			}
		});
		
		pageChatRoom.findViewById(R.id.btnChatMore).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				if (currentChatId[0] == 0) return;
				final long chatId = currentChatId[0];
				
				pageChats.setVisibility(View.GONE);
				pageChatRoom.setVisibility(View.GONE);
				pageProfile.setVisibility(View.VISIBLE);
				
				String letter = currentChatName[0].length() > 0
				? String.valueOf(currentChatName[0].charAt(0)).toUpperCase() : "?";
				((TextView) pageProfile.findViewById(R.id.txtProfileBigAvatar)).setText(letter);
				((TextView) pageProfile.findViewById(R.id.txtProfileBigName)).setText(currentChatName[0]);
				pageProfile.findViewById(R.id.imgProfileBigPhoto).setVisibility(View.GONE);
				pageProfile.findViewById(R.id.rowProfileUsername).setVisibility(View.GONE);
				pageProfile.findViewById(R.id.rowProfileBio).setVisibility(View.GONE);
				pageProfile.findViewById(R.id.rowProfileMembers).setVisibility(View.GONE);
				((TextView) pageProfile.findViewById(R.id.txtProfileId)).setText(String.valueOf(chatId));
				
				BaleApi.getChat(activity, token, chatId, new BaleApi.ChatInfoCallback() {
					@Override public void onSuccess(org.json.JSONObject chat) {
						String type = chat.optString("type", "private");
						boolean isGroup = StorageHelper.isGroupType(type);
						String typeLabel = type.equals("private") ? "کاربر"
						: type.equals("channel") ? "کانال" : "گروه";
						((TextView) pageProfile.findViewById(R.id.txtProfileBigType)).setText(typeLabel);
						
						String username = chat.optString("username", "");
						if (!username.isEmpty()) {
							pageProfile.findViewById(R.id.rowProfileUsername).setVisibility(View.VISIBLE);
							((TextView) pageProfile.findViewById(R.id.txtProfileUsername)).setText("@" + username);
						}
						
						String bio = chat.optString("bio", chat.optString("description", ""));
						if (!bio.isEmpty()) {
							pageProfile.findViewById(R.id.rowProfileBio).setVisibility(View.VISIBLE);
							((TextView) pageProfile.findViewById(R.id.txtProfileBio)).setText(bio);
						}
						
						if (isGroup) {
							pageProfile.findViewById(R.id.rowProfileMembers).setVisibility(View.VISIBLE);
							BaleApi.getChatMembersCount(activity, token, chatId, new BaleApi.CountCallback() {
								@Override public void onSuccess(int count) {
									((TextView) pageProfile.findViewById(R.id.txtProfileMembers))
									.setText(count + " عضو");
								}
								@Override public void onError(String error) {
									((TextView) pageProfile.findViewById(R.id.txtProfileMembers))
									.setText("نامشخص");
								}
							});
						}
						
						org.json.JSONObject photo = chat.optJSONObject("photo");
						if (photo != null) {
							String bigFileId = photo.optString("big_file_id", "");
							if (!bigFileId.isEmpty()) {
								FileDownloader.downloadBitmap(activity, token, bigFileId,
								new FileDownloader.BitmapCallback() {
									@Override public void onSuccess(android.graphics.Bitmap bitmap) {
										ImageView img = (ImageView) pageProfile.findViewById(R.id.imgProfileBigPhoto);
										img.setImageBitmap(bitmap);
										img.setVisibility(View.VISIBLE);
									}
									@Override public void onError(String error) { }
								});
							}
						}
					}
					@Override public void onError(String error) {
						android.widget.Toast.makeText(activity, "خطا در دریافت اطلاعات: " + error, android.widget.Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		
		final androidx.activity.result.ActivityResultLauncher<String> mediaPickerLauncher =
		registerForActivityResult(
		new androidx.activity.result.contract.ActivityResultContracts.GetContent(),
		new androidx.activity.result.ActivityResultCallback<Uri>() {
			@Override
			public void onActivityResult(final Uri uri) {
				if (uri == null) return;
				final long chatId = PendingUpload.getChatId();
				final String chatName = PendingUpload.getChatName();
				final String mediaType = PendingUpload.getType();
				PendingUpload.clear();
				if (chatId == 0) return;
				
				final android.app.ProgressDialog pd = new android.app.ProgressDialog(activity);
				pd.setMessage("در حال ارسال...");
				pd.setCancelable(false);
				pd.show();
				
				new Thread(new Runnable() {
					@Override public void run() {
						final String filePath = FileDownloader.prepareUploadFile(activity, uri);
						activity.runOnUiThread(new Runnable() {
							@Override public void run() {
								pd.dismiss();
								if (filePath == null) {
									android.widget.Toast.makeText(activity, "خطا در آماده‌سازی فایل", android.widget.Toast.LENGTH_SHORT).show();
									return;
								}
								BaleApi.sendMediaFile(activity, token, chatId, filePath, mediaType, "",
								new BaleApi.SendCallback() {
									@Override public void onSuccess(long messageId) {
										String label = mediaType.equals("photo") ? "📷 عکس"
										: mediaType.equals("audio") ? "🎵 آهنگ" : "📄 فایل";
										StorageHelper.saveSentMessage(activity, chatId, chatName, label, messageId);
										PollingManager.notifyLocalUpdate(chatId, chatName, label, messageId);
										if (chatId == currentChatId[0] && msgAdapter[0] != null) {
											org.json.JSONArray updated = StorageHelper.loadMessages(activity, chatId);
											msgAdapter[0].updateData(updated);
											listMessages.setSelection(updated.length() - 1);
										}
									}
									@Override public void onError(String error) {
										LogHelper.log(activity, "sendMediaFile: " + error);
										android.widget.Toast.makeText(activity, "ارسال ناموفق: " + error, android.widget.Toast.LENGTH_SHORT).show();
									}
								});
							}
						});
					}
				}).start();
			}
		}
		);
		
		final Runnable sendCurrentLocation = new Runnable() {
			@Override public void run() {
				if (android.os.Build.VERSION.SDK_INT >= 23 &&
				activity.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
				!= android.content.pm.PackageManager.PERMISSION_GRANTED) {
					activity.requestPermissions(
					new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 501);
					android.widget.Toast.makeText(activity,
					"دسترسی موقعیت مکانی رو بدید و دوباره روی دکمه بزنید",
					android.widget.Toast.LENGTH_LONG).show();
					return;
				}
				try {
					android.location.LocationManager lm =
					(android.location.LocationManager) getSystemService(LOCATION_SERVICE);
					android.location.Location loc = null;
					if (lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
						loc = lm.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
					}
					if (loc == null && lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
						loc = lm.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER);
					}
					if (loc == null) {
						android.widget.Toast.makeText(activity,
						"موقعیت مکانی در دسترس نیست، GPS رو روشن کنید", android.widget.Toast.LENGTH_SHORT).show();
						return;
					}
					final double lat = loc.getLatitude();
					final double lng = loc.getLongitude();
					BaleApi.sendLocation(activity, token, currentChatId[0], lat, lng,
					new BaleApi.SendCallback() {
						@Override public void onSuccess(long messageId) {
							StorageHelper.saveSentMessage(activity, currentChatId[0], currentChatName[0],
							"📍 موقعیت مکانی من", messageId);
							org.json.JSONArray updated = StorageHelper.loadMessages(activity, currentChatId[0]);
							if (msgAdapter[0] != null) {
								msgAdapter[0].updateData(updated);
								listMessages.setSelection(updated.length() - 1);
							}
						}
						@Override public void onError(String error) {
							android.widget.Toast.makeText(activity, "ارسال لوکیشن ناموفق", android.widget.Toast.LENGTH_SHORT).show();
						}
					});
				} catch (Exception e) {
					LogHelper.log(activity, "sendLocation: " + e.getMessage());
				}
			}
		};
		
		pageChatRoom.findViewById(R.id.btnAttach).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				if (currentChatId[0] == 0) return;
				final String[] attachOptions = {"لوکیشن فعلی من", "ارسال فایل", "ارسال آهنگ", "ارسال عکس"};
				new android.app.AlertDialog.Builder(activity)
				.setItems(attachOptions, new android.content.DialogInterface.OnClickListener() {
					@Override public void onClick(android.content.DialogInterface dialog, int which) {
						if (which == 0) {
							sendCurrentLocation.run();
						} else if (which == 1) {
							PendingUpload.set(currentChatId[0], currentChatName[0], "document");
							mediaPickerLauncher.launch("*/*");
						} else if (which == 2) {
							PendingUpload.set(currentChatId[0], currentChatName[0], "audio");
							mediaPickerLauncher.launch("audio/*");
						} else if (which == 3) {
							PendingUpload.set(currentChatId[0], currentChatName[0], "photo");
							mediaPickerLauncher.launch("image/*");
						}
					}
				}).show();
			}
		});
		
		pageChatRoom.findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				final String text = edtMessage.getText().toString().trim();
				if (text.isEmpty() || currentChatId[0] == 0) return;
				edtMessage.setText("");
				
				final long replyId = ReplyState.hasPending() ? ReplyState.getId() : 0;
				final String replyPreviewText = ReplyState.hasPending() ? ReplyState.getText() : "";
				layoutReplyPreview.setVisibility(View.GONE);
				ReplyState.clear();
				
				BaleApi.sendMessage(activity, token, currentChatId[0], text, replyId, null,
				new BaleApi.SendCallback() {
					@Override public void onSuccess(long messageId) {
						StorageHelper.saveSentMessage(activity,
						currentChatId[0], currentChatName[0], text, messageId,
						replyId, replyPreviewText);
						org.json.JSONArray updated =
						StorageHelper.loadMessages(activity, currentChatId[0]);
						if (msgAdapter[0] != null) {
							msgAdapter[0].updateData(updated);
							listMessages.setSelection(updated.length() - 1);
						}
					}
					@Override public void onError(String error) {
						LogHelper.log(activity, "sendMessage: " + error);
						android.widget.Toast.makeText(activity,
						"ارسال ناموفق", android.widget.Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		
		PollingManager.setExtraCallback(new PollingManager.UpdateCallback() {
			@Override
			public void onNewMessage(final long chatId, final String chatName,
			final String text, final long messageId) {
				activity.runOnUiThread(new Runnable() {
					@Override public void run() {
						org.json.JSONArray updated = StorageHelper.loadChatList(activity);
						chatAdapter.updateData(updated);
						refreshChatVisibility.run();
						
						if (chatId == currentChatId[0] && msgAdapter[0] != null
						&& pageChatRoom.getVisibility() == View.VISIBLE) {
							org.json.JSONArray msgs = StorageHelper.loadMessages(activity, chatId);
							msgAdapter[0].updateData(msgs);
							listMessages.setSelection(msgs.length() - 1);
							StorageHelper.markChatAsRead(activity, chatId);
						}
					}
				});
			}
		});
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		PollingManager.stop();
		
	}
}