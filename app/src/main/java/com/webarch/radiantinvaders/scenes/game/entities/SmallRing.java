package com.webarch.radiantinvaders.scenes.game.entities;

import android.opengl.GLES20;

import com.badlogic.gdx.physics.box2d.Body;
import com.webarch.radiantinvaders.GameManager;
import com.webarch.radiantinvaders.scenes.game.Entity;
import com.webarch.radiantinvaders.scenes.game.GameScene;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.modifier.IModifier;

/**
 * @author Manoj Khanna
 */

public class SmallRing extends Entity {

    public SmallRing(GameManager gameManager, GameScene gameScene, float x, float y, TextureRegion textureRegion) {
        super(gameManager, gameScene, x, y, textureRegion);

        setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public Body onCreateBody() {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

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
                new ScaleModifier(0.25f, 0.0f, 1.0f)));
    }

}
