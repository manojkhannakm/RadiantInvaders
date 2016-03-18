package com.webarch.radiantinvaders.scenes.game;

import com.webarch.radiantinvaders.Game;
import com.webarch.radiantinvaders.GameManager;
import com.webarch.radiantinvaders.scenes.game.entities.Player;
import com.webarch.radiantinvaders.scenes.game.entities.PlayerProjectile;
import com.webarch.radiantinvaders.scenes.game.entities.SmallRing;
import com.webarch.radiantinvaders.scenes.game.entities.Stone;
import com.webarch.radiantinvaders.scenes.game.entities.StoneRing;
import com.webarch.radiantinvaders.scenes.game.entities.enemyentites.BlueArrowEnemy;
import com.webarch.radiantinvaders.scenes.game.entities.enemyentites.GreenArrowEnemy;
import com.webarch.radiantinvaders.scenes.game.entities.enemyentites.Logo;
import com.webarch.radiantinvaders.scenes.game.entities.enemyentites.SquareDiamondEnemy;

import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder;
import org.andengine.opengl.texture.region.TextureRegion;

import java.util.ArrayList;

/**
 * @author Manoj Khanna
 */

public class EntitySpawner {

    private GameManager gameManager;
    private GameScene gameScene;
    private BuildableBitmapTextureAtlas buildableBitmapTextureAtlas;
    private TextureRegion gameBackgroundTextureRegion, playerTextureRegion, playerProjectileTextureRegion, smallRingTextureRegion,
            greenArrowEnemyTextureRegion, blueArrowEnemyTextureRegion, squareDiamondTextureRegion;
    private TextureRegion[] stoneTextureRegions = new TextureRegion[5], stoneRingTextureRegions = new TextureRegion[5],
            logoTextureRegions = new TextureRegion[10], whiteLogoTextureRegions = new TextureRegion[10];
    private ArrayList<Entity> entities = new ArrayList<>();
    private Player player;
    private ArrayList<Entity> staticEntities = new ArrayList<>();

    public EntitySpawner(GameManager gameManager, GameScene gameScene) {
        this.gameManager = gameManager;
        this.gameScene = gameScene;

        Game game = gameManager.getGame();
        buildableBitmapTextureAtlas = new BuildableBitmapTextureAtlas(game.getTextureManager(), 2000, 2000, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        gameBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "game_background.png");
        playerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "player.png");
        playerProjectileTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "player_projectile.png");
        stoneTextureRegions[0] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "red_stone.png");
        stoneTextureRegions[1] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "green_stone.png");
        stoneTextureRegions[2] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "blue_stone.png");
        stoneTextureRegions[3] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "violet_stone.png");
        stoneTextureRegions[4] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "orange_stone.png");
        stoneRingTextureRegions[0] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "red_stone_ring.png");
        stoneRingTextureRegions[1] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "green_stone_ring.png");
        stoneRingTextureRegions[2] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "blue_stone_ring.png");
        stoneRingTextureRegions[3] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "violet_stone_ring.png");
        stoneRingTextureRegions[4] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "orange_stone_ring.png");
        logoTextureRegions[0] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "bluebook_logo.png");
        logoTextureRegions[1] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "fundaz_logo.png");
        logoTextureRegions[2] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "konstruktion_logo.png");
        logoTextureRegions[3] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "magefficie_logo.png");
        logoTextureRegions[4] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "online_logo.png");
        logoTextureRegions[5] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "praesentatio_logo.png");
        logoTextureRegions[6] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "robogyan_logo.png");
        logoTextureRegions[7] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "specials_logo.png");
        logoTextureRegions[8] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "xzone_logo.png");
        logoTextureRegions[9] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "yuddhame_logo.png");
        whiteLogoTextureRegions[0] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "white_bluebook_logo.png");
        whiteLogoTextureRegions[1] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "white_fundaz_logo.png");
        whiteLogoTextureRegions[2] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "white_konstruktion_logo.png");
        whiteLogoTextureRegions[3] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "white_magefficie_logo.png");
        whiteLogoTextureRegions[4] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "white_online_logo.png");
        whiteLogoTextureRegions[5] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "white_praesentatio_logo.png");
        whiteLogoTextureRegions[6] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "white_robogyan_logo.png");
        whiteLogoTextureRegions[7] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "white_specials_logo.png");
        whiteLogoTextureRegions[8] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "white_xzone_logo.png");
        whiteLogoTextureRegions[9] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "white_yuddhame_logo.png");
        smallRingTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "small_ring.png");
        greenArrowEnemyTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "green_arrow_enemy.png");
        blueArrowEnemyTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "blue_arrow_enemy.png");
        squareDiamondTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, game, "square_diamond_enemy.png");
        try {
            buildableBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 1));
            buildableBitmapTextureAtlas.load();
        } catch (ITextureAtlasBuilder.TextureAtlasBuilderException ignored) {
        }
    }

    private void spawn(Entity entity) {
        gameScene.attachChild(entity);
        entities.add(entity);
        if (!(entity instanceof Player) && !(entity instanceof PlayerProjectile)) {
            staticEntities.add(entity);
        }
        entity.onCreate();
    }

    public Player spawnPlayer(float x, float y) {
        Player player = new Player(gameManager, gameScene, x, y, playerTextureRegion);
        spawn(player);
        this.player = player;
        return player;
    }

    public PlayerProjectile spawnPlayerProjectile(float x, float y, float angleDeg) {
        PlayerProjectile playerProjectile = new PlayerProjectile(gameManager, gameScene, x, y, angleDeg, playerProjectileTextureRegion);
        spawn(playerProjectile);
        return playerProjectile;
    }

    public Stone spawnStone(float x, float y, int colorId) {
        Stone stone = new Stone(gameManager, gameScene, x, y, colorId, stoneTextureRegions[colorId]);
        spawn(stone);
        return stone;
    }

    public StoneRing spawnStoneRing(float x, float y, int colorId) {
        StoneRing stoneRing = new StoneRing(gameManager, gameScene, x, y, stoneRingTextureRegions[colorId]);
        spawn(stoneRing);
        return stoneRing;
    }

    public Logo spawnLogo(float x, float y, int logoId) {
        Logo logo = new Logo(gameManager, gameScene, x, y, logoId, logoTextureRegions[logoId]);
        spawn(logo);
        return logo;
    }

    public SmallRing spawnSmallRing(float x, float y) {
        SmallRing smallRing = new SmallRing(gameManager, gameScene, x, y, smallRingTextureRegion);
        spawn(smallRing);
        return smallRing;
    }

    public GreenArrowEnemy spawnGreenArrowEnemy(float x, float y) {
        GreenArrowEnemy greenArrowEnemy = new GreenArrowEnemy(gameManager, gameScene, x, y, greenArrowEnemyTextureRegion, 7.5f, 50);
        spawn(greenArrowEnemy);
        return greenArrowEnemy;
    }

    public BlueArrowEnemy spawnBlueArrowEnemy(float x, float y) {
        BlueArrowEnemy blueArrowEnemy = new BlueArrowEnemy(gameManager, gameScene, x, y, blueArrowEnemyTextureRegion, 10.0f, 100);
        spawn(blueArrowEnemy);
        return blueArrowEnemy;
    }

    public SquareDiamondEnemy spawnSquareDiamondEnemy(float x, float y) {
        SquareDiamondEnemy squareDiamondEnemy = new SquareDiamondEnemy(gameManager, gameScene, x, y, squareDiamondTextureRegion, 10.0f, 200);
        spawn(squareDiamondEnemy);
        return squareDiamondEnemy;
    }

    public void destroy(final Entity entity) {
        entities.remove(entity);
        staticEntities.remove(entity);

        entity.clearUpdateHandlers();
        entity.clearEntityModifiers();
        entity.detachSelf();
        if (!entity.isDisposed()) {
            entity.dispose();
        }
    }

    public void onDestroy() {
        gameManager.getGame().runOnUpdateThread(new Runnable() {

            @Override
            public void run() {
                player.onDestroy();
                player = null;

                for (Entity entity : new ArrayList<>(EntitySpawner.this.entities)) {
                    destroy(entity);
                }

                buildableBitmapTextureAtlas.unload();
            }

        });
    }

    public TextureRegion getGameBackgroundTextureRegion() {
        return gameBackgroundTextureRegion;
    }

    public TextureRegion getLogoTextureRegion(int index) {
        return logoTextureRegions[index];
    }

    public TextureRegion getWhiteLogoTextureRegion(int index) {
        return whiteLogoTextureRegions[index];
    }

    public ArrayList<Entity> getEntities() {
        return new ArrayList<>(entities);
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Entity> getStaticEntities() {
        return new ArrayList<>(staticEntities);
    }

}
