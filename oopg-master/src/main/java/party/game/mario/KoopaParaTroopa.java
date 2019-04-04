package party.game.mario;

import nl.han.ica.oopg.objects.Sprite;


/**
 * @author Jelle & Merel
 * KoopaParaTroopa enemy klasse
 */

public class KoopaParaTroopa extends Enemy{
	
	private float minX;
	private float maxX;
	
	public KoopaParaTroopa(World world, float minX, float maxX) {
		super(new Sprite(world.MEDIA_URL.concat("Characters/Enemies/tile000.png")));
		this.world = world;
		this.minX = minX;
		this.maxX = maxX;
		
		setGravity(0);
	}
	
	@Override
	public void update() {
		if(triggerMovement()) {
			speed = 1;
			setDirectionSpeed(direction, speed);
		}
		if (x < minX || x > maxX) {
			direction = (direction + 180) % 360;
			setDirectionSpeed(direction, speed);
		}   

	}

	public float getMinX() {
		return minX;
	}

	public void setMinX(float minX) {
		this.minX = minX;
	}

	public float getMaxX() {
		return maxX;
	}

	public void setMaxX(float maxX) {
		this.maxX = maxX;
	}
}
