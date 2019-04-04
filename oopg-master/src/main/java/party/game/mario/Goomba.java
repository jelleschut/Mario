package party.game.mario;

import nl.han.ica.oopg.objects.Sprite;

/**
 * @author Jelle & Merel
 * Goomba enemy klasse
 */

public class Goomba extends Enemy {
	
	public Goomba(World world) {
		super(new Sprite(world.MEDIA_URL.concat("Characters/Enemies/tile001.png")));
		this.world = world;
	}
	
	@Override
	public void update() {
		if(triggerMovement()) {
			speed = 1;
		}
		
		setDirectionSpeed(direction, speed);
	}
}
