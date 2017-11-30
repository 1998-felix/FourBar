import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class Controller extends Canvas implements Runnable {
	private static final long serialVersionUID = -6748925586576993018L;
	public static final int WIDTH = 1024, HEIGHT = 768;
	ArrayList<Point2D> drawing = new ArrayList<Point2D>();
	private MouseInput mouseInput;
	private boolean running;
	private Thread thread;
	
	public void drag(int mouseX, int mouseY) {
		drawing.add(new Point2D.Double(mouseX, mouseY));
	}
	
	public void release() {
		
	}
	
	public void tick() {
		
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs==null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(Color.white);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		g.setColor(Color.black);
		for(int i=0;i<drawing.size();i++) {
			g.fillRect((int) drawing.get(i).getX()-2, (int) drawing.get(i).getY()-2, 4, 4);
			if(i>0) {
				g.drawLine((int) drawing.get(i).getX(), (int) drawing.get(i).getY(), (int) drawing.get(i-1).getX(), (int) drawing.get(i-1).getY());
			}
		}
		
		g.dispose();
		bs.show();
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}
	
	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(running) {
        	long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >=1) {
            	tick();
            	delta--;
            }
            render();
            frames++;
            if(System.currentTimeMillis() - timer > 1000) {
            	timer += 1000;
            	System.out.println("FPS: "+ frames);
            	frames = 0;
            }
        }
        stop();
    }
	
	public Controller() {
		new Window(WIDTH, HEIGHT, "Draw", this);
		mouseInput = new MouseInput(this);
		this.addMouseListener(mouseInput);
		this.addMouseMotionListener(mouseInput);
	}
	
	public static void main(String[] args) {
		new Controller();
	}
}
