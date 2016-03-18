package com.webarch.radiantinvaders.scenes.game.entities;

import android.opengl.GLES20;

import com.badlogic.gdx.physics.box2d.Body;
import com.webarch.radiantinvaders.GameManager;
import com.webarch.radiantinvaders.scenes.game.Entity;
import com.webarch.radiantinvaders.scenes.game.GameScene;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.math.MathUtils;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseQuintOut;

/**
 * @author Manoj Khanna
 */

public class Stone extends Entity {

    private int colorId;
    private Player player;

    public Stone(GameManager gameManager, GameScene gameScene, float x, float y, int colorId, TextureRegion textureRegion) {
        super(gameManager, gameScene, x, y, textureRegion);

        this.colorId = colorId;

        player = gameScene.getEntitySpawner().getPlayer();

        setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public Body onCreateBody() {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        float x1 = getX(), y1 = getY(), x2 = MathUtils.random(x1 - 100, x1 + 100), y2 = MathUtils.random(y1 - 100, y1 + 100);
        registerEntityModifier(new MoveModifier(MathUtils.distance(x1, y1, x2, y2) * 0.005f, x1, y1, x2, y2, EaseQuintOut.getInstance()));

        registerUpdateHandler(new TimerHandler(MathUtils.random(3.0f, 5.0f), new ITimerCallback() {

            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                setLockUpdate(true);

                registerEntityModifier(new ParallelEntityModifier(new IEntityModifier.IEntityModifierListener() {

                    @Override
                    public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                    }

                    @Override
                    public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                        onDestroy();
                    }
                },
                        new AlphaModifier(0.25f, 1.0f, 0.0f),
                        new ScaleModifier(0.25f, 1.0f, 0.0f)));

                gameScene.getEntitySpawner().spawnStoneRing(getX(), getY(), colorId);
            }

        }));
    }

    @Override
    public void onUpdate() {
        float x1 = getX(), y1 = getY(), x2 = player.getX(), y2 = player.getY();
        if (MathUtils.distance(x1, y1, x2, y2) <= 100.0f) {
            clearEntityModifiers();
            clearUpdateHandlers();

            gameScene.getSound("stone_" + MathUtils.random(1, 7)).play();

            registerEntityModifier(new ParallelEntityModifier(new IEntityModifier.IEntityModifierListener() {

                @Override
                public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                }

                @Override
                public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                    onDestroy();
                }

            },
                    new AlphaModifier(0.5f, 1.0f, 0.0f),
                    new MoveModifier(0.5f, x1, y1, x2, y2, EaseQuintOut.getInstance())));

            gameScene.getGameInterface().updateStoneCount();
        }
    }

}
