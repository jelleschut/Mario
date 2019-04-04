package party.game.mario;

import java.util.List;

import nl.han.ica.oopg.collision.ICollidableWithGameObjects;
import nl.han.ica.oopg.objects.GameObject;
import nl.han.ica.oopg.objects.Sprite;
import nl.han.ica.oopg.objects.SpriteObject;

/**
 * 
 * @author Jelle & Merel
 * Coin klasse
 */

public class Coin extends SpriteObject implements ICollidableWithGameObjects {

	private World world;
	
	public Coin(World world) {
		super(new Sprite(World.MEDIA_URL.concat("collectables/tile001.png")));

		this.world = world;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Houdt bij of Coin wordt opgepakt
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
