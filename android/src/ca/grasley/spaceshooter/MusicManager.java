// MusicManager.java
package ca.grasley.spaceshooter;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
public class MusicManager {
    private static MediaPlayer mediaPlayer;

    public static void start(Context context, int resId) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, resId);
            float savedVolume = getSavedVolume(context); // Aplicar volumen guardado
            mediaPlayer.setVolume(savedVolume, savedVolume);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    public static void setVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

    private static float getSavedVolume(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        int volumeProgress = prefs.getInt("soundLevel", 100);
        return volumeProgress / 100f;
    }

    public static void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
