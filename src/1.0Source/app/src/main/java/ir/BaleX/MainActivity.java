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
import android.view.View;
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

public class MainActivity extends AppCompatActivity {
	
	private MainBinding binding;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		binding = MainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		
		binding.btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				final Activity activity = MainActivity.this;
				final android.content.SharedPreferences prefs =
				getSharedPreferences("balex", android.content.Context.MODE_PRIVATE);
				final String token = ((EditText) findViewById(R.id.edtToken)).getText().toString().trim();
				
				if (token.isEmpty()) {
					android.widget.Toast.makeText(activity, "توکن را وارد کنید", android.widget.Toast.LENGTH_SHORT).show();
					return;
				}
				
				final android.app.Dialog loader = new android.app.Dialog(activity);
				loader.setContentView(R.layout.dialog_loading);
				loader.getWindow().setBackgroundDrawable(
				new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
				loader.setCancelable(false);
				loader.show();
				
				BaleApi.getMe(activity, token, new BaleApi.GetMeCallback() {
					
					@Override
					public void onSuccess(final long id, final String firstName, final String username) {
						loader.dismiss();
						
						android.view.View btnLogin = activity.findViewById(R.id.btnLogin);
						android.view.animation.Animation anim = android.view.animation.AnimationUtils
						.loadAnimation(activity, R.anim.bounce);
						btnLogin.startAnimation(anim);
						
						final android.app.Dialog dialog = new android.app.Dialog(activity);
						dialog.setContentView(R.layout.dialog_bot_confirm);
						dialog.getWindow().setBackgroundDrawable(
						new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
						dialog.setCancelable(false);
						
						((TextView) dialog.findViewById(R.id.dialogBotName)).setText(firstName);
						((TextView) dialog.findViewById(R.id.dialogBotUsername)).setText("@" + username);
						((TextView) dialog.findViewById(R.id.dialogBotId)).setText(String.valueOf(id));
						
						dialog.findViewById(R.id.dialogBtnBack).setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});
						
						dialog.findViewById(R.id.dialogBtnConfirm).setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								prefs.edit()
								.putString("token", token)
								.putLong("bot_id", id)
								.putString("bot_name", firstName)
								.putString("bot_username", username)
								.apply();
								
								dialog.dismiss();
								
								android.content.Intent intent = new android.content.Intent(activity, HomeActivity.class);
								intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK |
								android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
								activity.startActivity(intent);
								activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
							}
						});
						
						dialog.show();
					}
					
					@Override
					public void onError(String message) {
						loader.dismiss();
						
						android.view.View edtToken = activity.findViewById(R.id.edtToken);
						android.view.animation.Animation shake = android.view.animation.AnimationUtils
						.loadAnimation(activity, R.anim.shake);
						edtToken.startAnimation(shake);
						
						android.widget.Toast.makeText(activity,
						"توکن اشتباه است",
						android.widget.Toast.LENGTH_LONG).show();
					}
				});
				
			}
		});
	}
	
	private void initializeLogic() {
		// ══════════ نوار سیستمی ══════════
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		getWindow().setStatusBarColor(android.graphics.Color.WHITE);
		getWindow().getDecorView().setSystemUiVisibility(
		View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
		View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
		View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
		);
		
		// ══════════ چک لاگین قبلی ══════════
		final Activity activity = MainActivity.this;
		final android.content.SharedPreferences prefs =
		getSharedPreferences("balex", android.content.Context.MODE_PRIVATE);
		
		if (!prefs.getString("token", "").isEmpty()) {
			android.content.Intent intent = new android.content.Intent(activity, HomeActivity.class);
			intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			return;
		}
		
	}
	
}