package ca.grasley.spaceshooter;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;

class GameScreen implements Screen {
    private SpaceShooterGame game;
    private SettingsManager settingsManager;
    private Camera camera;
    private Viewport viewport;
    private boolean gameOver = false;
    private AndroidInterface androidInterface;
    //power up

    private long lastPowerUpTime = 0;
    private float powerUpDropChance = 0.2f;
    private LinkedList<PowerUp> powerUpList = new LinkedList<>();
    private Texture powerUpTexture;


    private long gameOverTimer = 0;
    //graphics
    private SpriteBatch batch;
    private TextureAtlas textureAtlas;
    private Texture explosionTexture;
    private TextureRegion[] backgrounds;
    private float backgroundHeight;
    private TextureRegion playerShipTextureRegion, playerShieldTextureRegion,
            enemyShipTextureRegion, enemyShieldTextureRegion,
            playerLaserTextureRegion, enemyLaserTextureRegion;
    private float[] backgroundOffsets;
    private float backgroundMaxScrollingSpeed;
    private float timeBetweenEnemySpawns = 1f;
    private float enemySpawnTimer = 0;
    private final float WORLD_WIDTH = 72;
    private final float WORLD_HEIGHT = 128;
    private final float TOUCH_MOVEMENT_THRESHOLD = 5f;
    private PlayerShip playerShip;
    private LinkedList<EnemyShip> enemyShipList;
    private LinkedList<Laser> playerLaserList;
    private LinkedList<Laser> enemyLaserList;
    private LinkedList<Explosion> explosionList;
    private boolean difficultyIncreased = false;
    private boolean tripleShotEnabled = false;   // para 4000 pts
    private float powerUpSpawnTimer = 0f;
    private final float POWERUP_SPAWN_INTERVAL = 10f;
    private void spawnPowerUps(float deltaTime) {
        powerUpSpawnTimer += deltaTime;

        if (powerUpSpawnTimer >= POWERUP_SPAWN_INTERVAL) {
            powerUpSpawnTimer = 0f;

            // Generar posición X aleatoria dentro del mundo (ajusta según tus límites)
            float x = MathUtils.random(0, WORLD_WIDTH - powerUpTexture.getWidth());
            float y = WORLD_HEIGHT; // aparece justo arriba de la pantalla

            // Crear y añadir el power-up a la lista
            PowerUp newPowerUp = new PowerUp(x, y, powerUpTexture);
            powerUpList.add(newPowerUp);
        }
    }

    private int score = 0;
    private BitmapFont font;
    private float hudVerticalMargin, hudLeftX, hudRightX, hudCentreX, hudRow1Y, hudRow2Y, hudSectionWidth;

    GameScreen(SpaceShooterGame game, AndroidInterface androidInterface, SettingsManager settingsManager) {
        this.game = game;
        this.settingsManager = settingsManager;
        this.androidInterface = androidInterface;  // ✅ Aquí se arregla todo
        powerUpTexture = new Texture(Gdx.files.internal("power-up.png"));
        powerUpList = new LinkedList<>();



        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        batch = new SpriteBatch();

        textureAtlas = new TextureAtlas("images.atlas");

        backgrounds = new TextureRegion[4];
        backgrounds[0] = textureAtlas.findRegion("Starscape00");
        backgrounds[1] = textureAtlas.findRegion("Starscape01");
        backgrounds[2] = textureAtlas.findRegion("Starscape02");
        backgrounds[3] = textureAtlas.findRegion("Starscape03");

        backgroundOffsets = new float[4];
        backgroundHeight = WORLD_HEIGHT * 2;
        backgroundMaxScrollingSpeed = WORLD_HEIGHT / 4f;

        playerShipTextureRegion = textureAtlas.findRegion("playerShip2_blue");
        enemyShipTextureRegion = textureAtlas.findRegion("enemyRed3");
        playerShieldTextureRegion = textureAtlas.findRegion("shield2");
        enemyShieldTextureRegion = textureAtlas.findRegion("shield1");
        enemyShieldTextureRegion.flip(false, true);

        playerLaserTextureRegion = textureAtlas.findRegion("laserBlue03");
        enemyLaserTextureRegion = textureAtlas.findRegion("laserRed03");

        explosionTexture = new Texture("explosion.png");

        playerShip = new PlayerShip(WORLD_WIDTH / 2, WORLD_HEIGHT / 4,
                10, 10,
                48, 10,
                0.4f, 4, 45, 0.5f,
                playerShipTextureRegion, playerShieldTextureRegion, playerLaserTextureRegion);

        enemyShipList = new LinkedList<>();
        playerLaserList = new LinkedList<>();
        enemyLaserList = new LinkedList<>();
        explosionList = new LinkedList<>();

        Gdx.app.log("GameScreen", "Backgrounds array initialized: " + (backgrounds != null));
        Gdx.app.log("GameScreen", "BackgroundOffsets array initialized: " + (backgroundOffsets != null));
        Gdx.app.log("GameScreen", "SpriteBatch initialized: " + (batch != null));
        Gdx.app.log("GameScreen", "PlayerShip initialized: " + (playerShip != null));

        prepareHUD();
    }

    private void prepareHUD() {
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("EdgeOfTheGalaxyRegular-OVEa6.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.size = 72;
        fontParameter.borderWidth = 3.6f;
        fontParameter.color = new Color(1, 1, 1, 0.3f);
        fontParameter.borderColor = new Color(0, 0, 0, 0.3f);

        font = fontGenerator.generateFont(fontParameter);

        font.getData().setScale(0.08f);

        hudVerticalMargin = font.getCapHeight() / 2;
        hudLeftX = hudVerticalMargin;
        hudRightX = WORLD_WIDTH * 2 / 3 - hudLeftX;
        hudCentreX = WORLD_WIDTH / 3;
        hudRow1Y = WORLD_HEIGHT - hudVerticalMargin;
        hudRow2Y = hudRow1Y - hudVerticalMargin - font.getCapHeight();
        hudSectionWidth = WORLD_WIDTH / 3;
    }

    @Override
    public void render(float deltaTime) {
        batch.begin();

        if (gameOver) {
            font.draw(batch, "GAME OVER", WORLD_WIDTH / 2 - 10, WORLD_HEIGHT / 2);
            batch.end();

            if (gameOverTimer == 0) {
                gameOverTimer = TimeUtils.millis();
                guardarPuntosEnFirebase(score);
            } else if (TimeUtils.timeSinceMillis(gameOverTimer) > 2000) {
                androidInterface.goToMainPage();
            }

            return;
        }

        // Fondo en movimiento
        renderBackground(deltaTime);

        // Entrada y movimiento del jugador
        detectInput(deltaTime);
        playerShip.update(deltaTime);

        // Generación y render de enemigos
        spawnEnemyShips(deltaTime);
        ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
        while (enemyShipListIterator.hasNext()) {
            EnemyShip enemyShip = enemyShipListIterator.next();
            moveEnemy(enemyShip, deltaTime);
            enemyShip.update(deltaTime);
            enemyShip.draw(batch);
        }

        // Dibujar nave del jugador
        playerShip.draw(batch);

        // Power-ups
        spawnPowerUps(deltaTime);
        ListIterator<PowerUp> powerUpIterator = powerUpList.listIterator();
        while (powerUpIterator.hasNext()) {
            PowerUp powerUp = powerUpIterator.next();
            powerUp.update(deltaTime);
            powerUp.draw(batch);
        }

        // Láseres y colisiones
        renderLasers(deltaTime);
        detectCollisions();

        // Explosiones
        updateAndRenderExplosions(deltaTime);

        // HUD
        updateAndRenderHUD();

        batch.end();
    }


    private void updateAndRenderHUD() {
        font.draw(batch, "Score", hudLeftX, hudRow1Y, hudSectionWidth, Align.left, false);
        font.draw(batch, "Shield", hudCentreX, hudRow1Y, hudSectionWidth, Align.center, false);
        font.draw(batch, "Lives", hudRightX, hudRow1Y, hudSectionWidth, Align.right, false);

        font.draw(batch, String.format(Locale.getDefault(), "%06d", score), hudLeftX, hudRow2Y, hudSectionWidth, Align.left, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.shield), hudCentreX, hudRow2Y, hudSectionWidth, Align.center, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.lives), hudRightX, hudRow2Y, hudSectionWidth, Align.right, false);
    }

    private void spawnEnemyShips(float deltaTime) {
        // Aumentar dificultad al llegar a 2000 puntos
        if (score >= 2000 && !difficultyIncreased) {
            difficultyIncreased = true;
            timeBetweenEnemySpawns *= 0.75f; // Aumenta la frecuencia un 25%
        }

        enemySpawnTimer += deltaTime;

        if (enemySpawnTimer > timeBetweenEnemySpawns) {
            int shield = difficultyIncreased ? 11 : 10; // +1 de escudo si aumenta la dificultad
            float laserSpeed = difficultyIncreased ? 60 : 50; // proyectiles más rápidos

            enemyShipList.add(new EnemyShip(
                    SpaceShooterGame.random.nextFloat() * (WORLD_WIDTH - 10) + 5,
                    WORLD_HEIGHT - 5,
                    10, shield,
                    48, 1,
                    0.3f, 5, laserSpeed, 0.8f,
                    enemyShipTextureRegion, enemyShieldTextureRegion, enemyLaserTextureRegion));

            enemySpawnTimer -= timeBetweenEnemySpawns;
        }
    }
    private void detectInput(float deltaTime) {
        if (playerShip == null) {
            Gdx.app.log("GameScreen", "PlayerShip is null in detectInput!");
            return;
        }

        //keyboard input
        float leftLimit, rightLimit, upLimit, downLimit;
        leftLimit = -playerShip.boundingBox.x;
        downLimit = -playerShip.boundingBox.y;
        rightLimit = WORLD_WIDTH - playerShip.boundingBox.x - playerShip.boundingBox.width;
        upLimit = (float) WORLD_HEIGHT / 2 - playerShip.boundingBox.y - playerShip.boundingBox.height;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && rightLimit > 0) {
            playerShip.translate(Math.min(playerShip.movementSpeed * deltaTime, rightLimit), 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && upLimit > 0) {
            playerShip.translate(0f, Math.min(playerShip.movementSpeed * deltaTime, upLimit));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && leftLimit < 0) {
            playerShip.translate(Math.max(-playerShip.movementSpeed * deltaTime, leftLimit), 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && downLimit < 0) {
            playerShip.translate(0f, Math.max(-playerShip.movementSpeed * deltaTime, downLimit));
        }

        //touch input (also mouse)
        if (Gdx.input.isTouched()) {
            float xTouchPixels = Gdx.input.getX();
            float yTouchPixels = Gdx.input.getY();

            Vector2 touchPoint = new Vector2(xTouchPixels, yTouchPixels);
            touchPoint = viewport.unproject(touchPoint);

            Vector2 playerShipCentre = new Vector2(
                    playerShip.boundingBox.x + playerShip.boundingBox.width / 2,
                    playerShip.boundingBox.y + playerShip.boundingBox.height / 2);

            float touchDistance = touchPoint.dst(playerShipCentre);

            if (touchDistance > TOUCH_MOVEMENT_THRESHOLD) {
                float xTouchDifference = touchPoint.x - playerShipCentre.x;
                float yTouchDifference = touchPoint.y - playerShipCentre.y;

                float xMove = xTouchDifference / touchDistance * playerShip.movementSpeed * deltaTime;
                float yMove = yTouchDifference / touchDistance * playerShip.movementSpeed * deltaTime;

                if (xMove > 0) xMove = Math.min(xMove, rightLimit);
                else xMove = Math.max(xMove, leftLimit);

                if (yMove > 0) yMove = Math.min(yMove, upLimit);
                else yMove = Math.max(yMove, downLimit);

                playerShip.translate(xMove, yMove);
            }
        }
    }


    private void guardarPuntosEnFirebase(int puntos) {
        if (androidInterface != null) {
            androidInterface.savePoints(puntos);
        } else {
            Gdx.app.log("GameScreen", "AndroidInterface no inicializada, no se pueden guardar puntos.");
        }
    }






    private void moveEnemy(EnemyShip enemyShip, float deltaTime) {
        float leftLimit, rightLimit, upLimit, downLimit;
        leftLimit = -enemyShip.boundingBox.x;
        downLimit = (float) WORLD_HEIGHT / 2 - enemyShip.boundingBox.y;
        rightLimit = WORLD_WIDTH - enemyShip.boundingBox.x - enemyShip.boundingBox.width;
        upLimit = WORLD_HEIGHT - enemyShip.boundingBox.y - enemyShip.boundingBox.height;

        float xMove = enemyShip.getDirectionVector().x * enemyShip.movementSpeed * deltaTime;
        float yMove = enemyShip.getDirectionVector().y * enemyShip.movementSpeed * deltaTime;

        if (xMove > 0) xMove = Math.min(xMove, rightLimit);
        else xMove = Math.max(xMove, leftLimit);

        if (yMove > 0) yMove = Math.min(yMove, upLimit);
        else yMove = Math.max(yMove, downLimit);

        enemyShip.translate(xMove, yMove);
    }

    private void detectCollisions() {
        ListIterator<Laser> laserListIterator = playerLaserList.listIterator();
        while (laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
            while (enemyShipListIterator.hasNext()) {
                EnemyShip enemyShip = enemyShipListIterator.next();

                if (enemyShip.intersects(laser.boundingBox)) {
                    if (enemyShip.hitAndCheckDestroyed(laser)) {
                        enemyShipListIterator.remove();
                        explosionList.add(new Explosion(explosionTexture, new Rectangle(enemyShip.boundingBox), 0.7f));
                        if (Math.random() < powerUpDropChance) {
                            powerUpList.add(new PowerUp(
                                    enemyShip.boundingBox.x + enemyShip.boundingBox.width / 2,
                                    enemyShip.boundingBox.y,
                                    powerUpTexture
                            ));
                        }
                        if (Gdx.app.getType() == Application.ApplicationType.Android && settingsManager.isVibrationEnabled()) {
                            game.getAndroidInterface().vibrate(100);
                        }

                        score += 100;
                    }
                    laserListIterator.remove();
                    break;
                }
            }
        }
        laserListIterator = enemyLaserList.listIterator();
        while (laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            if (playerShip.intersects(laser.boundingBox)) {
                if (playerShip.hitAndCheckDestroyed(laser)) {
                    explosionList.add(
                            new Explosion(explosionTexture,
                                    new Rectangle(playerShip.boundingBox),
                                    1.6f));
                    playerShip.shield = 10;
                    playerShip.lives--;

                    if (playerShip.lives <= 0) {
                        gameOver = true;
                    }
                }
                laserListIterator.remove();
            }
        }
    }
    private void updateAndRenderExplosions(float deltaTime) {
        ListIterator<Explosion> explosionListIterator = explosionList.listIterator();
        while (explosionListIterator.hasNext()) {
            Explosion explosion = explosionListIterator.next();
            explosion.update(deltaTime);
            if (explosion.isFinished()) {
                explosionListIterator.remove();
            } else {
                explosion.draw(batch);
            }
        }
    }

    private void renderLasers(float deltaTime) {
        if (playerShip.canFireLaser()) {
            Laser[] lasers = playerShip.fireLasers();
            playerLaserList.addAll(Arrays.asList(lasers));
        }
        ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
        while (enemyShipListIterator.hasNext()) {
            EnemyShip enemyShip = enemyShipListIterator.next();
            if (enemyShip.canFireLaser()) {
                Laser[] lasers = enemyShip.fireLasers();
                enemyLaserList.addAll(Arrays.asList(lasers));
            }
        }
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y += laser.movementSpeed * deltaTime;
            if (laser.boundingBox.y > WORLD_HEIGHT) {
                iterator.remove();
            }
        }
        iterator = enemyLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y -= laser.movementSpeed * deltaTime;
            if (laser.boundingBox.y + laser.boundingBox.height < 0) {
                iterator.remove();
            }
        }
    }

    private void renderBackground(float deltaTime) {
        // Actualizar offsets para desplazamiento del fondo
        for (int i = 0; i < backgrounds.length; i++) {
            backgroundOffsets[i] += backgroundMaxScrollingSpeed * (i + 1) / backgrounds.length * deltaTime;
            if (backgroundOffsets[i] > backgroundHeight) {
                backgroundOffsets[i] -= backgroundHeight;
            }
        }

        // Dibujar fondos en capas
        for (int i = 0; i < backgrounds.length; i++) {
            float y = -backgroundOffsets[i];
            batch.draw(backgrounds[i], 0, y, WORLD_WIDTH, backgroundHeight);
            batch.draw(backgrounds[i], 0, y + backgroundHeight, WORLD_WIDTH, backgroundHeight);
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("GameScreen", "Resizing to: " + width + "x" + height);
        if (viewport != null) {
            viewport.update(width, height, true);
        } else {
            Gdx.app.log("GameScreen", "Viewport is null during resize!");
        }
        if (batch != null) {
            batch.setProjectionMatrix(camera.combined);
        } else {
            Gdx.app.log("GameScreen", "SpriteBatch is null during resize!");
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void show() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (powerUpTexture != null) {
            powerUpTexture.dispose();
        }
    }
}