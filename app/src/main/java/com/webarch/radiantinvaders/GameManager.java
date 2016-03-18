package com.webarch.radiantinvaders;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Manoj Khanna
 */

public class GameManager {

    private Game game;
    private Camera camera;
    private float cameraWidth, cameraHeight, cameraHalfWidth, cameraHalfHeight;
    private HashMap<String, TextureRegion> globalTextureRegions = new HashMap<>();
    private Music music;

    public GameManager(Game game) {
        this.game = game;

        camera = game.getEngine().getCamera();
        cameraWidth = camera.getWidth();
        cameraHeight = camera.getHeight();
        cameraHalfWidth = cameraWidth * 0.5f;
        cameraHalfHeight = cameraHeight * 0.5f;

        BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(game.getTextureManager(), 800, 480, TextureOptions.BILINEAR);
        globalTextureRegions.put("main_background", BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, game, "main_background.png", 0, 0));
        bitmapTextureAtlas.load();

        try {
            music = MusicFactory.createMusicFromAsset(game.getMusicManager(), game, "music.ogg");
            music.setVolume(0.1f);
            music.setLooping(true);
        } catch (IOException ignored) {
        }
    }

    public BaseScene getScene() {
        return (BaseScene) game.getEngine().getScene();
    }

    public void setScene(final BaseScene baseScene) {
        game.runOnUpdateThread(new Runnable() {

            @Override
            public void run() {
                final BaseScene preScene = getScene();

                baseScene.setGameManager(GameManager.this);
                baseScene.onCreate();
                game.getEngine().setScene(baseScene);

                preScene.onDestroy();
                preScene.detachSelf();
                preScene.dispose();
                System.gc();

                baseScene.onStart();
            }

        });
    }

    public Game getGame() {
        return game;
    }

    public Camera getCamera() {
        return camera;
    }

    public float getCameraWidth() {
        return cameraWidth;
    }

    public float getCameraHeight() {
        return cameraHeight;
    }

    public float getCameraHalfWidth() {
        return cameraHalfWidth;
    }

    public float getCameraHalfHeight() {
        return cameraHalfHeight;
    }

    public TextureRegion getGlobalTextureRegion(String textureName) {
        return globalTextureRegions.get(textureName);
    }

    public Music getMusic() {
        return music;
    }

}
