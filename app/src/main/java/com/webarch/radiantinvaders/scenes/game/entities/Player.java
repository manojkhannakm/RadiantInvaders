package com.webarch.radiantinvaders.scenes.game.entities;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.webarch.radiantinvaders.GameManager;
import com.webarch.radiantinvaders.scenes.game.Entity;
import com.webarch.radiantinvaders.scenes.game.GameInterface;
import com.webarch.radiantinvaders.scenes.game.GameScene;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.modifier.IModifier;

/**
 * @author Manoj Khanna
 */

public class Player extends Entity {

    private int health = 10, level = 1;
    private long shootCoolDownTime = 200, lastShootTime = System.currentTimeMillis();

    public Player(GameManager gameManager, GameScene gameScene, float x, float y, TextureRegion textureRegion) {
        super(gameManager, gameScene, x, y, textureRegion);
    }

    @Override
    public Body onCreateBody() {
        return PhysicsFactory.createCircleBody(gameScene.getPhysicsWorld(), getX(), getY(), getWidth() * 0.4f, BodyDef.BodyType.DynamicBody,
                PhysicsFactory.createFixtureDef(0.0f, 0.0f, 0.0f, false, GameScene.playerCategoryBit, GameScene.playerMaskBit, (short) 0));
    }

    @Override
    public void onCollide(Entity entity) {
        if (entity instanceof EnemyEntity) {
            gameScene.getSound("enemy_clash").play();

            gameScene.shake(0.5f, 0.5f);

            EnemyEntity enemyEntity = (EnemyEntity) entity;
            enemyEntity.setDeathValid(false);
            enemyEntity.onDestroy();

            GameInterface gameInterface = gameScene.getGameInterface();

            if (level > 1) {
                gameScene.getSound("player_level_down").play();

                level--;

                gameInterface.generatePatternLogos(level + 1);
            } else {
                gameInterface.updatePatternLogos(-1);
            }

            health--;
            gameInterface.updateHealthBar(health / 10.0f);
            if (health == 0) {
                onDeath();
            }
        }
    }

    public void onShoot(float x2, float y2) {
        long shootTime = System.currentTimeMillis();
        if (shootTime - lastShootTime < shootCoolDownTime) {
            return;
        }

        gameScene.getSound("player_shoot").play();

        float x1 = getX(), y1 = getY(),
                angleRad = (float) Math.atan2(x2 - x1, y2 - y1),
                angleDeg = (float) Math.toDegrees(angleRad),
                x = x1 + (float) Math.sin(angleRad) * 35.0f, y = y1 + (float) Math.cos(angleRad) * 35.0f;

        setRotation(angleDeg);

        switch (level) {
            case 1:
                gameScene.getEntitySpawner().spawnPlayerProjectile(x, y, angleDeg);
                break;

            case 2:
                gameScene.getEntitySpawner().spawnPlayerProjectile(x + (float) Math.sin(angleRad - 90.0f) * 25.0f, y + (float) Math.cos(angleRad - 90.0f) * 25.0f, angleDeg);
                gameScene.getEntitySpawner().spawnPlayerProjectile(x + (float) Math.sin(angleRad + 90.0f) * 25.0f, y + (float) Math.cos(angleRad + 90.0f) * 25.0f, angleDeg);
                break;

            case 3:
                gameScene.getEntitySpawner().spawnPlayerProjectile(x + (float) Math.sin(angleRad - 45.0f) * 25.0f, y + (float) Math.cos(angleRad - 45.0f) * 25.0f, angleDeg - 45.0f);
                gameScene.getEntitySpawner().spawnPlayerProjectile(x, y, angleDeg);
                gameScene.getEntitySpawner().spawnPlayerProjectile(x + (float) Math.sin(angleRad + 45.0f) * 25.0f, y + (float) Math.cos(angleRad + 45.0f) * 25.0f, angleDeg + 45.0f);
                break;

            case 4:
                gameScene.getEntitySpawner().spawnPlayerProjectile(x + (float) Math.sin(angleRad - 45.0f) * 25.0f, y + (float) Math.cos(angleRad - 45.0f) * 25.0f, angleDeg - 45.0f);
                gameScene.getEntitySpawner().spawnPlayerProjectile(x, y, angleDeg);
                gameScene.getEntitySpawner().spawnPlayerProjectile(x + (float) Math.sin(angleRad + 45.0f) * 25.0f, y + (float) Math.cos(angleRad + 45.0f) * 25.0f, angleDeg + 45.0f);
                gameScene.getEntitySpawner().spawnPlayerProjectile(x - (float) Math.sin(angleRad) * 70.0f, y - (float) Math.cos(angleRad) * 70.0f, angleDeg + 180.0f);
                break;

            case 5:
                gameScene.getEntitySpawner().spawnPlayerProjectile(x + (float) Math.sin(angleRad - 45.0f) * 25.0f, y + (float) Math.cos(angleRad - 45.0f) * 25.0f, angleDeg - 45.0f);
                gameScene.getEntitySpawner().spawnPlayerProjectile(x, y, angleDeg);
                gameScene.getEntitySpawner().spawnPlayerProjectile(x + (float) Math.sin(angleRad + 45.0f) * 25.0f, y + (float) Math.cos(angleRad + 45.0f) * 25.0f, angleDeg + 45.0f);
                float xBack = x - (float) Math.sin(angleRad) * 70.0f, yBack = y - (float) Math.cos(angleRad) * 70.0f, angleDegBack = angleDeg + 180.0f;
                gameScene.getEntitySpawner().spawnPlayerProjectile(xBack - (float) Math.sin(angleRad - 90.0f) * 25.0f, yBack - (float) Math.cos(angleRad - 90.0f) * 25.0f, angleDegBack);
                gameScene.getEntitySpawner().spawnPlayerProjectile(xBack - (float) Math.sin(angleRad + 90.0f) * 25.0f, yBack - (float) Math.cos(angleRad + 90.0f) * 25.0f, angleDegBack);
                break;
        }

        lastShootTime = shootTime;
    }

    public void onDeath() {
        gameScene.getSound("player_death").play();

        gameScene.shake(1.0f, 1.0f);

        super.onDestroyBody();

        registerEntityModifier(new ParallelEntityModifier(new IEntityModifier.IEntityModifierListener() {

            @Override
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
            }

            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                Player.super.onDestroy();
            }
        },
                new AlphaModifier(0.25f, 1.0f, 0.0f),
                new ScaleModifier(0.25f, 1.0f, 0.0f)));

        gameScene.getEntitySpawner().spawnSmallRing(getX(), getY());

        gameScene.onGameOver();
    }

    public void updateLevel() {
        gameScene.getSound("player_level_up").play();

        gameScene.getGameInterface().updateScore(level * 100);

        level++;
    }

    public int getLevel() {
        return level;
    }

    public void setShootCoolDownTime(long shootCoolDownTime) {
        this.shootCoolDownTime = shootCoolDownTime;
    }

}
