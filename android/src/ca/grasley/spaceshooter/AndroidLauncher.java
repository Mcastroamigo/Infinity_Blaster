package ca.grasley.spaceshooter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.content.Context;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication implements AndroidInterface {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		SettingsManager settingsManager = new AndroidSettingsManager(this);
		initialize(new SpaceShooterGame(this, settingsManager), config);  // ✅ Se pasan ambos objetos
	}

	@Override
	public void vibrate(int milliseconds) {
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		if (v != null && v.hasVibrator()) {
			v.vibrate(milliseconds);
		}
	}

	// Este método se puede llamar desde la clase InicioSesion cuando el usuario se autentique
	public void launchGameActivity() {
		Intent intent = new Intent(this, AndroidLauncher.class);
		startActivity(intent);
	}
}
