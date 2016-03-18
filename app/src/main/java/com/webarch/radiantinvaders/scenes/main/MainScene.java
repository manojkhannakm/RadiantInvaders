package com.webarch.radiantinvaders.scenes.main;

import android.content.Context;
import android.content.SharedPreferences;

import com.webarch.radiantinvaders.BaseScene;
import com.webarch.radiantinvaders.scenes.loading.LoadingScene;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBackIn;
import org.andengine.util.modifier.ease.EaseBackOut;

import java.io.IOException;

/**
 * @author Manoj Khanna
 */

public class MainScene extends BaseScene {

    private Sprite mainBackgroundSprite;
    private BitmapTextureAtlas bitmapTextureAtlas;
    private ButtonSprite playButtonSprite, highscoresButtonSprite, settingsButtonSprite, musicButtonSprite, soundButtonSprite;
    private Sound buttonSound;
    private SharedPreferences preferences;

    @Override
    public void onCreate() {
        VertexBufferObjectManager vertexBufferObjectManager = game.getVertexBufferObjectManager();

        mainBackgroundSprite = new Sprite(cameraHalfWidth, cameraHalfHeight, gameManager.getGlobalTextureRegion("main_background"), vertexBufferObjectManager);

        bitmapTextureAtlas = new BitmapTextureAtlas(game.getTextureManager(), 620, 130, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        playButtonSprite = new ButtonSprite(cameraHalfWidth, cameraHeight * 0.55f, BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, game, "play.png", 0, 0, 1, 2), vertexBufferObjectManager, new ButtonSprite.OnClickListener() {

            @Override
            public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                buttonSound.play();

                clearTouchAreas();

                playButtonSprite.setCurrentTileIndex(1);

                playButtonSprite.registerEntityModifier(new ScaleModifier(0.5f, 1.0f, 0.0f, EaseBackIn.getInstance()));

                highscoresButtonSprite.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(0.2f),
                        new ScaleModifier(0.5f, 1.0f, 0.0f, EaseBackIn.getInstance())));

                settingsButtonSprite.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(0.4f),
                        new ScaleModifier(0.5f, 1.0f, 0.0f, EaseBackIn.getInstance())));

                musicButtonSprite.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(0.6f),
                        new ScaleModifier(0.5f, 1.0f, 0.0f, EaseBackIn.getInstance())));

                soundButtonSprite.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(0.6f),
                        new ScaleModifier(0.5f, 1.0f, 0.0f, new IEntityModifier.IEntityModifierListener() {

                            @Override
                            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                            }

                            @Override
                            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                                gameManager.setScene(new LoadingScene());
                            }

                        }, EaseBackIn.getInstance())));
            }

        });

        highscoresButtonSprite = new ButtonSprite(cameraHalfWidth, cameraHeight * 0.38f, BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, game, "highscores.png", 139, 0, 1, 2), vertexBufferObjectManager, new ButtonSprite.OnClickListener() {

            @Override
            public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                buttonSound.play();
            }

        });

        settingsButtonSprite = new ButtonSprite(cameraHalfWidth, cameraHeight * 0.21f, BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, game, "settings.png", 337, 0, 1, 2), vertexBufferObjectManager, new ButtonSprite.OnClickListener() {

            @Override
            public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                buttonSound.play();
            }

        });

        musicButtonSprite = new ButtonSprite(cameraWidth * 0.8f, cameraHeight * 0.15f, BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, game, "music.png", 492, 0, 1, 2), vertexBufferObjectManager, new ButtonSprite.OnClickListener() {

            @Override
            public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                buttonSound.play();

                boolean isMusicEnabled = preferences.getBoolean("isMusicEnabled", true);
                if (isMusicEnabled) {
                    musicButtonSprite.setCurrentTileIndex(0);

                    gameManager.getMusic().pause();
                } else {
                    musicButtonSprite.setCurrentTileIndex(1);

                    gameManager.getMusic().resume();
                }
                preferences.edit().putBoolean("isMusicEnabled", !isMusicEnabled).apply();
            }

        });

        soundButtonSprite = new ButtonSprite(cameraWidth * 0.9f, cameraHeight * 0.15f, BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, game, "sound.png", 557, 0, 1, 2), vertexBufferObjectManager, new ButtonSprite.OnClickListener() {

            @Override
            public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                buttonSound.play();

                boolean isSoundEnabled = preferences.getBoolean("isSoundEnabled", true);
                if (isSoundEnabled) {
                    soundButtonSprite.setCurrentTileIndex(0);

                    game.getSoundManager().setMasterVolume(0.0f);
                } else {
                    soundButtonSprite.setCurrentTileIndex(1);

                    game.getSoundManager().setMasterVolume(1.0f);
                }
                preferences.edit().putBoolean("isSoundEnabled", !isSoundEnabled).apply();
            }

        });

        bitmapTextureAtlas.load();

        try {
            buttonSound = SoundFactory.createSoundFromAsset(game.getSoundManager(), game, "enemy_death.ogg");
            buttonSound.setVolume(0.1f);
        } catch (IOException ignored) {
        }

        preferences = game.getSharedPreferences("RadiantInvaders", Context.MODE_PRIVATE);
        if (preferences.getBoolean("isMusicEnabled", true)) {
            musicButtonSprite.setCurrentTileIndex(1);
            gameManager.getMusic().play();
        }
        if (preferences.getBoolean("isSoundEnabled", true)) {
            soundButtonSprite.setCurrentTileIndex(1);
            game.getSoundManager().setMasterVolume(1.0f);
        } else {
            game.getSoundManager().setMasterVolume(0.0f);
        }

        playButtonSprite.setScale(0.0f);
        highscoresButtonSprite.setScale(0.0f);
        settingsButtonSprite.setScale(0.0f);
        musicButtonSprite.setScale(0.0f);
        soundButtonSprite.setScale(0.0f);

        setBackground(new SpriteBackground(mainBackgroundSprite));
        attachChild(playButtonSprite);
        attachChild(highscoresButtonSprite);
        attachChild(settingsButtonSprite);
        attachChild(musicButtonSprite);
        attachChild(soundButtonSprite);
    }

    @Override
    public void onStart() {
        playButtonSprite.registerEntityModifier(new ScaleModifier(0.5f, 0.0f, 1.0f, EaseBackOut.getInstance()));

        highscoresButtonSprite.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(0.2f),
                new ScaleModifier(0.5f, 0.0f, 1.0f, EaseBackOut.getInstance())));

        settingsButtonSprite.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(0.4f),
                new ScaleModifier(0.5f, 0.0f, 1.0f, EaseBackOut.getInstance())));

        musicButtonSprite.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(0.6f),
                new ScaleModifier(0.5f, 0.0f, 1.0f, EaseBackOut.getInstance())));

        soundButtonSprite.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(0.6f),
                new ScaleModifier(0.5f, 0.0f, 1.0f, new IEntityModifier.IEntityModifierListener() {

                    @Override
                    public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                    }

                    @Override
                    public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                        registerTouchArea(playButtonSprite);
                        registerTouchArea(highscoresButtonSprite);
                        registerTouchArea(settingsButtonSprite);
                        registerTouchArea(musicButtonSprite);
                        registerTouchArea(soundButtonSprite);
                        setTouchAreaBindingOnActionDownEnabled(true);
                    }

                }, EaseBackOut.getInstance())));
    }

    @Override
    public void onDestroy() {
        settingsButtonSprite.detachSelf();
        highscoresButtonSprite.detachSelf();
        playButtonSprite.detachSelf();
        musicButtonSprite.detachSelf();
        soundButtonSprite.detachSelf();

        settingsButtonSprite.dispose();
        highscoresButtonSprite.dispose();
        playButtonSprite.dispose();
        musicButtonSprite.dispose();
        soundButtonSprite.dispose();
        bitmapTextureAtlas.unload();
        mainBackgroundSprite.dispose();

        settingsButtonSprite = null;
        highscoresButtonSprite = null;
        playButtonSprite = null;
        musicButtonSprite = null;
        soundButtonSprite = null;
        bitmapTextureAtlas = null;
        mainBackgroundSprite = null;
    }

    @Override
    public void onBackPressed() {
        game.finish();
    }

}
