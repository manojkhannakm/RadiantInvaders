package com.webarch.radiantinvaders.scenes.game;

import android.opengl.GLES20;

import com.webarch.radiantinvaders.Game;
import com.webarch.radiantinvaders.GameManager;
import com.webarch.radiantinvaders.scenes.game.entities.Player;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;
import org.andengine.util.math.MathUtils;

/**
 * @author Manoj Khanna
 */

public class GameInterface extends HUD {

    private GameManager gameManager;
    private GameScene gameScene;
    private Font font;
    private Text stoneCountText, scoreText, gameOverText;
    private Rectangle healthBarRectangle, gameOverRectangle;
    private Line[] healthBarLines = new Line[4];
    private int score = 0, stoneCount = 0;
    private int[] patternLogoIds;
    private Sprite[] patternLogoSprites, patternWhiteLogoSprites;
    private boolean[] isPatternLogoEnabled;
    private int curPatternLogoIndex;

    public GameInterface(GameManager gameManager, GameScene gameScene) {
        this.gameManager = gameManager;
        this.gameScene = gameScene;

        setCamera(gameManager.getCamera());

        Game game = gameManager.getGame();
        font = FontFactory.createFromAsset(game.getFontManager(), game.getTextureManager(), 256, 256, TextureOptions.BILINEAR, game.getAssets(), "halo.ttf", 20, true, android.graphics.Color.WHITE);
        font.load();

        float cameraWidth = gameManager.getCameraWidth(), cameraHeight = gameManager.getCameraHeight();
        VertexBufferObjectManager vertexBufferObjectManager = game.getVertexBufferObjectManager();

        stoneCountText = new Text(cameraWidth * 0.13f, cameraHeight * 0.95f, font, "Stones: " + stoneCount, 25, vertexBufferObjectManager);
        attachChild(stoneCountText);

        final float x = cameraWidth * 0.5f, y = cameraHeight * 0.95f, width = 200, height = 12;

        healthBarRectangle = new Rectangle(x, y, width, height, vertexBufferObjectManager);
        healthBarRectangle.setScaleCenterX(0);
        healthBarRectangle.setColor(Color.WHITE);
        attachChild(healthBarRectangle);

        final float halfWidth = width * 0.5f, halfHeight = height * 0.5f;
        healthBarLines[0] = new Line(x - halfWidth, y - halfHeight, x - halfWidth, y + halfHeight, 2.0f, vertexBufferObjectManager);
        healthBarLines[1] = new Line(x - halfWidth, y + halfHeight, x + halfWidth, y + halfHeight, 2.0f, vertexBufferObjectManager);
        healthBarLines[2] = new Line(x + halfWidth, y + halfHeight, x + halfWidth, y - halfHeight, 2.0f, vertexBufferObjectManager);
        healthBarLines[3] = new Line(x + halfWidth, y - halfHeight, x - halfWidth, y - halfHeight, 2.0f, vertexBufferObjectManager);
        for (Line line : healthBarLines) {
            line.setColor(Color.WHITE);
            attachChild(line);
        }

        scoreText = new Text(cameraWidth * 0.8f, cameraHeight * 0.95f, font, "Score: " + score, 25, vertexBufferObjectManager);
        attachChild(scoreText);

        onStart();
    }

    @Override
    public boolean detachSelf() {
        for (Sprite patternWhiteLogoSprite : patternWhiteLogoSprites) {
            patternWhiteLogoSprite.detachSelf();
        }
        for (Sprite patternLogoSprite : patternLogoSprites) {
            patternLogoSprite.detachSelf();
        }
        scoreText.detachSelf();
        for (Line healthBarLine : healthBarLines) {
            healthBarLine.detachSelf();
        }
        healthBarRectangle.detachSelf();
        stoneCountText.detachSelf();

        return super.detachSelf();
    }

    @Override
    public void dispose() {
        for (Sprite patternWhiteLogoSprite : patternWhiteLogoSprites) {
            patternWhiteLogoSprite.dispose();
        }
        for (Sprite patternLogoSprite : patternLogoSprites) {
            patternLogoSprite.dispose();
        }
        scoreText.dispose();
        for (Line healthBarLine : healthBarLines) {
            healthBarLine.dispose();
        }
        healthBarRectangle.dispose();
        stoneCountText.dispose();
        font.unload();

        super.dispose();
    }

    public void onStart() {
        stoneCount = -1;
        updateStoneCount();

        healthBarRectangle.setColor(Color.WHITE);
        updateHealthBar(1.0f);

        score = 0;
        updateScore(0);

        generatePatternLogos(2);
    }

    public void updateStoneCount() {
        stoneCount++;
        stoneCountText.setText("Stones: " + stoneCount);
    }

    public void updateHealthBar(float healthPercent) {
        healthBarRectangle.setScaleX(healthPercent);
        if (healthPercent <= 0.3f) {
            healthBarRectangle.setColor(Color.RED);
        }
    }

    public void updateScore(int score) {
        this.score += score;
        scoreText.setText("Score: " + this.score);
    }

    public void generatePatternLogos(int logoCount) {
        if (patternLogoIds != null) {
            for (Sprite patternLogoSprite : patternLogoSprites) {
                patternLogoSprite.detachSelf();
                patternLogoSprite.dispose();
            }
            for (Sprite patternWhiteLogoSprite : patternWhiteLogoSprites) {
                patternWhiteLogoSprite.detachSelf();
                patternWhiteLogoSprite.dispose();
            }
        }

        patternLogoIds = new int[logoCount];
        patternLogoSprites = new Sprite[logoCount];
        patternWhiteLogoSprites = new Sprite[logoCount];
        isPatternLogoEnabled = new boolean[logoCount];
        curPatternLogoIndex = 0;

        final float cameraHalfHeight = gameManager.getCameraHalfHeight(), logoDimen = 50.0f, logoHalfDimen = logoDimen * 0.5f,
                xOffset = logoHalfDimen * 1.5f, yOffset = MathUtils.isEven(logoCount) ? (cameraHalfHeight + logoHalfDimen + (logoCount / 2 - 1) * logoDimen) : (cameraHalfHeight + logoCount / 2 * logoDimen);
        EntitySpawner entitySpawner = gameScene.getEntitySpawner();
        VertexBufferObjectManager vertexBufferObjectManager = gameManager.getGame().getVertexBufferObjectManager();
        for (int i = 0; i < logoCount; i++) {
            int logoId = MathUtils.random(10);

            patternLogoIds[i] = logoId;

            float y = yOffset - i * logoDimen;

            patternLogoSprites[i] = new Sprite(xOffset, y, entitySpawner.getLogoTextureRegion(logoId), vertexBufferObjectManager);
            patternLogoSprites[i].setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            attachChild(patternLogoSprites[i]);

            patternWhiteLogoSprites[i] = new Sprite(xOffset, y, entitySpawner.getWhiteLogoTextureRegion(logoId), vertexBufferObjectManager);
            patternWhiteLogoSprites[i].setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            attachChild(patternWhiteLogoSprites[i]);

            isPatternLogoEnabled[i] = true;

            setLogoEnabled(i, false);
        }
    }

    public void updatePatternLogos(int logoId) {
        if (logoId == patternLogoIds[curPatternLogoIndex]) {
            setLogoEnabled(curPatternLogoIndex, true);
            if (curPatternLogoIndex == patternLogoIds.length - 1) {
                Player player = gameScene.getEntitySpawner().getPlayer();
                if (player.getLevel() < 5) {
                    player.updateLevel();
                    generatePatternLogos(player.getLevel() + 1);
                }
                return;
            }
            curPatternLogoIndex++;
        } else {
            for (int i = 0; i < patternLogoIds.length; i++) {
                setLogoEnabled(i, false);
            }
            curPatternLogoIndex = 0;
        }
    }

    private void setLogoEnabled(int logoIndex, boolean enable) {
        if (isPatternLogoEnabled[logoIndex] == enable) {
            return;
        }

        if (enable) {
            isPatternLogoEnabled[logoIndex] = true;

            patternWhiteLogoSprites[logoIndex].clearEntityModifiers();
            patternWhiteLogoSprites[logoIndex].registerEntityModifier(new AlphaModifier(0.25f, 1.0f, 0.0f));

            patternLogoSprites[logoIndex].clearEntityModifiers();
            patternLogoSprites[logoIndex].registerEntityModifier(new AlphaModifier(0.25f, 0.0f, 1.0f));
        } else {
            isPatternLogoEnabled[logoIndex] = false;

            patternLogoSprites[logoIndex].clearEntityModifiers();
            patternLogoSprites[logoIndex].registerEntityModifier(new AlphaModifier(0.25f, 1.0f, 0.0f));

            patternWhiteLogoSprites[logoIndex].clearEntityModifiers();
            patternWhiteLogoSprites[logoIndex].registerEntityModifier(new AlphaModifier(0.25f, 0.0f, 1.0f));
        }
    }

    public void showGameOver() {
        float cameraHalfWidth = gameManager.getCameraHalfWidth(), cameraHalfHeight = gameManager.getCameraHalfHeight();
        VertexBufferObjectManager vertexBufferObjectManager = gameManager.getGame().getVertexBufferObjectManager();

        gameOverRectangle = new Rectangle(cameraHalfWidth, cameraHalfHeight, gameManager.getCameraWidth(), gameManager.getCameraHeight(), vertexBufferObjectManager);
        gameOverRectangle.setColor(0.0f, 0.0f, 0.0f, 0.5f);
        attachChild(gameOverRectangle);

        gameOverText = new Text(cameraHalfWidth, cameraHalfHeight, font, "Game over!", 25, vertexBufferObjectManager);
        gameOverRectangle.attachChild(gameOverText);
    }

    public void hideGameOver() {
        gameOverText.detachSelf();
        gameOverRectangle.detachSelf();

        gameOverText.dispose();
        gameOverRectangle.dispose();
    }

    public int getCurPatternLogoId() {
        return patternLogoIds[curPatternLogoIndex];
    }

}
