package com.webarch.radiantinvaders.scenes.loading;

import com.webarch.radiantinvaders.BaseScene;
import com.webarch.radiantinvaders.scenes.game.GameScene;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.math.MathUtils;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBackOut;

/**
 * @author Manoj Khanna
 */

public class LoadingScene extends BaseScene {

    private Sprite mainBackgroundSprite, loadingSprite;
    private BitmapTextureAtlas bitmapTextureAtlas;
    private AnimatedSprite loadingBarSprite;

    @Override
    public void onCreate() {
        VertexBufferObjectManager vertexBufferObjectManager = game.getVertexBufferObjectManager();

        mainBackgroundSprite = new Sprite(cameraHalfWidth, cameraHalfHeight, gameManager.getGlobalTextureRegion("main_background"), vertexBufferObjectManager);

        bitmapTextureAtlas = new BitmapTextureAtlas(game.getTextureManager(), 410, 580, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        loadingSprite = new Sprite(cameraHalfWidth, cameraHeight * 0.23f, BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, game, "loading.png", 0, 0), vertexBufferObjectManager);
        loadingBarSprite = new AnimatedSprite(cameraHalfWidth, cameraHeight * 0.1f, BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, game, "loading_bar.png", 0, 161, 1, 10), vertexBufferObjectManager);
        bitmapTextureAtlas.load();

        loadingSprite.setScale(0.0f);
        loadingBarSprite.setScale(0.0f);
        loadingBarSprite.animate(50);

        setBackground(new SpriteBackground(mainBackgroundSprite));
        attachChild(loadingSprite);
        attachChild(loadingBarSprite);
    }

    @Override
    public void onStart() {
        loadingSprite.registerEntityModifier(new ScaleModifier(0.5f, 0.0f, 1.0f, EaseBackOut.getInstance()));
        loadingBarSprite.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(0.2f),
                new ScaleModifier(0.5f, 0.0f, 1.0f, EaseBackOut.getInstance()),
                new DelayModifier(MathUtils.random(0.5f, 1.0f),
                        new IEntityModifier.IEntityModifierListener() {

                            @Override
                            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                            }

                            @Override
                            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                                gameManager.setScene(new GameScene());
                            }

                        })));
    }

    @Override
    public void onDestroy() {
        loadingBarSprite.detachSelf();
        loadingSprite.detachSelf();

        loadingBarSprite.dispose();
        loadingSprite.dispose();
        bitmapTextureAtlas.unload();
        mainBackgroundSprite.dispose();

        loadingBarSprite = null;
        loadingSprite = null;
        bitmapTextureAtlas = null;
        mainBackgroundSprite = null;
    }

    @Override
    public void onBackPressed() {
        game.finish();
    }

}
