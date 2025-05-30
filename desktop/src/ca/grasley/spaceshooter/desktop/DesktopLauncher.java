package ca.grasley.spaceshooter.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import ca.grasley.spaceshooter.SpaceShooterGame;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 640;
		config.width = 360;

		// Pasamos null para AndroidInterface porque no se necesita en desktop
		new LwjglApplication(new SpaceShooterGame(null, new DesktopSettingsManager()), config);
	}
}
