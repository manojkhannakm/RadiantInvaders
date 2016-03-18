package com.webarch.radiantinvaders.scenes.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.webarch.radiantinvaders.GameManager;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.TextureRegion;

/**
 * @author Manoj Khanna
 */

public abstract class Entity extends Sprite {

    protected GameManager gameManager;
    protected GameScene gameScene;
    private Body body;
    private PhysicsConnector physicsConnector;
    private boolean lockUpdate = false;

    public Entity(GameManager gameManager, GameScene gameScene, float x, float y, TextureRegion textureRegion) {
        super(x, y, textureRegion, gameManager.getGame().getVertexBufferObjectManager());

        this.gameManager = gameManager;
        this.gameScene = gameScene;

        PhysicsWorld physicsWorld = gameScene.getPhysicsWorld();
        body = onCreateBody();
        if (body != null) {
            body.setUserData(this);
            physicsConnector = new PhysicsConnector(this, body, true, false);
            physicsWorld.registerPhysicsConnector(physicsConnector);
        }
    }

    public abstract Body onCreateBody();

    public void onCreate() {
        registerUpdateHandler(new IUpdateHandler() {

            @Override
            public void onUpdate(float pSecondsElapsed) {
                if (!lockUpdate) {
                    Entity.this.onUpdate();
                }
            }

            @Override
            public void reset() {
            }

        });
    }

    public void onUpdate() {
    }

    public void onCollide(Entity entity) {
    }

    public void onDestroyBody() {
        if (body != null) {
            final Body body = this.body;
            gameManager.getGame().runOnUpdateThread(new Runnable() {

                @Override
                public void run() {
                    PhysicsWorld physicsWorld = gameScene.getPhysicsWorld();
                    physicsWorld.unregisterPhysicsConnector(physicsConnector);
                    physicsWorld.destroyBody(body);
                }

            });
            this.body = null;
        }
    }

    public void onDestroy() {
        onDestroyBody();
        gameManager.getGame().runOnUpdateThread(new Runnable() {

            @Override
            public void run() {
                gameScene.getEntitySpawner().destroy(Entity.this);
            }

        });
    }

    public void setLockUpdate(boolean lockUpdate) {
        this.lockUpdate = lockUpdate;
    }

    public Body getBody() {
        return body;
    }

}
