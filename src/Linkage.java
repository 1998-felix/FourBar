import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Linkage {
	private ArrayList<Point2D> points = new ArrayList<Point2D>();
	private ArrayList<Double> lengths = new ArrayList<Double>();
	private ArrayList<Point2D> path1 = new ArrayList<Point2D>(); // Blue path, path with positive initial condition
	private ArrayList<Point2D> path2 = new ArrayList<Point2D>(); // Red path, path with negative initial condition
	private double crankTheta, rockerTheta, d, a, h, x0, y0, x1, y1, m1, m2, m3, x2, x3, y2, y3, angle1, angle2, tempdist, scale = 1;
	private Point2D p, dPoint, p0, p1, p2, p3, p4, p5;
	private Controller control;
	private int xOffset = 400, yOffset = 400;
	private boolean done = false, broken = false;
	public double speed;
	
	public void tick() {
		
		crankTheta += speed; // Every tick, move the crank forward by the speed
		
		// Calculate the intersection of the two circles that define the linkage (
		dPoint = new Point2D.Double(getCrankXY().getX() + getCrankL()*(Math.sin(Math.toRadians(crankTheta))), getCrankXY().getY() + getCrankL()*(Math.cos(Math.toRadians(crankTheta))));
		d = dPoint.distance(getRockerXY());
		a = ((getCouplerL()*getCouplerL()) - (getRockerL()*getRockerL()) + (d*d))/(2*d);
		h = (Math.sqrt(Math.abs((getCouplerL()*getCouplerL() - a*a))));
		p = new Point2D.Double((getRockerXY().getX() - dPoint.getX())*(a/d) + dPoint.getX(), (getRockerXY().getY() - dPoint.getY())*(a/d) + dPoint.getY());
		p0 = new Point2D.Double(p.getX() + h*(getRockerXY().getY() - dPoint.getY())/d, p.getY() - h*(getRockerXY().getX() - dPoint.getX())/d);
		p1 = new Point2D.Double(p.getX() - h*(getRockerXY().getY() - dPoint.getY())/d, p.getY() + h*(getRockerXY().getX() - dPoint.getX())/d);
		m1 = (dPoint.getY() - getCrankXY().getY())/(dPoint.getX() - getCrankXY().getX());
		m2 = (p0.getY() - dPoint.getY())/(p0.getX() - dPoint.getX());
		m3 = (p1.getY() - dPoint.getY())/(p1.getX() - dPoint.getX());
		angle1 = Math.atan2(dPoint.getY() - p0.getY(), dPoint.getX() - p0.getX());
		angle2 = Math.atan2(dPoint.getY() - p1.getY(), dPoint.getX() - p1.getX());
		p2 = new Point2D.Double(dPoint.getX() + (p0.getX() - dPoint.getX())/2 - getCouplerXY().getY()*Math.sin(angle1) + getCouplerXY().getX()*Math.cos(angle1), dPoint.getY() + (p0.getY() - dPoint.getY())/2 + getCouplerXY().getY()*Math.cos(angle1) + getCouplerXY().getX()*Math.sin(angle1));
		p3 = new Point2D.Double(dPoint.getX() + (p1.getX() - dPoint.getX())/2 - getCouplerXY().getY()*Math.sin(angle2) + getCouplerXY().getX()*Math.cos(angle2), dPoint.getY() + (p1.getY() - dPoint.getY())/2 + getCouplerXY().getY()*Math.cos(angle2) + getCouplerXY().getX()*Math.sin(angle2));
		p4 = new Point2D.Double(dPoint.getX() + (p0.getX() - dPoint.getX())/2, dPoint.getY() + (p0.getY() - dPoint.getY())/2);
		p5 = new Point2D.Double(dPoint.getX() + (p1.getX() - dPoint.getX())/2, dPoint.getY() + (p1.getY() - dPoint.getY())/2);
		
		if(p0.distance(getRockerXY())<getRockerL()+5) { // Only add points when the linkage is defined
			try {
				
				// Add points to the linkage drawing
				path1.add(new Point2D.Double(p2.getX(), p2.getY()));
				path2.add(new Point2D.Double(p3.getX(), p3.getY()));
				
			} catch(Exception e) {
				
			}
		} else {
			broken = true; // If broken, this linkage is non-continuous and is undefined at least at one point
		}
		if(!done) {
			scale = 100/lengths.get(0); // Scale the linkage by the size of the crank so that the user can see the entire linkage
		}
		//System.out.println(h);
	}
	
	public void render(Graphics2D g) { // What the linkage draws on screen
		try {
			
			// Draw the basic linkage
			g.setColor(Color.black);
			g.setStroke(new BasicStroke(5));
			g.drawLine((int) (getCrankXY().getX()*scale + xOffset - getCrankXY().getX()), (int) (getCrankXY().getY()*scale + yOffset - getCrankXY().getY()), (int) (getCrankXY().getX()*scale + xOffset + scale*getCrankL()*(Math.sin(Math.toRadians(crankTheta))) - getCrankXY().getX()), (int) (getCrankXY().getY()*scale + yOffset + scale*getCrankL()*(Math.cos(Math.toRadians(crankTheta))) - getCrankXY().getY()));
			g.setColor(Color.blue);
			g.drawLine((int) (p0.getX()*scale + xOffset - getCrankXY().getX()), (int) (p0.getY()*scale + yOffset - getCrankXY().getY()), (int) (getRockerXY().getX()*scale + xOffset - getCrankXY().getX()), (int) (getRockerXY().getY()*scale + yOffset - getCrankXY().getY()));
			g.drawLine((int) (dPoint.getX()*scale + xOffset - getCrankXY().getX()), (int) (dPoint.getY()*scale + yOffset - getCrankXY().getY()), (int) (p0.getX()*scale + xOffset - getCrankXY().getX()), (int) (p0.getY()*scale + yOffset - getCrankXY().getY()));
			g.setColor(Color.black);
			g.drawRect((int) (p2.getX()*scale + xOffset - getCrankXY().getX()), (int) (p2.getY()*scale + yOffset - getCrankXY().getY()), 1, 1);
			g.drawRect((int) (p4.getX()*scale + xOffset - getCrankXY().getX()), (int) (p4.getY()*scale + yOffset - getCrankXY().getY()), 1, 1);
			g.setColor(Color.black);
			
			if(control.debug) { // If the debug screen is on, draw the circles as well as important points
				g.setColor(Color.red);
				g.drawLine((int) (p1.getX()*scale + xOffset - getCrankXY().getX()), (int) (p1.getY()*scale + yOffset - getCrankXY().getY()), (int) (getRockerXY().getX()*scale + xOffset - getCrankXY().getX()), (int) (getRockerXY().getY()*scale + yOffset - getCrankXY().getY()));
				g.drawLine((int) (dPoint.getX()*scale + xOffset - getCrankXY().getX()), (int) (dPoint.getY()*scale + yOffset - getCrankXY().getY()), (int) (p1.getX()*scale + xOffset - getCrankXY().getX()), (int) (p1.getY()*scale + yOffset - getCrankXY().getY()));
				g.setColor(Color.black);
				g.drawRect((int) (p.getX()*scale + xOffset - getCrankXY().getX()), (int) (p.getY()*scale + yOffset - getCrankXY().getY()), 1, 1);
				g.drawRect((int) (p0.getX()*scale + xOffset - getCrankXY().getX()), (int) (p0.getY()*scale + yOffset - getCrankXY().getY()), 1, 1);
				g.drawRect((int) (p1.getX()*scale + xOffset - getCrankXY().getX()), (int) (p1.getY()*scale + yOffset - getCrankXY().getY()), 1, 1);
				g.drawRect((int) (p3.getX()*scale + xOffset - getCrankXY().getX()), (int) (p3.getY()*scale - getCrankXY().getY()) + yOffset, 1, 1);
				g.drawRect((int) (p5.getX()*scale + xOffset - getCrankXY().getX()), (int) (p5.getY()*scale - getCrankXY().getY()) + yOffset, 1, 1);
				g.setStroke(new BasicStroke(1));
				g.setColor(Color.black);
				g.drawOval((int) (dPoint.getX()*scale + xOffset - Math.round(scale*getCouplerL()) - getCrankXY().getX()), (int) (dPoint.getY()*scale + yOffset - Math.round(scale*getCouplerL()) - getCrankXY().getY()), (int) Math.round(scale*getCouplerL()*2), (int) Math.round(scale*getCouplerL()*2));
				g.drawOval((int) (getRockerXY().getX()*scale + xOffset - Math.round(scale*getRockerL()) - getCrankXY().getX()), (int) (getRockerXY().getY()*scale + yOffset - Math.round(scale*getRockerL()) - getCrankXY().getY()), (int) Math.round(scale*getRockerL()*2), (int) Math.round(scale*getRockerL()*2));
				for(int i=0;i<path2.size();i++) { // Draw the second path
					g.drawRect((int) (path2.get(i).getX()*scale + xOffset - getCrankXY().getX()), (int) (path2.get(i).getY()*scale + yOffset - getCrankXY().getY()), 1, 1);
					if(i>0) {
						g.drawLine((int) (path2.get(i).getX()*scale + xOffset - getCrankXY().getX()), (int) (path2.get(i).getY()*scale + yOffset - getCrankXY().getY()), (int) (path2.get(i-1).getX()*scale + xOffset - getCrankXY().getX()), (int) (path2.get(i-1).getY()*scale + yOffset - getCrankXY().getY()));
					}
				}
			}
			
			g.setStroke(new BasicStroke(1));
			for(int i=0;i<path1.size();i++) {
				g.drawRect((int) (path1.get(i).getX()*scale + xOffset - getCrankXY().getX()), (int) (path1.get(i).getY()*scale + yOffset - getCrankXY().getY()), 1, 1);
				if(i>0) { // Draw the path
					g.drawLine((int) (path1.get(i).getX()*scale + xOffset - getCrankXY().getX()), (int) (path1.get(i).getY()*scale + yOffset - getCrankXY().getY()), (int) (path1.get(i-1).getX()*scale + xOffset - getCrankXY().getX()), (int) (path1.get(i-1).getY()*scale + yOffset - getCrankXY().getY()));
				}
			}
		}
		catch(Exception e) {
			
		}
	}
	
	public void clear() { // Clear the linkage path
		path1 = new ArrayList<Point2D>();
		path2 = new ArrayList<Point2D>();
	}
	
	public Linkage(Controller control) { // Linkage constructor
		this.control = control;
		crankTheta = 90;
		rockerTheta = 0;
		speed = -2; // Degrees per tick
		scale = 1;
		lengths.add(180.0); //Crank Length
		lengths.add(260.0); //Rocker Length
		lengths.add(475.0); //Coupler Length
		points.add(new Point2D.Double(90, 0)); //Crank Initial
		points.add(new Point2D.Double(643, 0)); //Rocker Initial
		points.add(new Point2D.Double(0, 0)); //Coupler Offset
	}
	
	public boolean isRael() { // Determines if the linkage is real or not (defined at all points)
		// According to Grashof's Law, the largest + smallest lengths < smaller middle + larger middle lengths of the four bars
		int shortesti = -1;
		int longesti = -1;
		double shortest = 10000000;
		double longest = 0;
		double tempnum1 = 0;
		
		// Find the shortest and longest bars of the four bars
		for(int i=0;i<lengths.size();i++) {
			double tempnum = lengths.get(i);
			if(tempnum < shortest) {
				shortest = tempnum;
				shortesti = i;
			}
			if(tempnum > longest) {
				longest = tempnum;
				longesti = i;
			}
		}	
		if(Math.sqrt(Math.abs((points.get(1).getX()-points.get(0).getX())*(points.get(1).getX()-points.get(0).getX()) + (points.get(1).getY()-points.get(0).getY())*(points.get(1).getY()-points.get(0).getY())))<shortest) {
			shortest = Math.abs((points.get(1).getX()-points.get(0).getX())*(points.get(1).getX()-points.get(0).getX()) + (points.get(1).getY()-points.get(0).getY())*(points.get(1).getY()-points.get(0).getY()));
			shortesti = -1;
		} else if(Math.sqrt(Math.abs((points.get(1).getX()-points.get(0).getX())*(points.get(1).getX()-points.get(0).getX()) + (points.get(1).getY()-points.get(0).getY())*(points.get(1).getY()-points.get(0).getY())))>longest) {
			longest = Math.sqrt(Math.abs((points.get(1).getX()-points.get(0).getX())*(points.get(1).getX()-points.get(0).getX()) + (points.get(1).getY()-points.get(0).getY())*(points.get(1).getY()-points.get(0).getY())));
			longesti = -1;
		}
		
		// Get the combined lengths of the middle two bars
		for(int i=0;i<lengths.size();i++) {
			if(i != shortesti && i != longesti) {
				tempnum1 += lengths.get(i);
			}
		}
		
		// If this is false, this is an undefined linkage
		if(!(getCouplerL() - getCrankL() < getRockerXY().getX() + getRockerL())) {
			return false;
		}
		
		// Get the path of the linkage and check if the linkage is broken at any points. If so, it is not a real linkage
		getPath();
		if(broken) {
			return false;
		}
		try {
			path1.subList((int) (360/-speed), path1.size()).clear();
			path2.subList((int) (360/-speed), path2.size()).clear();
		} catch(Exception e) {
			
		}
		
		//Check the lengths of these bars to make sure they satisfy Grashof's Law
		return((shortest+longest)<(tempnum1));
	}
	
	// Getters and Setters for the 7 variables, as well as some others like scale and path
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
	
	public Double getCouplerL() {
		return lengths.get(2);
	}

	public Point2D getCouplerXY() {
		return points.get(2);
	}
	
	public void setCrankX(double newL) {
		points.set(0, new Point2D.Double(newL, points.get(0).getY()));
	}
	
	public void setCrankY(double newL) {
		points.set(0, new Point2D.Double(points.get(0).getX(), newL));
	}
	
	public void setCrankL(double newL) {
		lengths.set(0, Math.abs(newL));
	}

	public void setRockerX(double newL) {
		points.set(1, new Point2D.Double(newL, points.get(1).getY()));
	}
	
	public void setRockerY(double newL) {
		points.set(1, new Point2D.Double(points.get(1).getX(), newL));
	}

	public void setRockerL(double newL) {
		lengths.set(1, Math.abs(newL));
	}
	
	public void setCouplerL(double newL) {
		lengths.set(2, Math.abs(newL));
	}
	
	public void setScale(double mult) {
		scale*=mult;
	}
	
	public void setOffsetX(double move) {
		xOffset += move;
	}
	
	public void setOffsetY(double move) {
		yOffset += move;
	}

	public void setCouplerX(double newL) {
		points.set(2, new Point2D.Double(newL, points.get(2).getY()));
	}
	
	public void setCouplerY(double newL) {
		points.set(2, new Point2D.Double(points.get(2).getX(), newL));
	}
	
	public double getCrankTheta() {
		return crankTheta;
	}
	
	public ArrayList<Point2D> getPath() {
		for(double i=0;i<=360;i+=Math.abs(speed)) {
			done = true;
			tick();
		}
		if(path1.size()>0) {
			return path1;
		} else {
			path1.add(getCouplerXY());
			return path1;
		}
	}
}
