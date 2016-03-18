package com.webarch.radiantinvaders;

import com.webarch.radiantinvaders.scenes.game.GameScene;
import com.webarch.radiantinvaders.scenes.splash.SplashScene;

import org.andengine.audio.music.Music;
import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.ui.activity.BaseGameActivity;

import java.io.IOException;

/**
 * @author Manoj Khanna
 */

public class Game extends BaseGameActivity {

    private GameManager gameManager;

    @Override
    public Engine onCreateEngine(EngineOptions pEngineOptions) {
        return new LimitedFPSEngine(pEngineOptions, 60);
    }

    @Override
    public synchronized void onResumeGame() {
        super.onResumeGame();

        BaseScene baseScene = gameManager.getScene();
        if (baseScene instanceof GameScene) {
            enableAccelerationSensor((GameScene) baseScene);
        }
        if (!(baseScene instanceof SplashScene) && getSharedPreferences("RadiantInvaders", MODE_PRIVATE).getBoolean("isMusicEnabled", true)) {
            gameManager.getMusic().resume();
        }
    }

    @Override
    public synchronized void onPauseGame() {
        disableAccelerationSensor();
        Music music = gameManager.getMusic();
        if (music.isPlaying()) {
            music.pause();
        }

        super.onPauseGame();
    }

    @Override
    public boolean enableAccelerationSensor(IAccelerationListener pAccelerationListener) {
        return super.enableAccelerationSensor(pAccelerationListener);
    }

    @Override
    public boolean disableAccelerationSensor() {
        return super.disableAccelerationSensor();
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), new Camera(0, 0, 800, 480))
                .setWakeLockOptions(WakeLockOptions.SCREEN_ON);
        engineOptions.getRenderOptions().setDithering(true);
        engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
        return engineOptions;
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {
        gameManager = new GameManager(this);
        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {
        SplashScene splashScene = new SplashScene();
        splashScene.setGameManager(gameManager);
        splashScene.onCreate();
        pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {
        pOnPopulateSceneCallback.onPopulateSceneFinished();
        ((BaseScene) pScene).onStart();
    }

    @Override
    public void onBackPressed() {
        BaseScene baseScene = gameManager.getScene();
        if (baseScene.isBackKeyEnabled()) {
            baseScene.onBackPressed();
            baseScene.setBackKeyEnabled(false);
        }
    }

}
