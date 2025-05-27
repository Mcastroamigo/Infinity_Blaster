package ca.grasley.spaceshooter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SettingsActivity extends BaseActivity  {

    private ImageView profileImage;
    private TextView userName;
    private SeekBar soundSeekBar;
    private Switch vibrationSwitch;
    private View vibrationIndicator;
    private SeekBar textSizeSeekBar;
    private Button logoutButton;
    private Button saveButton;

    private static final String PREFS_NAME = "app_settings";

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        float fontScale = prefs.getFloat("font_scale", 1.0f);

        Configuration configuration = newBase.getResources().getConfiguration();
        configuration.fontScale = fontScale;

        Context context = newBase.createConfigurationContext(configuration);
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // Inicializa Firebase Auth y Google Sign-In
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Referencias UI
        profileImage     = findViewById(R.id.profileImage);
        userName         = findViewById(R.id.userName);
        soundSeekBar     = findViewById(R.id.soundSeekBar);
        vibrationSwitch  = findViewById(R.id.vibrationSwitch);
        vibrationIndicator = findViewById(R.id.vibrationIndicator);
        textSizeSeekBar  = findViewById(R.id.textSizeSeekBar);
        logoutButton     = findViewById(R.id.logoutButton);
        saveButton       = findViewById(R.id.saveButton);

        // Cargar perfil de FirebaseUser (nombre + foto)
        loadUserProfile();

        // Carga resto de ajustes guardados
        loadSettings();

        // Listeners
        soundSeekBar.setMax(100);
        soundSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setMusicVolume(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        vibrationSwitch.setOnCheckedChangeListener((btn, isChecked) -> updateVibrationIndicator(isChecked));

        textSizeSeekBar.setMax(100);
        textSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float scale = (progress + 50) / 100.0f; // 0.5–1.5
                saveFontScale(scale);
                applyFontScale(scale);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        saveButton.setOnClickListener(v -> saveSettings());
        logoutButton.setOnClickListener(v -> logout());
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        // Nombre: displayName o extraído del email
        String name = currentUser.getDisplayName();
        if (name == null || name.isEmpty()) {
            String email = currentUser.getEmail();
            if (email != null && email.contains("@")) {
                name = email.substring(0, email.indexOf("@"));
            } else {
                name = "Usuario";
            }
        }
        userName.setText(name);

        // Foto de perfil si existe
        if (currentUser.getPhotoUrl() != null) {
            new Thread(() -> {
                try {
                    URL url = new URL(currentUser.getPhotoUrl().toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    Bitmap bmp = BitmapFactory.decodeStream(is);
                    runOnUiThread(() -> profileImage.setImageBitmap(bmp));
                } catch (IOException e) {
                    Log.e("SettingsActivity", "Error cargando foto", e);
                }
            }).start();
        }
    }

    private void setMusicVolume(int progress) {
        MusicManager.setVolume(progress / 100f);
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        int soundLevel = prefs.getInt("soundLevel", 50);
        soundSeekBar.setProgress(soundLevel);
        setMusicVolume(soundLevel);

        boolean vibrationEnabled = prefs.getBoolean("vibrationEnabled", true);
        vibrationSwitch.setChecked(vibrationEnabled);
        updateVibrationIndicator(vibrationEnabled);

        int textSizeProgress = (int)((prefs.getFloat("font_scale", 1.0f) * 100) - 50);
        textSizeSeekBar.setProgress(textSizeProgress);
    }

    private void updateVibrationIndicator(boolean enabled) {
        int color = enabled ?
                ContextCompat.getColor(this, android.R.color.holo_green_light) :
                ContextCompat.getColor(this, android.R.color.darker_gray);
        vibrationIndicator.setBackgroundColor(color);
    }

    private void saveFontScale(float scale) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit().putFloat("font_scale", scale).apply();
    }

    private void applyFontScale(float scale) {
        Configuration cfg = getResources().getConfiguration();
        cfg.fontScale = scale;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        getResources().updateConfiguration(cfg, metrics);
        recreate();
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt("soundLevel", soundSeekBar.getProgress());
        editor.putBoolean("vibrationEnabled", vibrationSwitch.isChecked());
        editor.apply();
        Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            Toast.makeText(SettingsActivity.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(SettingsActivity.this, InicioSesion.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }
}
