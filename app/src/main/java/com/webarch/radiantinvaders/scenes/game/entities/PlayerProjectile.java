package com.webarch.radiantinvaders.scenes.game.entities;

import android.graphics.Rect;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.webarch.radiantinvaders.GameManager;
import com.webarch.radiantinvaders.scenes.game.Entity;
import com.webarch.radiantinvaders.scenes.game.GameScene;

import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.texture.region.TextureRegion;

/**
 * @author Manoj Khanna
 */

public class PlayerProjectile extends Entity {

    private float angleDeg;
    private Rect gameScreenBounds;

    public PlayerProjectile(GameManager gameManager, GameScene gameScene, float x, float y, float angleDeg, TextureRegion textureRegion) {
        super(gameManager, gameScene, x, y, textureRegion);

        this.angleDeg = angleDeg;

        float cameraWidth = gameManager.getCameraWidth(), cameraHeight = gameManager.getCameraHeight();
        gameScreenBounds = new Rect((int) -cameraWidth * 2, (int) -cameraHeight * 2, (int) cameraWidth * 2, (int) cameraHeight * 2);

        setRotation(angleDeg);
    }

    @Override
    public Body onCreateBody() {
        return PhysicsFactory.createCircleBody(gameScene.getPhysicsWorld(), getX(), getY(), getWidth() * 0.4f, BodyDef.BodyType.KinematicBody,
                PhysicsFactory.createFixtureDef(0.0f, 0.0f, 0.0f, false, GameScene.otherCategoryBit, GameScene.otherMaskBit, (short) 0));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        float angleRad = (float) Math.toRadians(angleDeg);
        Vector2 vector2 = Vector2Pool.obtain((float) Math.sin(angleRad) * 25.0f, (float) Math.cos(angleRad) * 25.0f);
        getBody().setLinearVelocity(vector2);
        Vector2Pool.recycle(vector2);
    }

    @Override
    public void onUpdate() {
        if (!gameScreenBounds.contains((int) getX(), (int) getY())) {
            onDestroy();
        }
    }

    @Override
    public void onCollide(Entity entity) {
        if (entity instanceof EnemyEntity) {
            gameScene.getSound("enemy_death").play();

            entity.onDestroy();

            onDestroy();
        }
    }

}
