package ca.grasley.spaceshooter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "app_settings";

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        float fontScale = prefs.getFloat("font_scale", 1.0f);

        Configuration config = newBase.getResources().getConfiguration();
        config.fontScale = fontScale;

        Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }
}
