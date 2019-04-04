package party.game.mario;

import java.util.ArrayList;

import nl.han.ica.oopg.dashboard.Dashboard;
import nl.han.ica.oopg.engine.GameEngine;
import nl.han.ica.oopg.view.*;

import nl.han.ica.oopg.objects.Sprite;
import nl.han.ica.oopg.objects.TextObject;
import nl.han.ica.oopg.persistence.FilePersistence;
import nl.han.ica.oopg.persistence.IPersistence;
import nl.han.ica.oopg.sound.Sound;
import nl.han.ica.oopg.tile.TileMap;
import nl.han.ica.oopg.tile.TileType;

import party.game.tiles.*;

/**
 * @author Jelle Schut & Merel van de Graaf
 * 
 */

@SuppressWarnings("serial")
public class World extends GameEngine {

	private int worldHeight;
	private int worldWidth;
	private int viewportHeight;
	private int viewportWidth;
	private int tileSize;

	private int lives;
	private int score;

	private IPersistence persistence;
	private TextObject livesText;
	private TextObject scoreText;
	private TextObject highscoreText;
	private int highscore;

	private Player player;
	private ArrayList<Goomba> goombas = new ArrayList<>();
	private float[][] goombaCoordinates = { { 22, 12 }, { 40, 12 }, { 51, 12 }, { 53, 12 }, { 80, 4 }, { 82, 4 },
			{ 97, 12 }, { 99, 12 }, { 114, 12 }, { 116, 12 }, { 124, 12 }, { 126, 12 }, { 128, 12 }, { 130, 12 },
			{ 174, 12 }, { 176, 12 } };
	private ArrayList<KoopaParaTroopa> koopaParas = new ArrayList<>();
	private float[][] koopaParaCoordinates = { { 107, 12 } };

	public static String MEDIA_URL = "src/main/java/party/game/mario/media/";

	public static void main(String[] args) {
		World tw = new World();
		tw.runSketch();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see nl.han.ica.oopg.engine.GameEngine#setupGame() Hier worden alle
	 *      benodigdheden voor het starten van het spel geinitialiseerd
	 */

	@Override
	public void setupGame() {
		lives = 3;
		score = 0;
		setupWorld();
		setupPersistence();
		setupTileMap();
		setupPlayer();
		setupEnemies();
		setupView();
		setupSound();
		setupDashboard(viewportWidth, 100);
	}

	/**
	 * Hier worden de noodzakelijke zaken opnieuw geinitialiseerd voor een respawn
	 * 
	 */

	public void respawnPlayer() {
		deleteAllGameOBjects();
		setupPlayer();
		setupEnemies();
		setupView();
		setupSound();
	}

	/**
	 * Hier worden de zaken gereset die nodig zijn voor om de game opnieuw op te
	 * starten Dit gebeurt als er geen levens meer zijn of de speler het level haalt
	 */

	public void resetGame() {
		if (score > highscore) {
			persistence.saveData(Integer.toString(score));
			highscore = score;
		}

		lives = 3;
		score = 0;
		deleteAllGameOBjects();
		setupTileMap();
		setupPlayer();
		setupEnemies();
		setupView();
		setupSound();
	}

	/**
	 * Hier worden variabelen geinitialiseerd.
	 */

	private void setupWorld() {
		worldWidth = 6784;
		worldHeight = 448;
		viewportWidth = 800;
		viewportHeight = 448;
		tileSize = 32;
	}

	/**
	 * Deze methode intitialiseerd achtergrond muziek
	 */

	private void setupSound() {
		Sound themeSong = new Sound(this, MEDIA_URL.concat("Sound/themesong.mp3"));
		themeSong.cue(0);
		themeSong.loop(-1);
	}

	/**
	 * Deze methode initialiseerd de highscore text file
	 */

	private void setupPersistence() {
		persistence = new FilePersistence("highscore.txt");
		if (persistence.fileExists()) {
			highscore = Integer.parseInt(persistence.loadDataString());
		}
	}

	/**
	 * Deze methode voegt een speler aan de game toe
	 */

	private void setupPlayer() {
		player = new Player(this);
		addGameObject(player, 0, 12 * tileSize - player.getHeight());
	}

	/**
	 * Deze methode voegt enemies toe
	 */

	private void setupEnemies() {
		goombas.removeAll(goombas);
		for (int i = 0; i < goombaCoordinates.length; i++) {
			goombas.add(new Goomba(this));
		}

		for (Goomba goom : goombas) {
			addGameObject(goom, goombaCoordinates[goombas.indexOf(goom)][0] * tileSize,
					goombaCoordinates[goombas.indexOf(goom)][1] * tileSize - goom.getHeight());
		}

		koopaParas.removeAll(koopaParas);
		for (int i = 0; i < koopaParaCoordinates.length; i++) {
			koopaParas.add(new KoopaParaTroopa(this, (koopaParaCoordinates[i][0] - 10) * tileSize,
					koopaParaCoordinates[i][0] * tileSize));
		}
		for (KoopaParaTroopa koop : koopaParas) {
			addGameObject(koop, koopaParaCoordinates[koopaParas.indexOf(koop)][0] * tileSize,
					koopaParaCoordinates[koopaParas.indexOf(koop)][1] * tileSize - koop.getHeight());
		}
	}

	/**
	 * Creeert de meebewegende view
	 */

	private void setupView() {
		EdgeFollowingViewport viewport = new EdgeFollowingViewport(player, viewportWidth, viewportHeight,
				-viewportWidth / 2, 4.5 * tileSize);
		viewport.setRightTolerance(viewportWidth / 2);

		viewport.setBottomTolerance(-10 * (int) player.getHeight());
		viewport.setTopTolerance(-10 * (int) player.getHeight());

		View view = new View(viewport, worldWidth, worldHeight);
		setView(view);

		view.setBackground(loadImage(MEDIA_URL.concat("Background/backgroundlarge.png")));
		size(viewportWidth, viewportHeight);
	}

	/**
	 * creeert het dashboard met tekst in beeld
	 * @param dashWidth  breedte van het dashboard
	 * @param dashHeight hoogete van het dashboard
	 */

	private void setupDashboard(int dashWidth, int dashHeight) {
		Dashboard dashboard = new Dashboard(0, 0, dashWidth, dashHeight);
		livesText = new TextObject("", 20);
		scoreText = new TextObject("", 20);
		highscoreText = new TextObject("", 20);
		dashboard.addGameObject(livesText, 50, 40, 100);
		dashboard.addGameObject(scoreText, viewportWidth - 200, 25, 100);
		dashboard.addGameObject(highscoreText, viewportWidth - 200, 55, 100);
		addDashboard(dashboard);
		updateDashboard();
	}

	/**
	 * zorgt dat dashboard informatie up to date blijft
	 */

	private void updateDashboard() {
		livesText.setText("Lives " + getLives());
		scoreText.setText("Score " + getScore());
		highscoreText.setText("Highscore " + highscore);
	}

	/**
	 * Kan gebruikt worden om powerups aan de wereld toe te voegen
	 * @param x		x-coordinaat van beginpositie powerup
	 * @param y		y-coordinaat van beginpositie powerup
	 */

	public void addPowerUp(float x, float y) {
		PowerUp powerUp = new PowerUp(this);
		addGameObject(powerUp, x, y - powerUp.getHeight());
	}

	/**
	 * Kan gebruikt worden om coins aan de wereld toe te voegen
	 * @param x		x-coordinaat van coin
	 * @param y		y-coordinaat van coin
	 */

	public void addCoin(float x, float y) {
		Coin coin = new Coin(this);
		addGameObject(coin, x, y - coin.getHeight());
	}

	@Override
	public void update() {
		updateDashboard();
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void addScore(int score) {
		this.score += score;
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	public void subtractLife() {
		this.lives -= 1;
	}
	
	/**
	 * Hier wordt de volledige tilemap geladen.
	 * Eerst worden alle sprites lokaal geinitialiseerd.
	 * Daarna worden deze sprites aan tile types toegevoegd
	 * Deze worden in een array ingevoerd.
	 * Vervolgens wordt een kaart opgebouwd met de indices van deze tile types.
	 */

	private void setupTileMap() {

		// Sprites
		// Floor
		Sprite floorSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile000.png"));

		// Background
		// Hills
		Sprite hillLeftSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile272.png"));
		Sprite hillRightSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile274.png"));
		Sprite hillEmptySprite = new Sprite(this.MEDIA_URL.concat("tiles/tile306.png"));
		Sprite hillSpotLeftSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile307.png"));
		Sprite hillSpotRightSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile305.png"));
		Sprite hillTopSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile273.png"));

		// Bush
		Sprite bushLeftSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile308.png"));
		Sprite bushMidSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile309.png"));
		Sprite bushRightSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile310.png"));

		// Clouds
		Sprite cloudTopLeftSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile660.png"));
		Sprite cloudBottomLeftSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile693.png"));
		Sprite cloudTopMidSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile661.png"));
		Sprite cloudBottomMidSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile694.png"));
		Sprite cloudTopRightSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile662.png"));
		Sprite cloudBottomRightSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile695.png"));

		// Castle
		Sprite castleBattlementsEmptySprite = new Sprite(this.MEDIA_URL.concat("tiles/tile011.png"));
		Sprite castleBattlementsBrickSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile044.png"));
		Sprite castleBrickSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile013.png"));
		Sprite castleLeftWindowSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile014.png"));
		Sprite castleRightWindowSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile012.png"));
		Sprite castleDoorTopSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile045.png"));
		Sprite castleDoorSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile046.png"));

		// Platforms
		// Plateaus
		Sprite plateauSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile001.png"));

		// Boxes
		Sprite boxSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile024.png"));
		Sprite emptyBoxSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile027.png"));

		// Obstacles
		// Pipe
		Sprite pipeTopLeftSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile264.png"));
		Sprite pipeTopRightSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile265.png"));
		Sprite pipeLeftSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile297.png"));
		Sprite pipeRightSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile298.png"));

		// Block
		Sprite blockSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile033.png"));

		// Flag
		Sprite flagPoleSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile313.png"));
		Sprite flagTopSprite = new Sprite(this.MEDIA_URL.concat("tiles/tile280.png"));

		// ---------------------------------------------------------------------------------
		// Tiles

		// Floor
		TileType<FloorTile> floorTile = new TileType<>(FloorTile.class, floorSprite); // 00

		// Background
		// Hills
		TileType<BackgroundTile> hillLeftTile = new TileType<>(BackgroundTile.class, hillLeftSprite); // 01
		TileType<BackgroundTile> hillRightTile = new TileType<>(BackgroundTile.class, hillRightSprite); // 02
		TileType<BackgroundTile> hillEmptyTile = new TileType<>(BackgroundTile.class, hillEmptySprite); // 03
		TileType<BackgroundTile> hillSpotLeftTile = new TileType<>(BackgroundTile.class, hillSpotLeftSprite); // 04
		TileType<BackgroundTile> hillSpotRightTile = new TileType<>(BackgroundTile.class, hillSpotRightSprite); // 05
		TileType<BackgroundTile> hillTopTile = new TileType<>(BackgroundTile.class, hillTopSprite); // 06

		// Bush
		TileType<BackgroundTile> bushLeftTile = new TileType<>(BackgroundTile.class, bushLeftSprite); // 07
		TileType<BackgroundTile> bushMidTile = new TileType<>(BackgroundTile.class, bushMidSprite); // 08
		TileType<BackgroundTile> bushRightTile = new TileType<>(BackgroundTile.class, bushRightSprite); // 09

		// Clouds
		TileType<BackgroundTile> cloudTopLeftTile = new TileType<>(BackgroundTile.class, cloudTopLeftSprite); // 10
		TileType<BackgroundTile> cloudBottomLeftTile = new TileType<>(BackgroundTile.class, cloudBottomLeftSprite); // 11
		TileType<BackgroundTile> cloudTopMidTile = new TileType<>(BackgroundTile.class, cloudTopMidSprite); // 12
		TileType<BackgroundTile> cloudBottomMidTile = new TileType<>(BackgroundTile.class, cloudBottomMidSprite); // 13
		TileType<BackgroundTile> cloudTopRightTile = new TileType<>(BackgroundTile.class, cloudTopRightSprite); // 14
		TileType<BackgroundTile> cloudBottomRightTile = new TileType<>(BackgroundTile.class, cloudBottomRightSprite); // 15

		// Castle
		TileType<BackgroundTile> castleBattlementsEmptyTile = new TileType<>(BackgroundTile.class,
				castleBattlementsEmptySprite); // 16
		TileType<BackgroundTile> castleBattlementsBrickTile = new TileType<>(BackgroundTile.class,
				castleBattlementsBrickSprite); // 17
		TileType<BackgroundTile> castleBrickTile = new TileType<>(BackgroundTile.class, castleBrickSprite); // 18
		TileType<BackgroundTile> castleLeftWindowTile = new TileType<>(BackgroundTile.class, castleLeftWindowSprite); // 19
		TileType<BackgroundTile> castleRightWindowTile = new TileType<>(BackgroundTile.class, castleRightWindowSprite); // 20
		TileType<BackgroundTile> castleDoorTopTile = new TileType<>(BackgroundTile.class, castleDoorTopSprite); // 21
		TileType<BackgroundTile> castleDoorTile = new TileType<>(BackgroundTile.class, castleDoorSprite); // 22

		// Platforms
		// Plateau
		TileType<PlateauTile> plateauTile = new TileType<>(PlateauTile.class, plateauSprite); // 23
		TileType<PlateauTile> emptyBoxTile = new TileType<>(PlateauTile.class, emptyBoxSprite); // 24

		// Boxes
		TileType<CoinBoxTile> coinBoxTile = new TileType<>(CoinBoxTile.class, boxSprite); // 25
		TileType<PowerUpBoxTile> powerUpBoxTile = new TileType<>(PowerUpBoxTile.class, boxSprite); // 26

		// Obstacles
		// pipe
		TileType<ObstacleTile> pipeTopLeftTile = new TileType<>(ObstacleTile.class, pipeTopLeftSprite); // 27
		TileType<ObstacleTile> pipeTopRightTile = new TileType<>(ObstacleTile.class, pipeTopRightSprite); // 28
		TileType<ObstacleTile> pipeLeftTile = new TileType<>(ObstacleTile.class, pipeLeftSprite); // 29
		TileType<ObstacleTile> pipeRightTile = new TileType<>(ObstacleTile.class, pipeRightSprite); // 30

		// Block
		TileType<ObstacleTile> blockTile = new TileType<>(ObstacleTile.class, blockSprite); // 31

		// Flag
		TileType<FlagPoleTile> flagPoleTile = new TileType<>(FlagPoleTile.class, flagPoleSprite); // 32
		TileType<FlagPoleTile> flagTopTile = new TileType<>(FlagPoleTile.class, flagTopSprite); // 33

		@SuppressWarnings("rawtypes")
		TileType[] tileTypes = { floorTile, hillLeftTile, hillRightTile, hillEmptyTile, hillSpotLeftTile,
				hillSpotRightTile, hillTopTile, bushLeftTile, bushMidTile, bushRightTile, cloudTopLeftTile,
				cloudBottomLeftTile, cloudTopMidTile, cloudBottomMidTile, cloudTopRightTile, cloudBottomRightTile,
				castleBattlementsEmptyTile, castleBattlementsBrickTile, castleBrickTile, castleLeftWindowTile,
				castleRightWindowTile, castleDoorTopTile, castleDoorTile, plateauTile, emptyBoxTile, coinBoxTile,
				powerUpBoxTile, pipeTopLeftTile, pipeTopRightTile, pipeLeftTile, pipeRightTile, blockTile, flagPoleTile,
				flagTopTile };

		// worldwidth = 212 tiles
		// worldheight = 14 tiles
		int tilesMap[][] = {
				// 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10
				// 10 20 30 40 50 60 70 80 90 100 110 120 130 140 150 160 170 180 190 200 210
				{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, },
				{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 12, 14, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 12, 12, 14, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 12, 14, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 12, 12, 14, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 12, 14, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 12, 12, 14, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 12, 14, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 12, 12, 14, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, 33, -1, 10, 12, 14, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, },
				{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 11, 13, 15, -1, -1, -1,
						-1, -1, 10, 12, 12, 12, 14, -1, -1, -1, -1, -1, 11, 13, 13, 15, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, 10, 12, 14, -1, -1, -1, -1, -1, -1, -1, -1, 11, 13, 15, -1, -1, -1,
						-1, -1, 10, 12, 12, 12, 14, -1, -1, -1, -1, 11, 13, 13, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, 10, 12, 14, -1, -1, -1, -1, -1, -1, -1, -1, 11, 13, 15, -1, -1, -1,
						-1, -1, 10, 12, 12, 12, 14, -1, -1, -1, -1, 11, 13, 13, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, 10, 12, 14, -1, -1, -1, -1, -1, -1, -1, -1, 11, 13, 15, -1, -1, -1,
						-1, 10, 12, 12, 12, 14, -1, -1, -1, -1, 11, 13, 13, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, 32, -1, 11, 13, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, },
				{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, 11, 13, 13, 13, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, 11, 13, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, 11, 13, 13, 13, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, 11, 13, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, 11, 13, 13, 13, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, 11, 13, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, 11, 13, 13, 13, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, 32, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, },
				{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 25, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, 23, 23, 23, 23, 23, 23, 23, 23, -1, -1, -1, 23, 23, 23, 25, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 25, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						23, 23, 23, -1, -1, -1, -1, 23, 25, 25, 23, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 31, 31, -1, -1, -1, -1,
						-1, -1, -1, -1, 32, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, },
				{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 31, 31, 31, -1, -1, -1, -1,
						-1, -1, -1, -1, 32, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, },
				{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 31, 31, 31, 31, -1, -1, -1, -1,
						-1, -1, -1, -1, 32, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, },
				{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 31, 31, 31, 31, 31, -1, -1, -1, -1,
						-1, -1, -1, -1, 32, -1, -1, -1, -1, 16, 16, 16, -1, -1, -1, -1, -1, -1, -1, },
				{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 25, -1, -1, -1, 23, 25, 23, 26, 23,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 27, 28, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, 27, 28, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, 23, 25, 23, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 23, -1, -1,
						-1, -1, -1, 23, 23, -1, -1, -1, -1, 25, -1, -1, 25, -1, -1, 25, -1, -1, -1, -1, -1, 23, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, 23, 23, -1, -1, -1, -1, -1, -1, 31, -1, -1, 31, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, 31, 31, -1, -1, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 23, 23,
						25, 23, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 31, 31, 31, 31, 31, 31, -1, -1, -1, -1,
						-1, -1, -1, -1, 32, -1, -1, -1, -1, 20, 18, 19, -1, -1, -1, -1, -1, -1, -1, },
				{ -1, -1, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 27, 28, -1, -1, -1, -1, -1, 29, 30, -1, -1,
						6, -1, -1, -1, -1, -1, -1, 29, 30, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 31, 31, -1, -1, 31, 31, -1, -1, -1, -1,
						6, -1, -1, -1, 31, 31, 31, -1, -1, 31, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 31, 31, 31, 31, 31, 31, 31, -1, -1, -1, -1, 6,
						-1, -1, -1, 32, -1, -1, -1, 16, 17, 17, 17, 16, -1, -1, -1, -1, -1, -1, },
				{ -1, 1, 5, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 6, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, 27, 28, -1, -1, -1, -1, -1, -1, -1, -1, -1, 29, 30, -1, -1, -1, -1, -1, 29, 30, -1, 1,
						5, 2, -1, -1, -1, -1, -1, 29, 30, -1, -1, -1, -1, -1, -1, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 5, 2,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 31, 31, 31, -1, -1, 31, 31, 31, -1, -1, 1, 5, 2, -1,
						31, 31, 31, 31, -1, -1, 31, 31, 31, -1, -1, -1, 6, -1, 27, 28, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, 27, 28, -1, 31, 31, 31, 31, 31, 31, 31, 31, -1, -1, -1, 1, 5, 2, -1, -1, 32,
						-1, -1, -1, 18, 18, 21, 18, 18, -1, -1, 6, -1, -1, -1, },
				{ 1, 5, 3, 4, 2, -1, -1, -1, -1, -1, -1, 7, 8, 8, 8, 9, 1, 5, 2, -1, -1, -1, -1, 7, 8, 9, -1, -1, 29,
						30, -1, -1, -1, -1, -1, -1, -1, -1, -1, 29, 30, -1, 7, 8, 8, 9, 29, 30, 1, 5, 3, 4, 2, -1, -1,
						-1, -1, 29, 30, 7, 8, 8, 8, 9, 1, 5, 2, -1, -1, -1, -1, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, 7, 8, 8, 9, -1, -1, -1, 1, 5, 3, 4, 2, -1, -1, -1, -1, -1, -1, 7, 8,
						8, 8, 9, 1, 5, 2, -1, -1, -1, -1, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 31,
						31, 31, 31, 8, 8, 31, 31, 31, 31, 1, 5, 3, 4, 31, 31, 31, 31, 31, -1, -1, 31, 31, 31, 31, 9, 1,
						5, 2, 29, 30, -1, -1, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, -1, 29, 30, 31, 31, 31, 31, 31, 31,
						31, 31, 31, -1, -1, 1, 5, 3, 4, 2, -1, 31, -1, -1, -1, 18, 18, 22, 18, 18, 9, 1, 5, 2, -1,
						-1, },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, }, };// 10 20 30 40 50 60 70 80
																							// 90 100 110 120 130 140
																							// 150 160 170 190 200 210
		tileMap = new TileMap(tileSize, tileTypes, tilesMap);

	}
}
