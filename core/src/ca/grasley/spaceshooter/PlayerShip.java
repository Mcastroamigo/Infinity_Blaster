package ca.grasley.spaceshooter;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

class PlayerShip extends Ship {

    private boolean tripleShotEnabled = false; // Por defecto desactivado

    int lives;

    public PlayerShip(float xCentre, float yCentre,
                      float width, float height,
                      float movementSpeed, int shield,
                      float laserWidth, float laserHeight,
                      float laserMovementSpeed, float timeBetweenShots,
                      TextureRegion shipTextureRegion,
                      TextureRegion shieldTextureRegion,
                      TextureRegion laserTextureRegion) {
        super(xCentre, yCentre, width, height, movementSpeed, shield, laserWidth, laserHeight,
                laserMovementSpeed, timeBetweenShots, shipTextureRegion, shieldTextureRegion, laserTextureRegion);
        lives = 3;
    }

    public void setTripleShotEnabled(boolean enabled) {
        this.tripleShotEnabled = enabled;
    }

    @Override
    public Laser[] fireLasers() {
        if (tripleShotEnabled) {
            Laser[] lasers = new Laser[3];
            lasers[0] = new Laser(
                    boundingBox.x + boundingBox.width * 0.07f,
                    boundingBox.y + boundingBox.height * 0.45f,
                    laserWidth, laserHeight,
                    laserMovementSpeed, laserTextureRegion);

            lasers[1] = new Laser(
                    boundingBox.x + boundingBox.width * 0.5f - laserWidth / 2,
                    boundingBox.y + boundingBox.height * 0.45f,
                    laserWidth, laserHeight,
                    laserMovementSpeed, laserTextureRegion);

            lasers[2] = new Laser(
                    boundingBox.x + boundingBox.width * 0.93f - laserWidth,
                    boundingBox.y + boundingBox.height * 0.45f,
                    laserWidth, laserHeight,
                    laserMovementSpeed, laserTextureRegion);

            timeSinceLastShot = 0;
            return lasers;

        } else {
            // Ahora dispara dos láseres
            Laser[] lasers = new Laser[2];
            lasers[0] = new Laser(
                    boundingBox.x + boundingBox.width * 0.25f - laserWidth / 2,
                    boundingBox.y + boundingBox.height * 0.45f,
                    laserWidth, laserHeight,
                    laserMovementSpeed, laserTextureRegion);

            lasers[1] = new Laser(
                    boundingBox.x + boundingBox.width * 0.75f - laserWidth / 2,
                    boundingBox.y + boundingBox.height * 0.45f,
                    laserWidth, laserHeight,
                    laserMovementSpeed, laserTextureRegion);

            timeSinceLastShot = 0;
            return lasers;
        }
    }
}
