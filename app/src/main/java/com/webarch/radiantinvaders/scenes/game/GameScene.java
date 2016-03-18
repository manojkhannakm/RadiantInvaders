package com.webarch.radiantinvaders.scenes.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.webarch.radiantinvaders.BaseScene;
import com.webarch.radiantinvaders.scenes.game.entities.EnemyEntity;
import com.webarch.radiantinvaders.scenes.game.entities.Player;
import com.webarch.radiantinvaders.scenes.game.entities.PlayerProjectile;
import com.webarch.radiantinvaders.scenes.game.entities.enemyentites.SquareDiamondEnemy;
import com.webarch.radiantinvaders.scenes.main.MainScene;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.audio.sound.SoundManager;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.math.MathUtils;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Manoj Khanna
 */

public class GameScene extends BaseScene implements IAccelerationListener, IOnSceneTouchListener, ContactListener {

    public static short wallCategoryBit = 1, playerCategoryBit = 2, otherCategoryBit = 4,
            wallMaskBit = (short) (wallCategoryBit + playerCategoryBit),
            playerMaskBit = (short) (wallCategoryBit + playerCategoryBit + otherCategoryBit),
            otherMaskBit = (short) (playerCategoryBit + otherCategoryBit);
    private EntitySpawner entitySpawner;
    private Sprite gameBackgroundSprite;
    private GameInterface gameInterface;
    private PhysicsWorld physicsWorld;
    private Body[] wallBodies = new Body[4];
    private HashMap<String, Sound> sounds = new HashMap<>();
    private Camera camera;
    private Player player;
    private float playerX1, playerY1, shakeIntensity;
    private long startTime, shakeTime;
    private TimerHandler enemySpawnTimerHandler;
    private boolean isShaking;

    @Override
    public void onCreate() {
        entitySpawner = new EntitySpawner(gameManager, this);

        gameBackgroundSprite = new Sprite(cameraHalfWidth, cameraHalfHeight, entitySpawner.getGameBackgroundTextureRegion(), game.getVertexBufferObjectManager());
        attachChild(gameBackgroundSprite);

        gameInterface = new GameInterface(gameManager, this);
        attachChild(gameInterface);

        physicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
        FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0.0f, 0.0f, 0.0f, false, wallCategoryBit, wallMaskBit, (short) 0);
        wallBodies[0] = PhysicsFactory.createBoxBody(physicsWorld, 0, cameraHalfHeight, 1, cameraHeight, BodyDef.BodyType.StaticBody, wallFixtureDef);
        wallBodies[1] = PhysicsFactory.createBoxBody(physicsWorld, cameraHalfWidth, cameraHeight, cameraWidth, 1, BodyDef.BodyType.StaticBody, wallFixtureDef);
        wallBodies[2] = PhysicsFactory.createBoxBody(physicsWorld, cameraWidth, cameraHalfHeight, 1, cameraHeight, BodyDef.BodyType.StaticBody, wallFixtureDef);
        wallBodies[3] = PhysicsFactory.createBoxBody(physicsWorld, cameraHalfWidth, 0, cameraWidth, 1, BodyDef.BodyType.StaticBody, wallFixtureDef);

        try {
            SoundManager soundManager = game.getSoundManager();
            sounds.put("player_shoot", SoundFactory.createSoundFromAsset(soundManager, game, "player_shoot.ogg"));
            sounds.put("player_death", SoundFactory.createSoundFromAsset(soundManager, game, "player_death.ogg"));
            sounds.put("player_level_up", SoundFactory.createSoundFromAsset(soundManager, game, "player_level_up.ogg"));
            sounds.put("player_level_down", SoundFactory.createSoundFromAsset(soundManager, game, "player_level_down.ogg"));
            sounds.put("enemy_spawn", SoundFactory.createSoundFromAsset(soundManager, game, "enemy_spawn.ogg"));
            sounds.put("enemy_death", SoundFactory.createSoundFromAsset(soundManager, game, "enemy_death.ogg"));
            sounds.put("enemy_clash", SoundFactory.createSoundFromAsset(soundManager, game, "enemy_clash.ogg"));
            sounds.put("stone_1", SoundFactory.createSoundFromAsset(soundManager, game, "stone_1.ogg"));
            sounds.put("stone_2", SoundFactory.createSoundFromAsset(soundManager, game, "stone_2.ogg"));
            sounds.put("stone_3", SoundFactory.createSoundFromAsset(soundManager, game, "stone_3.ogg"));
            sounds.put("stone_4", SoundFactory.createSoundFromAsset(soundManager, game, "stone_4.ogg"));
            sounds.put("stone_5", SoundFactory.createSoundFromAsset(soundManager, game, "stone_5.ogg"));
            sounds.put("stone_6", SoundFactory.createSoundFromAsset(soundManager, game, "stone_6.ogg"));
            sounds.put("stone_7", SoundFactory.createSoundFromAsset(soundManager, game, "stone_7.ogg"));

            for (Sound sound : sounds.values()) {
                sound.setVolume(0.1f);
            }
        } catch (IOException ignored) {
        }

        camera = gameManager.getCamera();

//		TODO: Remove later
//		attachChild(new DebugRenderer(physicsWorld, game.getVertexBufferObjectManager()));
    }

    @Override
    public void onStart() {
        player = entitySpawner.spawnPlayer(cameraHalfWidth, cameraHalfHeight);
        playerX1 = player.getX();
        playerY1 = player.getY();

        isShaking = false;
        shakeTime = 0;
        shakeIntensity = 0;

        game.enableAccelerationSensor(this);
        setOnSceneTouchListener(this);
        physicsWorld.setContactListener(this);
        registerUpdateHandler(physicsWorld);

        startTime = System.currentTimeMillis();

        registerUpdateHandler(new TimerHandler(5.0f, true, new ITimerCallback() {

            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                long curTime = System.currentTimeMillis() - startTime;
                float minTimerSeconds;
                if (curTime <= 10000) {
                    minTimerSeconds = 2.0f;
                } else if (curTime <= 30000) {
                    minTimerSeconds = 1.75f;
                } else if (curTime <= 60000) {
                    minTimerSeconds = 1.5f;
                } else if (curTime <= 120000) {
                    minTimerSeconds = 1.25f;
                } else {
                    minTimerSeconds = 1.0f;
                }
                pTimerHandler.setTimerSeconds(MathUtils.random(minTimerSeconds, minTimerSeconds + 2.0f));

                final float playerTwoWidth = player.getWidth() * 2.0f, playerTwoHeight = player.getHeight() * 2.0f,
                        x = MathUtils.random(0.0f, 1.0f) <= 0.5f ? MathUtils.random(-playerTwoWidth, playerX1 - playerTwoWidth) : MathUtils.random(playerX1 + playerTwoWidth, cameraWidth),
                        y = MathUtils.random(0.0f, 1.0f) <= 0.5f ? MathUtils.random(-playerTwoHeight, playerY1 - playerTwoHeight) : MathUtils.random(playerY1 + playerTwoHeight, cameraHeight),
                        spawnRandom = MathUtils.random(0.0f, 1.0f);
                if (spawnRandom <= 0.3f) {
                    entitySpawner.spawnGreenArrowEnemy(x, y);
                } else if (spawnRandom <= 0.6f) {
                    entitySpawner.spawnBlueArrowEnemy(x, y);
                } else if (spawnRandom <= 0.9f) {
                    entitySpawner.spawnSquareDiamondEnemy(x, y);
                } else {
                    pTimerHandler.setTimerSeconds(MathUtils.random(3.0f, 5.0f));

                    final long enemySpawnStopTime = System.currentTimeMillis() + 1500;
                    enemySpawnTimerHandler = new TimerHandler(0.25f, true, new ITimerCallback() {

                        @Override
                        public void onTimePassed(TimerHandler pTimerHandler) {
                            if (System.currentTimeMillis() > enemySpawnStopTime) {
                                unregisterUpdateHandler(enemySpawnTimerHandler);
                                return;
                            }

                            entitySpawner.spawnGreenArrowEnemy(MathUtils.random(x - 100.0f, x + 100.0f), MathUtils.random(y - 100.0f, y + 100.0f));
                        }

                    });
                    registerUpdateHandler(enemySpawnTimerHandler);
                }
            }

        }));
    }

    @Override
    public void onDestroy() {
        game.disableAccelerationSensor();
        setOnSceneTouchListener(null);
        physicsWorld.setContactListener(null);
        clearUpdateHandlers();

        gameInterface.detachSelf();
        gameBackgroundSprite.detachSelf();

        for (Sound sound : sounds.values()) {
            sound.release();
        }
        entitySpawner.onDestroy();
        for (Body body : wallBodies) {
            physicsWorld.destroyBody(body);
        }
        physicsWorld.dispose();
        gameInterface.dispose();
        gameBackgroundSprite.dispose();

        player = null;
        sounds = null;
        physicsWorld = null;
        gameBackgroundSprite = null;
        entitySpawner = null;
    }

    @Override
    public void onBackPressed() {
        gameManager.setScene(new MainScene());
    }

    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {
        super.onManagedUpdate(pSecondsElapsed);

        if (player != null) {
            float playerX2 = player.getX(), playerY2 = player.getY(),
                    playerDx = playerX2 - playerX1, playerDy = playerY2 - playerY1;
            gameBackgroundSprite.setPosition(gameBackgroundSprite.getX() - playerDx, gameBackgroundSprite.getY() - playerDy);
            for (Entity staticEntity : entitySpawner.getStaticEntities()) {
                Body body = staticEntity.getBody();
                if (body != null) {
                    Vector2 vector2 = Vector2Pool.obtain((staticEntity.getX() - playerDx) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (staticEntity.getY() - playerDy) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
                    body.setTransform(vector2, body.getAngle());
                    Vector2Pool.recycle(vector2);
                } else {
                    staticEntity.setPosition(staticEntity.getX() - playerDx, staticEntity.getY() - playerDy);
                }
            }
            playerX1 = playerX2;
            playerY1 = playerY2;
        }

        if (isShaking) {
            if (System.currentTimeMillis() < shakeTime) {
                camera.setCenter(cameraHalfWidth + MathUtils.random(15.0f, 25.0f) * shakeIntensity * (MathUtils.random(0.0f, 1.0f) <= 0.5f ? -1 : 1),
                        cameraHalfHeight + MathUtils.random(15.0f, 25.0f) * shakeIntensity * (MathUtils.random(0.0f, 1.0f) <= 0.5f ? -1 : 1));
            } else {
                isShaking = false;
                shakeTime = 0;
                shakeIntensity = 0.0f;
                camera.setCenter(cameraHalfWidth, cameraHalfHeight);
            }
        }
    }

    @Override
    public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
    }

    @Override
    public void onAccelerationChanged(AccelerationData pAccelerationData) {
        if (player != null) {
            Body playerBody = player.getBody();
            if (playerBody != null) {
                Vector2 vector2 = Vector2Pool.obtain(pAccelerationData.getX() * 3.0f, pAccelerationData.getY() * 3.0f + 15.0f);
                playerBody.setLinearVelocity(vector2);
                Vector2Pool.recycle(vector2);
            }
        }
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        if (player != null) {
            player.onShoot(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
        }
        return true;
    }

    @Override
    public void beginContact(Contact contact) {
        Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData(),
                entityB = (Entity) contact.getFixtureB().getBody().getUserData();
        if (entityA != null && entityB != null) {
            entityA.onCollide(entityB);
            entityB.onCollide(entityA);
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    public void shake(float shakeTime, float shakeIntensity) {
        this.shakeTime = System.currentTimeMillis() + (int) (shakeTime * 1000);
        this.shakeIntensity = shakeIntensity;
        isShaking = true;
    }

    public void onGameOver() {
        clearUpdateHandlers();

        player = null;
        for (Entity entity : entitySpawner.getEntities()) {
            if (entity instanceof PlayerProjectile || entity instanceof EnemyEntity) {
                if (entity instanceof SquareDiamondEnemy && ((SquareDiamondEnemy) entity).getHealth() > 0) {
                    entity.onDestroy();
                }
                entity.onDestroy();
            }
        }

        final long gameOverTime = System.currentTimeMillis() + 2000;
        gameInterface.showGameOver();
        setOnSceneTouchListener(new IOnSceneTouchListener() {

            @Override
            public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
                if (System.currentTimeMillis() < gameOverTime) {
                    return false;
                }

                gameInterface.hideGameOver();

                for (Entity entity : entitySpawner.getEntities()) {
                    entity.onDestroy();
                }
                gameBackgroundSprite.setPosition(cameraHalfWidth, cameraHalfHeight);
                gameInterface.onStart();
                onStart();
                return true;
            }

        });
    }

    public EntitySpawner getEntitySpawner() {
        return entitySpawner;
    }

    public GameInterface getGameInterface() {
        return gameInterface;
    }

    public PhysicsWorld getPhysicsWorld() {
        return physicsWorld;
    }

    public Sound getSound(String soundName) {
        return sounds.get(soundName);
    }

}
