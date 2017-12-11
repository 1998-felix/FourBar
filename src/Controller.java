import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
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
	private double similarity;
	
	public void compare(ArrayList<Point2D> draw1, ArrayList<Point2D> draw2) {
		
	}
	
	public ArrayList<Point2D> rescale(ArrayList<Point2D> fourBarPointsTemp, ArrayList<Point2D> drawingPointsTemp){
		
		ArrayList<Point2D>  fourBarPoints = new ArrayList<Point2D>();
		ArrayList<Point2D>  drawingPoints = new ArrayList<Point2D>();
		fourBarPoints = fourBarPointsTemp;
		drawingPoints = drawingPointsTemp;
		ArrayList<Point2D> outputMe  = new ArrayList<Point2D>();
			
		
		
		double scaling = ((getMaxX(fourBarPoints) - getMinX(fourBarPoints)) / (getMaxX(drawingPoints) - getMinX(drawingPoints)));
		double minX = getMinX(drawingPoints);
		double minY = getMinY(drawingPoints);
		
		for(int i=0; i<= drawingPoints.size(); i++){
			
			outputMe.set(i, new Point2D.Double((drawingPoints.get(i).getX() - minX)*scaling,(drawingPoints.get(i).getY() - minY)*scaling));
			
		}
		return outputMe;
		
	}
	public double getMinX(ArrayList<Point2D> points){
		
		double val = points.get(0).getX();
		for(int i=0; i<= points.size(); i++){
			if(val>points.get(i).getX()){
				val = points.get(i).getX();
			}	
		}
		return(val);
	}
	public double getMaxX(ArrayList<Point2D> points){
		
		double val = points.get(0).getX();
		for(int i=0; i<= points.size(); i++){
			if(val<points.get(i).getX()){
				val = points.get(i).getX();
			}	
		}
		return(val);
	}
	public double getMinY(ArrayList<Point2D> points){
		
		double val = points.get(0).getY();
		for(int i=0; i<= points.size(); i++){
			if(val>points.get(i).getY()){
				val = points.get(i).getY();
			}	
		}
		return(val);
	}
	
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
		
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		
		g.setColor(Color.white);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		g.setColor(Color.black);
		for(int i=0;i<drawing.size();i++) {
			//g.fillRect((int) drawing.get(i).getX()-2, (int) drawing.get(i).getY()-2, 4, 4);
			if(i>0) {
				g.setStroke(new BasicStroke(5));
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
