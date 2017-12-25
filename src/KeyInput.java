import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {
	Controller control;
	int key; // keycode value of the key being pressed
	
	public KeyInput(Controller control){
		this.control = control;
	}
	
	public void keyPressed(KeyEvent e) { // When a key is pressed, pass the key code as an argument to the keyPress method of controller
		key = e.getKeyCode();
		control.keyPress(key);
	}

}
