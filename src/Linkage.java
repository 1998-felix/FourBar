import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Linkage {
	private ArrayList<Point2D> points = new ArrayList<Point2D>();
	private ArrayList<Double> lengths = new ArrayList<Double>();;
	/* Points:
	 * 0:CrankXY
	 * 1:RockerXY
	 * 2:CouplerXY
	 * 
	 * Lengths:
	 * 0:CrankL
	 * 1:RockerL
	*/
	private double theta, speed;
	
	public void tick() {
		theta += speed;
	}
	
	public void render(Graphics2D g) {
		g.setColor(Color.black);
		g.setStroke(new BasicStroke(5));
		g.drawRect((int) getCrankXY().getX() + 400, (int) getCrankXY().getY() + 300, (int) (getCrankL()*(Math.sin(theta))), (int) (getCrankL()*(Math.cos(theta))));
		g.drawRect((int) getCrankXY().getX() + 400, (int) getCrankXY().getY() + 300, (int) (getCrankL()*(Math.sin(theta))), (int) (getCrankL()*(Math.cos(theta))));
	}
	
	public Linkage() {
		theta = 0;
		speed = 0.1;
		points.add(new Point2D.Double(0, 0));
		points.add(new Point2D.Double(250, 0));
		points.add(new Point2D.Double(0, 0));
		lengths.add(100.0);
		lengths.add(100.0);
	}
	
	public Point2D getCrankXY() {
		return points.get(0);
	}
	
	public Double getCrankL() {
		return lengths.get(0);
	}

	public Point2D getRockerXY() {
		return points.get(1);
	}

	public Double getRockerL() {
		return lengths.get(1);
	}

	public Point2D getCouplerXY() {
		return points.get(2);
	}
	
	public double getTheta() {
		return theta;
	}
	
}
