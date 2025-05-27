package ca.grasley.spaceshooter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
public class PowerUp {
    private Rectangle boundingBox;
    private Texture texture;
    private boolean active;
    private PowerUpType type;

    public PowerUp(float xCenter, float yCenter, Texture texture) {
        float width = 20;
        float height = 20;
        this.boundingBox = new Rectangle(xCenter - width / 2, yCenter - height / 2, width, height);
        this.texture = texture;
        this.active = true;

        // Tipo aleatorio
        PowerUpType[] values = PowerUpType.values();
        this.type = values[MathUtils.random(values.length - 1)];
    }

    public PowerUpType getType() {
        return type;
    }

    public void update(float deltaTime) {
        boundingBox.y -= 100 * deltaTime;
        if (boundingBox.y + boundingBox.height < 0) active = false;
    }

    public void draw(SpriteBatch batch) {
        float scale = 0.5f;
        batch.draw(texture, boundingBox.x, boundingBox.y,
                boundingBox.width * scale, boundingBox.height * scale);
    }

    public boolean collidesWith(Rectangle other) {
        return active && boundingBox.overlaps(other);
    }

    public void collect() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }
}
