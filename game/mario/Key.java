package party.game.mario;

/**
 * 
 * @author Jelle & Merel
 * Toetsklasse om input bij te houden
 */

public class Key {

	private int keyCode;
	private boolean keyPressed;
	
	public Key(int keyCode) {
		this.keyCode = keyCode;
		keyPressed = false;
	}
	
	public int getKeyCode() {
		return keyCode;
	}
	
	public void setKeyPressed(boolean keyPressed) {
		this.keyPressed = keyPressed;
	}
	
	public boolean getKeyPressed() {
		return keyPressed;
	}
}
