package ca.grasley.spaceshooter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SettingsActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity); // Cambia por el nombre correcto de tu XML

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

        vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateVibrationIndicator(isChecked);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
            }
        });
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String savedUserName = prefs.getString("userName", "Nombre de Usuario");
        userName.setText(savedUserName);

        int soundLevel = prefs.getInt("soundLevel", 50);
        soundSeekBar.setProgress(soundLevel);

        boolean vibrationEnabled = prefs.getBoolean("vibrationEnabled", true);
        vibrationSwitch.setChecked(vibrationEnabled);
        updateVibrationIndicator(vibrationEnabled);

        int languagePosition = prefs.getInt("languagePosition", 0);
        languageSpinner.setSelection(languagePosition);

        int textSize = prefs.getInt("textSize", 50);
        textSizeSeekBar.setProgress(textSize);
    }

    private void updateVibrationIndicator(boolean enabled) {
        int color;
        if (enabled) {
            color = ContextCompat.getColor(this, android.R.color.holo_green_light);
        } else {
            color = ContextCompat.getColor(this, android.R.color.darker_gray);
        }
        vibrationIndicator.setBackgroundColor(color);
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();

        editor.putString("userName", userName.getText().toString());
        editor.putInt("soundLevel", soundSeekBar.getProgress());
        editor.putBoolean("vibrationEnabled", vibrationSwitch.isChecked());
        editor.putInt("languagePosition", languageSpinner.getSelectedItemPosition());
        editor.putInt("textSize", textSizeSeekBar.getProgress());

        editor.apply();

        Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        // Lógica real para cerrar sesión (borrar datos, tokens, etc)
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        finish();
    }
}
