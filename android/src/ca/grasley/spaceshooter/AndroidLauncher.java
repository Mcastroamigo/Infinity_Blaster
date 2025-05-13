package ca.grasley.spaceshooter;

import android.content.Intent;
import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import ca.grasley.spaceshooter.SpaceShooterGame;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new SpaceShooterGame(), config);
	}

	// Este método se puede llamar desde la clase InicioSesion cuando el usuario se autentique
	public void launchGameActivity() {
		Intent intent = new Intent(this, AndroidLauncher.class);
		startActivity(intent);
	}
}
