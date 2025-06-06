package ca.grasley.spaceshooter;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

class PlayerShip extends Ship {

    int lives;
    public boolean isGlowing = false;
    private float glowTimer = 0f;
    private float glowSpeed = 4f;
    public boolean hasActivePowerUp = false;

    public PlayerShip(float xCentre, float yCentre,
                      float width, float height,
                      float movementSpeed, int shield,
                      float laserWidth, float laserHeight,
                      float laserMovementSpeed, float timeBetweenShots,
                      TextureRegion shipTextureRegion,
                      TextureRegion shieldTextureRegion,
                      TextureRegion laserTextureRegion) {
        super(xCentre, yCentre, width, height, movementSpeed, shield,
                laserWidth, laserHeight, laserMovementSpeed,
                timeBetweenShots, shipTextureRegion,
                shieldTextureRegion, laserTextureRegion);
        lives = 3;
    }

    @Override
    public Laser[] fireLasers() {
        Laser[] laser = new Laser[2];
        laser[0] = new Laser(boundingBox.x + boundingBox.width * 0.07f,
                boundingBox.y + boundingBox.height * 0.45f,
                laserWidth, laserHeight,
                laserMovementSpeed, laserTextureRegion);
        laser[1] = new Laser(boundingBox.x + boundingBox.width * 0.93f,
                boundingBox.y + boundingBox.height * 0.45f,
                laserWidth, laserHeight,
                laserMovementSpeed, laserTextureRegion);

        timeSinceLastShot = 0;

        return laser;
    }

    public Laser[] fireTripleLasers() {
        Laser[] laser = new Laser[3];
        // Láser izquierdo
        laser[0] = new Laser(boundingBox.x + boundingBox.width * 0.07f,
                boundingBox.y + boundingBox.height * 0.45f,
                laserWidth, laserHeight,
                laserMovementSpeed, laserTextureRegion);
        // Láser central
        laser[1] = new Laser(boundingBox.x + boundingBox.width * 0.50f - laserWidth / 2,
                boundingBox.y + boundingBox.height * 0.60f,
                laserWidth, laserHeight,
                laserMovementSpeed, laserTextureRegion);
        // Láser derecho
        laser[2] = new Laser(boundingBox.x + boundingBox.width * 0.93f,
                boundingBox.y + boundingBox.height * 0.45f,
                laserWidth, laserHeight,
                laserMovementSpeed, laserTextureRegion);

        timeSinceLastShot = 0;

        return laser;
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }
    public Rectangle getBoundingBox() {
        return boundingBox;
    }
    public void incrementGlowTimer(float deltaTime) {
        glowTimer += deltaTime;
    }
    public float getGlowTimer() {
        return glowTimer;
    }

    public void resetGlowTimer() {
        glowTimer = 0f;
    }

}
