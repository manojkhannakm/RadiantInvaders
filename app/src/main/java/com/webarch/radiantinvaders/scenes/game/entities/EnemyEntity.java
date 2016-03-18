package com.webarch.radiantinvaders.scenes.game.entities;

import android.opengl.GLES20;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.webarch.radiantinvaders.GameManager;
import com.webarch.radiantinvaders.scenes.game.Entity;
import com.webarch.radiantinvaders.scenes.game.EntitySpawner;
import com.webarch.radiantinvaders.scenes.game.GameInterface;
import com.webarch.radiantinvaders.scenes.game.GameScene;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.math.MathUtils;
import org.andengine.util.modifier.IModifier;

/**
 * @author Manoj Khanna
 */

public abstract class EnemyEntity extends Entity {

    protected static FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(0.0f, 0.0f, 0.0f, false, GameScene.otherCategoryBit, GameScene.otherMaskBit, (short) 0);
    private float speed;
    private int score;
    private Player player;
    private boolean isDeathValid = true;

    public EnemyEntity(GameManager gameManager, GameScene gameScene, float x, float y, TextureRegion textureRegion, float speed, int score) {
        super(gameManager, gameScene, x, y, textureRegion);

        this.speed = speed;
        this.score = score;

        player = gameScene.getEntitySpawner().getPlayer();

        setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public abstract Body onCreateBody();

    @Override
    public void onCreate() {
        setLockUpdate(true);

        super.onCreate();

        gameScene.getSound("enemy_spawn").play();

        gameScene.getEntitySpawner().spawnSmallRing(getX(), getY());

        registerEntityModifier(new ParallelEntityModifier(new IEntityModifier.IEntityModifierListener() {

            @Override
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
            }

            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                setLockUpdate(false);
            }
        },
                new AlphaModifier(0.25f, 0.0f, 1.0f),
                new ScaleModifier(0.25f, 0.0f, 1.0f)));
    }

    @Override
    public void onUpdate() {
        float angleRad = (float) Math.atan2(player.getX() - getX(), player.getY() - getY()),
                angleDeg = (float) Math.toDegrees(angleRad);

        setRotation(angleDeg);

        Body body = getBody();

        body.setTransform(body.getPosition(), -angleRad);

        Vector2 vector2 = Vector2Pool.obtain((float) Math.sin(angleRad) * speed, (float) Math.cos(angleRad) * speed);
        body.setLinearVelocity(vector2);
        Vector2Pool.recycle(vector2);
    }

    @Override
    public void onDestroy() {
        clearUpdateHandlers();

        super.onDestroyBody();

        registerEntityModifier(new ParallelEntityModifier(new IEntityModifier.IEntityModifierListener() {

            @Override
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
            }

            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                EnemyEntity.super.onDestroy();
            }
        },
                new AlphaModifier(0.25f, 1.0f, 0.0f),
                new ScaleModifier(0.25f, 1.0f, 0.0f)));

        final float x = getX(), y = getY();

        EntitySpawner entitySpawner = gameScene.getEntitySpawner();

        entitySpawner.spawnSmallRing(x, y);
        if (isDeathValid) {
            for (int i = 0, n = MathUtils.random(3, 5); i < n; i++) {
                entitySpawner.spawnStone(x, y, MathUtils.random(5));
            }

            GameInterface gameInterface = gameScene.getGameInterface();

            if (MathUtils.random(0.0f, 1.0f) > 0.5f) {
                int curPatternLogoId = gameInterface.getCurPatternLogoId(),
                        minPatternLogoId = curPatternLogoId - 2, maxPatternLogoId = curPatternLogoId + 2;
                entitySpawner.spawnLogo(x, y, MathUtils.random(minPatternLogoId < 0 ? 0 : minPatternLogoId, maxPatternLogoId > 9 ? 9 : maxPatternLogoId));
            }

            gameInterface.updateScore(score);
        }
    }

    public void setDeathValid(boolean deathValid) {
        this.isDeathValid = deathValid;
    }

}
