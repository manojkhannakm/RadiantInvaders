package com.webarch.radiantinvaders;

import org.andengine.entity.scene.Scene;

/**
 * @author Manoj Khanna
 */

public abstract class BaseScene extends Scene {

    protected GameManager gameManager;
    protected Game game;
    protected float cameraWidth, cameraHeight, cameraHalfWidth, cameraHalfHeight;
    private boolean isBackKeyEnabled = true;

    public abstract void onCreate();

    public abstract void onStart();

    public abstract void onDestroy();

    public abstract void onBackPressed();

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;

        game = gameManager.getGame();
        cameraWidth = gameManager.getCameraWidth();
        cameraHeight = gameManager.getCameraHeight();
        cameraHalfWidth = gameManager.getCameraHalfWidth();
        cameraHalfHeight = gameManager.getCameraHalfHeight();
    }

    public boolean isBackKeyEnabled() {
        return isBackKeyEnabled;
    }

    public void setBackKeyEnabled(boolean backKeyEnabled) {
        isBackKeyEnabled = backKeyEnabled;
    }

}
