package party.game.mario;

import nl.han.ica.oopg.objects.*;

/**
 * 
 * @author Jelle & Merel
 * Parent klasse voor alle wezens in het spel
 */

public class Character extends SpriteObject {

	protected World world;
	protected float gravity;
	protected float friction;

	public Character(Sprite sprite) {
		super(sprite);
		gravity = 0.6f;
		friction = 0.1f;

		setGravity(gravity);
		setFriction(friction);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

}
