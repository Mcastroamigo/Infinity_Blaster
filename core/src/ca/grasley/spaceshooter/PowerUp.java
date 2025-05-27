package ca.grasley.spaceshooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class PowerUp {
    private Rectangle boundingBox;
    private Texture texture;
    private boolean active;

    public PowerUp(float xCenter, float yCenter, Texture texture) {
        float width = 20;
        float height = 20;
        this.boundingBox = new Rectangle(xCenter - width / 2, yCenter - height / 2, width, height);
        this.texture = texture;
        this.active = true;
    }

    public void update(float deltaTime) {
        // Mueve el power-up hacia abajo
        boundingBox.y -= 100 * deltaTime;

        // Desactiva si sale de la pantalla
        if (boundingBox.y + boundingBox.height < 0) {
            active = false;
        }
    }

    public void draw(SpriteBatch batch) {
        if (active) {
            batch.draw(texture, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        }
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
