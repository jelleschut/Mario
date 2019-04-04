package party.game.tiles;

import nl.han.ica.oopg.objects.Sprite;
import nl.han.ica.oopg.tile.Tile;
import party.game.mario.World;

public class CoinBoxTile extends Tile{

	private boolean empty = false;
	/*
	 * 
	 * @param sprite image to be drawn as floor tile
	 */
	
	public CoinBoxTile(Sprite sprite) {
		super(sprite);
	}
	
	public void setEmptyCoinBoxTile() {
		Sprite emptyBox = new Sprite(World.MEDIA_URL.concat("tiles/tile003.png"));
		emptyBox.resize(32, 32);
		setSprite(emptyBox);
		empty = true;
	}
	
	public boolean getEmpty() {
		return empty;
	}
}
