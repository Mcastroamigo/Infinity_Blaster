package ca.grasley.spaceshooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PowerUpEffect {
    private float x, y;
    private Texture texture;
    private float timer = 0f;
    private final float DURATION = 0.5f;

    public PowerUpEffect(float x, float y, Texture texture) {
        this.x = x;
        this.y = y;
        this.texture = texture;
    }

    public void update(float deltaTime) {
        timer += deltaTime;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public boolean isFinished() {
        return timer >= DURATION;
    }
}

