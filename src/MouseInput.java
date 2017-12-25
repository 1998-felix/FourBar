import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseInput implements MouseListener, MouseMotionListener {
	Controller control;

	public void mouseDragged(MouseEvent e) {
		// Get the mouse position and pass it into the drag method of the controller
		int mouseX = e.getX();
		int mouseY = e.getY();
		control.drag(mouseX, mouseY);
	}
	
	public void mouseReleased(MouseEvent e) {
		
	}

	public void mouseMoved(MouseEvent arg0) {
		
	}

	public void mouseClicked(MouseEvent e) {
		
	}
	
	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		control.click(mouseX, mouseY);
	}
	
	public MouseInput(Controller controller) {
		control = controller;
	}
}
