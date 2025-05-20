package ca.grasley.spaceshooter;

import com.badlogic.gdx.Game;
import java.util.Random;

public class SpaceShooterGame extends Game {

    private AndroidInterface androidInterface;
    private SettingsManager settingsManager;
    private GameScreen gameScreen;

    public static Random random = new Random();

    public SpaceShooterGame(AndroidInterface androidInterface, SettingsManager settingsManager) {
        this.androidInterface = androidInterface;
        this.settingsManager = settingsManager;
    }

    public AndroidInterface getAndroidInterface() {
        return androidInterface;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    @Override
    public void create() {
        gameScreen = new GameScreen(this, settingsManager);  // ✅ Se pasa el settingsManager correctamente
        setScreen(gameScreen);
    }

    @Override
    public void dispose() {
        if (gameScreen != null) {
            gameScreen.dispose();
        }
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        if (gameScreen != null) {
            gameScreen.resize(width, height);
        }
    }
}
