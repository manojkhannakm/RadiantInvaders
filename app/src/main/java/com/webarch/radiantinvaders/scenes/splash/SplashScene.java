package com.webarch.radiantinvaders.scenes.splash;

import android.opengl.GLES20;

import com.webarch.radiantinvaders.BaseScene;
import com.webarch.radiantinvaders.scenes.main.MainScene;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.util.modifier.IModifier;

/**
 * @author Manoj Khanna
 */

public class SplashScene extends BaseScene {

    private BitmapTextureAtlas bitmapTextureAtlas;
    private Sprite splashBackgroundSprite;

    @Override
    public void onCreate() {
        bitmapTextureAtlas = new BitmapTextureAtlas(game.getTextureManager(), 800, 480, TextureOptions.BILINEAR);
        splashBackgroundSprite = new Sprite(cameraHalfWidth, cameraHalfHeight, BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, game, "splash_background.png", 0, 0), game.getVertexBufferObjectManager());
        bitmapTextureAtlas.load();

        splashBackgroundSprite.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        attachChild(splashBackgroundSprite);
    }

    @Override
    public void onStart() {
        splashBackgroundSprite.registerEntityModifier(new SequenceEntityModifier(new IEntityModifier.IEntityModifierListener() {

            @Override
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
            }

            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                gameManager.setScene(new MainScene());
            }

        },
                new AlphaModifier(0.25f, 0.0f, 1.0f),
                new DelayModifier(2.5f),
                new AlphaModifier(0.25f, 1.0f, 0.0f)));
    }

    @Override
    public void onDestroy() {
        splashBackgroundSprite.detachSelf();

        splashBackgroundSprite.dispose();
        bitmapTextureAtlas.unload();

        bitmapTextureAtlas = null;
    }

    @Override
    public void onBackPressed() {
        game.finish();
    }

}
