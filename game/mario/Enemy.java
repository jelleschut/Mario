package party.game.mario;

import nl.han.ica.oopg.objects.GameObject;
import nl.han.ica.oopg.objects.Sprite;
import party.game.tiles.*;
import party.game.tiles.PowerUpBoxTile;
import processing.core.PVector;

import java.util.List;

import nl.han.ica.oopg.collision.*;

/**
 *
 * @author Jelle & Merel
 * Parent klasse voor Goomba en KoopaParaTroopa
 */

public class Enemy extends Character implements ICollidableWithTiles, ICollidableWithGameObjects {

	protected float speed;
	protected float direction = 270;

	public Enemy(Sprite sprite) {
		super(sprite);
		setFriction(0);

		speed = 0;
	}
	
	/**
	 * Zorgt dat enemies pas beginnen te bewegen als ze 100px rechts van de viewport zijn.
	 * @return	boolean: true wanneer enemy zich binnen de viewport of 100px rechts daarvan bevindt.
	 */

	protected boolean triggerMovement() {
		if (getX() > world.getView().getViewport().getX()
				&& getX() < world.getView().getViewport().getX() + world.getView().getViewport().getZoomWidth() + 100) {
			return true;
		}
		return false;
	}
	
	/**
	 * Houdt bij of enemies tegen Tiles botsen
	 */

	@Override
	public void tileCollisionOccurred(List<CollidedTile> collidedTiles) {
		PVector vector;

		for (CollidedTile ct : collidedTiles) {
			vector = world.getTileMap().getTilePixelLocation(ct.getTile());

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

			if (ct.getTile() instanceof ObstacleTile || ct.getTile() instanceof CoinBoxTile
					|| ct.getTile() instanceof PowerUpBoxTile) {
				switch (ct.getCollisionSide()) {
				case LEFT:
					direction = (direction + 180) % 360;
					setX(vector.x - getWidth() - 1);
					break;
				case TOP:
					setySpeed(0);
					setY(vector.y - getHeight());
					break;
				case RIGHT:
					direction = (direction + 180) % 360;
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

			if (ct.getTile() instanceof FloorTile) {
				switch (ct.getCollisionSide()) {
				case LEFT:
					setxSpeed(0);
					setX(vector.x - getWidth() - 1);
					break;
				case TOP:
					setySpeed(0);
					setY(vector.y - getHeight());
					break;
				case RIGHT:
					setxSpeed(0);
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
	 * Houdt bij of enemies tegen GameObjects botsen
	 * 
	 */

	@Override
	public void gameObjectCollisionOccurred(List<GameObject> collidedGameObjects) {
		for (GameObject g : collidedGameObjects) {
			if (g instanceof Enemy && g.getAngleFrom(this) >= 180) {
				direction = 270;
			}
			if (g instanceof Enemy && g.getAngleFrom(this) <= 180) {
				direction = 90;
			}
		}
	}
}
