package party.game.mario;

import java.util.List;

import nl.han.ica.oopg.collision.CollidedTile;
import nl.han.ica.oopg.collision.ICollidableWithGameObjects;
import nl.han.ica.oopg.collision.ICollidableWithTiles;
import nl.han.ica.oopg.objects.GameObject;
import nl.han.ica.oopg.objects.Sprite;
import nl.han.ica.oopg.objects.SpriteObject;
import party.game.tiles.*;
import processing.core.PVector;

/**
 * 
 * @author Jelle & Merel
 * PowerUp collectable klasse
 */

public class PowerUp extends SpriteObject implements ICollidableWithTiles, ICollidableWithGameObjects {

	private World world;
	private float speed;

	public PowerUp(World world) {
		super(new Sprite(World.MEDIA_URL.concat("collectables/tile000.png")));

		this.world = world;

		speed = 1;
		setGravity(0.6f);
		setxSpeed(speed);
	}

	@Override
	public void update() {
		setxSpeed(speed);
	}
	
	/**
	 * Houdt bij of PowerUp met Tiles botst
	 */

	@Override
	public void tileCollisionOccurred(List<CollidedTile> collidedTiles) {
		PVector vector;

		for (CollidedTile ct : collidedTiles) {
			vector = world.getTileMap().getTilePixelLocation(ct.getTile());

			if (ct.getTile() instanceof FloorTile || ct.getTile() instanceof PlateauTile) {
				switch (ct.getCollisionSide()) {
				case TOP:
					setySpeed(0);
					setY(vector.y - getHeight());
					break;
				default:
					break;
				}
			}

			if (ct.getTile() instanceof ObstacleTile || ct.getTile() instanceof CoinBoxTile
					|| ct.getTile() instanceof PowerUpBoxTile) {
				switch (ct.getCollisionSide()) {
				case LEFT:
					speed = -speed;
					setX(vector.x - getWidth() - 1);
					break;
				case TOP:
					setySpeed(0);
					setY(vector.y - getHeight());
					break;
				case RIGHT:
					speed = -speed;
					setX(vector.x + world.getTileMap().getTileSize() + 1);
					break;
				case BOTTOM:
					setySpeed(0);
					setY(vector.y + world.getTileMap().getTileSize());
					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 * Houdt bij of PowerUp wordt opgepakt.
	 */
	
	@Override
	public void gameObjectCollisionOccurred(List<GameObject> collidedGameObjects) {
		for (GameObject g : collidedGameObjects) {
			if (g instanceof Player) {
				world.deleteGameObject(this);
			}
		}
	}
}
