package ca.grasley.spaceshooter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;

public class SettingsActivity extends BaseActivity  {

    private ImageView profileImage;
    private TextView userName;
    private SeekBar soundSeekBar;
    private Switch vibrationSwitch;
    private View vibrationIndicator;
    private Spinner languageSpinner;
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

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Referencias UI
        profileImage = findViewById(R.id.profileImage);
        userName = findViewById(R.id.userName);
        soundSeekBar = findViewById(R.id.soundSeekBar);
        vibrationSwitch = findViewById(R.id.vibrationSwitch);
        vibrationIndicator = findViewById(R.id.vibrationIndicator);
        languageSpinner = findViewById(R.id.languageSpinner);
        textSizeSeekBar = findViewById(R.id.textSizeSeekBar);
        logoutButton = findViewById(R.id.logoutButton);
        saveButton = findViewById(R.id.saveButton);

        loadSettings();

        soundSeekBar.setMax(100);
        soundSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setMusicVolume(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> updateVibrationIndicator(isChecked));

        textSizeSeekBar.setMax(100);
        textSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float scale = (progress + 50) / 100.0f; // Escala de 0.5 a 1.5
                saveFontScale(scale);
                applyFontScale(scale);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        logoutButton.setOnClickListener(v -> logout());

        saveButton.setOnClickListener(v -> saveSettings());
    }

    private void setMusicVolume(int progress) {
        float volume = progress / 100f;
        MusicManager.setVolume(volume);
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String savedUserName = prefs.getString("userName", "Nombre de Usuario");
        userName.setText(savedUserName);

        int soundLevel = prefs.getInt("soundLevel", 50);
        soundSeekBar.setProgress(soundLevel);
        setMusicVolume(soundLevel);

        boolean vibrationEnabled = prefs.getBoolean("vibrationEnabled", true);
        vibrationSwitch.setChecked(vibrationEnabled);
        updateVibrationIndicator(vibrationEnabled);

        int languagePosition = prefs.getInt("languagePosition", 0);
        languageSpinner.setSelection(languagePosition);

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
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putFloat("font_scale", scale).apply();
    }

    private void applyFontScale(float scale) {
        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = scale;

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        getResources().updateConfiguration(configuration, metrics);

        recreate(); // Recarga la UI con el nuevo tamaño
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("userName", userName.getText().toString());
        editor.putInt("soundLevel", soundSeekBar.getProgress());
        editor.putBoolean("vibrationEnabled", vibrationSwitch.isChecked());
        editor.putInt("languagePosition", languageSpinner.getSelectedItemPosition());
        // El textSize (font_scale) ya se guarda en tiempo real
        editor.apply();

        Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            Toast.makeText(SettingsActivity.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsActivity.this, InicioSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
