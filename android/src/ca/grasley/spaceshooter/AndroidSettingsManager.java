package ca.grasley.spaceshooter;

import android.content.Context;
import android.content.SharedPreferences;

// En el proyecto Android
public class AndroidSettingsManager implements SettingsManager {
    private Context context;
    private static final String PREFS_NAME = "app_settings";
    private static final String VIBRATION_ENABLED_KEY = "vibrationEnabled";

    public AndroidSettingsManager(Context context) {
        this.context = context;
    }

    @Override
    public boolean isVibrationEnabled() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(VIBRATION_ENABLED_KEY, true);
    }
}
