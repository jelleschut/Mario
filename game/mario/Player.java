package party.game.mario;

import java.util.ArrayList;
import java.util.List;

import nl.han.ica.oopg.userinput.*;
import party.game.tiles.*;
import processing.core.PVector;
import nl.han.ica.oopg.objects.*;
import nl.han.ica.oopg.sound.Sound;
import nl.han.ica.oopg.collision.*;


/**
 * @author Jelle & Merel
 * De speler, oftewel Mario
 */

public class Player extends Character implements IKeyInput, ICollidableWithGameObjects, ICollidableWithTiles {

	private Sound jumpSound;

	private int speed;
	private int jumpSpeed;
	private boolean canJump;
	private ArrayList<Key> keys = new ArrayList<>();
	private boolean bigMario;

	public Player(World world) {
		super(new Sprite(world.MEDIA_URL.concat("Characters/Mario/tile000.png")));
		this.world = world;

		this.speed = 5;
		this.jumpSpeed = 25;
		this.canJump = false;

		setBigMario(false);
		initializeKeys();
		initializeSound();
	}

	/**
	 * Hier wordt geluid geinitialiseerd voor het springgeluid
	 */
	
	private void initializeSound() {
		jumpSound = new Sound(world, world.MEDIA_URL.concat("Sound/jumpsound.mp3"));
	}

	/**
	 * maakt ArrayList met toetsen.
	 */
	
	private void initializeKeys() {
		keys.add(new Key(UP));
		keys.add(new Key(LEFT));
		keys.add(new Key(RIGHT));
	}

	/**
	 * Kijkt of een toets is ingedrukt
	 */
	
	@Override
	public void keyPressed(int keyCode, char key) {
		for (Key k : keys) {
			if (keyCode == k.getKeyCode()) {
				k.setKeyPressed(true);
			}
		}
	}
	
	/**
	 * Kijkt of een toets is losgelaten
	 */

	
	@Override
	public void keyReleased(int keyCode, char key) {
		for (Key k : keys) {
			if (keyCode == k.getKeyCode()) {
				k.setKeyPressed(false);
			}
		}
	}

	@Override
	public void update() {
		if (keys.get(0).getKeyPressed() && canJump) {
			setySpeed(-jumpSpeed);
			jumpSound.cue(0);
			jumpSound.play();
			canJump = false;
		}
		if (keys.get(1).getKeyPressed()) {
			setxSpeed(-speed);
		}
		if (keys.get(2).getKeyPressed()) {
			setxSpeed(speed);
		}

		if (getY() >= world.getView().getViewport().getZoomHeight()) {
			respawn();
		}

		if (getX() < world.getView().getViewport().getX()) {
			setxSpeed(0);
			setX(world.getView().getViewport().getX());
		}
	}
	
	/**
	 * Past score en aantal levens wanneer speler af gaat
	 */

	private void respawn() {
		world.subtractLife();
		if (world.getLives() > 0) {
			world.respawnPlayer();
			world.addScore(-1000);
			if (world.getScore() < 0) {
				world.setScore(0);
			}
		} else {
			world.resetGame();
		}
	}

	/**
	 * Houdt bij of speler tegen een tile botst
	 */
	
	@Override
	public void tileCollisionOccurred(List<CollidedTile> collidedTiles) {
		PVector vector;
		for (CollidedTile ct : collidedTiles) {
			vector = world.getTileMap().getTilePixelLocation(ct.getTile());

			if (ct.getTile() instanceof ObstacleTile || ct.getTile() instanceof CoinBoxTile
					|| ct.getTile() instanceof PowerUpBoxTile || ct.getTile() instanceof FloorTile) {
				switch (ct.getCollisionSide()) {
				case LEFT:
					setxSpeed(0);
					setX(vector.x - getWidth());
					break;
				case TOP:
					setySpeed(0);
					setY(vector.y - getHeight());
					break;
				case RIGHT:
					setxSpeed(0);
					setX(vector.x + world.getTileMap().getTileSize());
					break;
				case BOTTOM:
					setySpeed(-0.01f);
					setY(vector.y + world.getTileMap().getTileSize());
					break;
				default:
					break;
				}
			}

			if (ct.getTile() instanceof PlateauTile) {
				switch (ct.getCollisionSide()) {
				case TOP:
					setySpeed(0);
					setY(vector.y - getHeight());
					break;
				default:
					break;
				}
			}

			if (ct.getTile() instanceof CoinBoxTile) {
				switch (ct.getCollisionSide()) {
				case BOTTOM:
					if (!((CoinBoxTile) ct.getTile()).getEmpty()) {
						((CoinBoxTile) ct.getTile()).setEmptyCoinBoxTile();
						world.addCoin(vector.x, vector.y);
					}
					break;
				default:
					break;
				}
			}

			if (ct.getTile() instanceof PowerUpBoxTile) {

				switch (ct.getCollisionSide()) {
				case BOTTOM:
					if (!((PowerUpBoxTile) ct.getTile()).getEmpty()) {
						((PowerUpBoxTile) ct.getTile()).setEmptyPowerUpBoxTile();
						world.addPowerUp(vector.x, vector.y);
					}
					break;
				default:
					break;
				}
			}

			if (ct.getTile() instanceof ObstacleTile || ct.getTile() instanceof CoinBoxTile
					|| ct.getTile() instanceof PowerUpBoxTile || ct.getTile() instanceof FloorTile
					|| ct.getTile() instanceof PlateauTile) {
				switch (ct.getCollisionSide()) {
				case TOP:
					canJump = true;
					break;
				default:
					canJump = false;
					break;
				}
			}

			if (ct.getTile() instanceof FlagPoleTile) {
				switch (ct.getCollisionSide()) {
				case LEFT:
					if (vector.y < 100) {
						world.addScore(2000);
					} else if (vector.y < 200) {
						world.addScore(1000);
					} else if (vector.y < 300) {
						world.addScore(500);
					} else if (vector.y < 400) {
						world.addScore(250);
					} else if (vector.y < 500) {
						world.addScore(125);
					} else {
						world.addScore(50);
					}
					System.out.println("trigger");
					world.addScore(world.getLives() * 500);
					world.resetGame();
					break;
				default:
					break;
				}
			}
		}
	}
	
	/**
	 * Houdt bij of speler tegen een GameObject botst
	 */

	@Override
	public void gameObjectCollisionOccurred(List<GameObject> collidedGameObjects) {
		for (GameObject g : collidedGameObjects) {
			if (g instanceof Enemy && ((g.getAngleFrom(this) >= 0 && g.getAngleFrom(this) <= 60)
					|| (g.getAngleFrom(this) >= 300 && g.getAngleFrom(this) <= 359))) {
				world.deleteGameObject(g);
				this.setySpeed(-12);
				world.addScore(100);
			}
			
			if (g instanceof Enemy && (g.getAngleFrom(this) > 60 && g.getAngleFrom(this) < 300) && !isBigMario()) {
				respawn();
			}
			
			if (g instanceof Enemy && (g.getAngleFrom(this) > 45 && g.getAngleFrom(this) < 315) && isBigMario()) {
				this.sprite = new Sprite(world.MEDIA_URL.concat("Characters/Mario/tile000.png"));
				world.deleteGameObject(g);
				setHeight(sprite.getHeight());
				setWidth(sprite.getWidth());
				setBigMario(false);
				world.addScore(-100);
			}
			
			if (g instanceof PowerUp) {
				this.sprite = new Sprite(world.MEDIA_URL.concat("Characters/Mario/tile001.png"));
				setHeight(sprite.getHeight());
				setWidth(sprite.getWidth());
				setBigMario(true);
				world.addScore(200);
			}
			
			if (g instanceof Coin) {
				world.addScore(10);
			}
		}
	}
	
	public boolean isBigMario() {
		return bigMario;
	}

	public void setBigMario(boolean bigMario) {
		this.bigMario = bigMario;
	}
}
