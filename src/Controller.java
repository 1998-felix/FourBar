import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Random;

public class Controller extends Canvas implements Runnable {
	private static final long serialVersionUID = -6748925586576993018L;
	public static final int WIDTH = 1024, HEIGHT = 768; // Set the size of the window
	ArrayList<Point2D> drawing = new ArrayList<Point2D>(); // The list of user drawn points
	ArrayList<Point2D> newDraw = new ArrayList<Point2D>(); // The list of user drawn points scaled by time
	ArrayList<Point2D> newLink = new ArrayList<Point2D>(); // The list of linkage drawn points after scaling
	private MouseInput mouseInput; // How we get mouse input
	private KeyInput keyInput; // How we get key input
	public boolean running, showingPath, menu, debug, typed = false;
	private Thread thread;
	private double baseError, tempError, change = 0;
	private Linkage linkage, tempLinkage, successLinkage, tempLinkage2;
	private int done, mouseX0, mouseY0, select = 0;
	Random rand;
	
public ArrayList<Point2D> rescale(ArrayList<Point2D> fourBarPointsTemp, ArrayList<Point2D> drawingPointsTemp) {
		// This method takes two lists of points and scales them to be the same size
		
		// Create temporary lists of points
		ArrayList<Point2D>  fourBarPoints = new ArrayList<Point2D>();
		ArrayList<Point2D>  drawingPoints = new ArrayList<Point2D>();
		fourBarPoints = fourBarPointsTemp;
		drawingPoints = drawingPointsTemp;
		ArrayList<Point2D> outputMe  = new ArrayList<Point2D>();
		
		double scaling = ((getMaxX(fourBarPoints) - getMinX(fourBarPoints)) / (getMaxX(drawingPoints) - getMinX(drawingPoints))); // How far should they be scaled
		
		// Find the user's drawing's smallest X and Y points
		double minX = getMinX(drawingPoints);
		double minY = getMinY(drawingPoints);
		
		// Find the linkage's smallest X and Y points
		double minX2 = getMinX(fourBarPoints);
		double minY2 = getMinY(fourBarPoints);
		
		for(int i=0; i< drawingPoints.size(); i++){
			
			// Append points to the output matrix that are scaled points of the user's drawing
			outputMe.add(new Point2D.Double((drawingPoints.get(i).getX() - minX)*scaling + minX2,(drawingPoints.get(i).getY() - minY)*scaling + minY2));
			
		}
		
		return outputMe; // Returned the scaled user drawing
		
	}

	public double getMinX(ArrayList<Point2D> points){
		// Returns the smallest X coordinate in a list of points
		
		try {
			double val = points.get(0).getX();
			for(int i=0; i< points.size(); i++){
				if(points.get(i)!=null && val>points.get(i).getX()) {
					val = points.get(i).getX(); // Set 'val' to the smaller X value
				}	
			}
			return(val); // Return the smallest X value
		} catch(Exception e) {
			return(0); // If input list has no elements or doesn't exist, return 0
		}
	}
	
	public double getMaxX(ArrayList<Point2D> points){
		// Returns the largest X coordinate in a list of points
		
		try {
			double val = points.get(0).getX();
			for(int i=0;i<points.size();i++) {
				if(points.get(i)!=null && val<points.get(i).getX()) {
					val = points.get(i).getX(); // Set 'val' to the larger X value
				}	
			}
			return(val); // Return the largest X value
		} catch(Exception e) {
			return(0); // If input list has no elements or doesn't exist, return 0
		}
	}
	public double getMinY(ArrayList<Point2D> points){
		// Returns the smallest Y value in a list of points
		
		try {
			double val = points.get(0).getY();
			for(int i=0; i< points.size(); i++){
				if(val>points.get(i).getY()){
					val = points.get(i).getY(); // Set 'val' to the smaller Y value
				}	
			}
			return(val); // Return the smallest Y value
		} catch(Exception e) {
			return(0); // If input list has no elements or doesn't exist, return 0
		}
	}

	public void keyPress(int key) {
		// How we take user key input and change the linkage based on the input
		
		if(select > 0 && change>0 && key==8) { // Backspace key
			change = Math.floor(change/10); // Remove an order of magnitude if backspace is pressed
		} else if(select > 0 && key >= 48 && key <= 57) {
			change = change*10 + key-48; // Add an order of magnitude, then add the key that was pressed
		}
		
		// Change behavior based on what variable is selected
		switch(select) {
			case 1: if(key == 10) { // Enter key
						if(typed) { // If the user pressed any buttons
							linkage.setCrankL(change);
							linkage.clear(); // Set the crank length to the amount specified by the user
						}
						change = 0;
						select = 0;
						typed = false;
					}
					break;
			case 2: if(key == 10) { // Enter key
						if(typed) { // If the user pressed any buttons
							linkage.setRockerL(change);
							linkage.clear(); // Set the rocker length to the amount specified by the user
						}
						change = 0;
						select = 0;
						typed = false;
					}
					break;
			case 3: if(key == 10) { // Enter key
						if(typed) { // If the user pressed any buttons
							linkage.setCouplerL(change);
							linkage.clear(); // Set the coupler length to the amount specified by the user
						}
						change = 0;
						select = 0;
						typed = false;
						
					}
					break;
			case 4: if(key == 10) { // Enter key
						if(typed) { // If the user pressed any buttons
							linkage.setRockerX(change);
							linkage.clear(); // Set the rocker X position to the amount specified by the user
						}
						change = 0;
						select = 0;
						typed = false;
					}
					break;
			case 5: if(key == 10) { // Enter key
						if(typed) { // If the user pressed any buttons
							linkage.setRockerY(change);
							linkage.clear(); // Set the rocker Y position to the amount specified by the user
						}
						change = 0;
						select = 0;
						typed = false;
					}
					break;
			case 6: if(key == 10) { // Enter key
						if(typed) { // If the user pressed any buttons
							linkage.setCouplerX(change);
							linkage.clear(); // Set the coupler X position to the amount specified by the user
						}
						change = 0;
						select = 0;
						typed = false;
					}
					break;
			case 7: if(key == 10) { // Enter key
						if(typed) { // If the user pressed any buttons
							linkage.setCouplerY(change);
							linkage.clear(); // Set the coupler Y position to the amount specified by the user
						}
						change = 0;
						select = 0;
						typed = false;
					}
					break;
		}
		if(!typed) {
			typed = true;
		}
	}
	
	public double howGoodAmINate(ArrayList<Point2D> fourBarPointsTemp, ArrayList<Point2D> drawingPointsTemp) {
		// Cost function (Determining the similarity of the two lists of points provided as arguments)
		
		int subdivisions = 100; // How many divisions should these lists be split into
	
		// Set temporary arrays for the user's drawing and linkage
		ArrayList<Point2D> fourBarPoints = new ArrayList<Point2D>();
		ArrayList<Point2D> drawingPoints = new ArrayList<Point2D>();
		newDraw = new ArrayList<Point2D>();
		newLink = new ArrayList<Point2D>();
		fourBarPoints = fourBarPointsTemp;
		drawingPoints = drawingPointsTemp;
		
		// Rescale the lists of points to be the same size (XY size, not number of entries size)
		fourBarPoints = rescale(fourBarPointsTemp, drawingPointsTemp);
		
		double errorAmount = 0, drawDistance = 0, fourBarDistance = 0, drawPointNum = 0, drawFourBarNum = 0;
		Point2D prevPoint = null;
		Point2D thisPoint = null;
		
		// These two loops determine the length of each drawing
		for(int i=0;i<drawingPoints.size();i++) {
			if(i>0) {
				drawDistance += drawingPoints.get(i).distance(drawingPoints.get(i-1)); // Add the length between the two points
			}
		}
		for(int i=0;i<fourBarPoints.size();i++) {
			if(i>0) {
				fourBarDistance += fourBarPoints.get(i).distance(fourBarPoints.get(i-1)); // Add the length between the two points
			}
		}
		
		// Total length of the drawings divided by the number of subdivisions = length each subdivision should be
		drawPointNum = drawDistance/subdivisions;
		drawFourBarNum = fourBarDistance/subdivisions;
		
		prevPoint = null;
		thisPoint = null;
		
		// Add points defining the border of each subdivision when the correct length has been reached for the user's drawing
		for(int i=0;i<drawingPoints.size();i++) {
			if(i==0) {
				prevPoint = drawingPoints.get(i);
				newDraw.add(prevPoint);
			} else if(drawingPoints.get(i).distance(prevPoint)>=drawPointNum) {
				thisPoint = new Point2D.Double(prevPoint.getX() + (drawingPoints.get(i).getX() - prevPoint.getX()), prevPoint.getY() + (drawingPoints.get(i).getY() - prevPoint.getY()));
				prevPoint = thisPoint;
				newDraw.add(thisPoint);
				i-=1;
			}
		}
		
		prevPoint = null;
		thisPoint = null;
		
		// Add points defining the border of each subdivision when the correct length has been reached for the linkage path
		for(int i=0;i<fourBarPoints.size();i++) {
			if(i==0) {
				prevPoint = fourBarPoints.get(i);
				newLink.add(prevPoint);
			} else if(fourBarPoints.get(i).distance(prevPoint)>=drawFourBarNum) {
				thisPoint = new Point2D.Double(prevPoint.getX() + (fourBarPoints.get(i).getX() - prevPoint.getX()), prevPoint.getY() + (fourBarPoints.get(i).getY() - prevPoint.getY()));
				prevPoint = thisPoint;
				newLink.add(thisPoint);
				i-=1;
			}
		}
		
		// Each list of points has now been divided into the correct number of subdivisions so that we can more easily compare them
		
		// This mess calculates the difference between the two lists of points
		int up = 0;
		int tempDistj = -1;
		for(int i=0;i<newDraw.size();i++) {
			if(i==0) {
				double tempDist = 1000000;
				for(int j=0;j<newLink.size();j++) {
					if(newDraw.get(i).distance(newLink.get(j)) < tempDist) {
						tempDist = newDraw.get(i).distance(newLink.get(j));
						tempDistj = j; // Find the closest point on the linkage path to the start of the user's drawing's path
					}
				}
			} else {
				if(i >= newLink.size()-1) {
					errorAmount += newDraw.get(i).distance(newLink.get(newLink.size()-1)); // Add error equal to the distance between the points
				} else if(up == 0) {
					if(tempDistj == newLink.size()-1) {
						if(newLink.get(0).distance(newDraw.get(i+1)) < newLink.get(newLink.size() - 2).distance(newDraw.get(i+1))) {
							up = 1; // Should the index move up or down (moving up)
						} else {
							up = -1; // Move the index down
						}
					} else if(tempDistj == 0) {
						if(newLink.get(1).distance(newDraw.get(i+1)) < newLink.get(newLink.size() - 1).distance(newDraw.get(i+1))) {
							up = 1; // Should the index move up or down (moving up)
						} else {
							up = -1; // Move the index down
						}
					} else if(newLink.get(tempDistj + 1).distance(newDraw.get(i+1)) < newLink.get(tempDistj - 1).distance(newDraw.get(i+1))) {
						up = 1; // Should the index move up or down (moving up)
					} else {
						up = -1; // Move the index down
					}
				} else if(up == 1) {
					if(i + tempDistj >= newLink.size()) {
						errorAmount += newDraw.get(i).distance(newLink.get(tempDistj + i - newLink.size())); // Add error equal to the distance between the points
					} else {
						errorAmount += newDraw.get(i).distance(newLink.get(tempDistj + i - 1)); // Add error equal to the distance between the points
					}
				} else {
					if(tempDistj - i < 0) {
						errorAmount += newDraw.get(i).distance(newLink.get(newLink.size() + tempDistj - i)); // Add error equal to the distance between the points
					} else {
						errorAmount += newDraw.get(i).distance(newLink.get(tempDistj - i)); // Add error equal to the distance between the points
					}
				}
			}
		}
		if(newLink.size() > newDraw.size()) {
			for(int i=0;i<newLink.size()-newDraw.size();i++) {
				errorAmount += newLink.get(i + newDraw.size()-1).distance(newDraw.get(newDraw.size()-1)); // Add error equal to the distance between the points
			}
		}
		return errorAmount; // Return the total error amount
	}
	
	public void drag(int mouseX, int mouseY) {
		// Method called when the user drags the mouse
		
		if(menu) { // If in the menu, the user is drawing a curve
			drawing.add(new Point2D.Double(mouseX, mouseY)); // Add a point to the user's drawing
		} else { // If not in the menu, the user is moving the linkage
			
			//Move the linkage
			linkage.setOffsetX(mouseX-mouseX0);
			linkage.setOffsetY(mouseY-mouseY0);
			
		}
		
		mouseX0 = mouseX;
		mouseY0 = mouseY;
	}
	
	public Linkage randomLinkage() {
		
		// Generate a random linkage and make sure it is a real linkage
		boolean isDone = false;
		while(!isDone) {
			
			// Create a new linkage and assign it random values within these ranges
			linkage = new Linkage(this);
			linkage.setCouplerL(rand.nextInt(250)+10);
			linkage.setCouplerX(rand.nextInt(150));
			linkage.setCouplerY(rand.nextInt(150));
			linkage.setCrankL(rand.nextInt(150)+10);
			linkage.setRockerL(rand.nextInt(150)+10);
			linkage.setRockerX(rand.nextInt(200)+50);
			linkage.setRockerY(rand.nextInt(100)+50);
			if(linkage.isRael()) {
				isDone = true;
			}
		}
		
		// If the linkage is a real linkage, return it
		return linkage;
	}
	
	public void click(int mouseX, int mouseY) {
		// Method called when mouse is clicked
		
		mouseX0 = mouseX;
		mouseY0 = mouseY;
		if(menu && mouseX > WIDTH-300 && mouseX < WIDTH && mouseY > HEIGHT-200 && mouseY < HEIGHT) { // If the user has clicked the next button
			menu = false;
			double smallDelta = 100000000;
			
			// Generate 200 random linkages and return the one nearest to the user's drawing
			for(int i=0;i<200;i++) {				
				tempLinkage2 = randomLinkage();
				tempError = howGoodAmINate(tempLinkage2.getPath(), drawing);
				if(tempError<smallDelta) { // If the error of this linkage is the smallest so far, it is the success linkage
					smallDelta = tempError;
					successLinkage = tempLinkage2;
					if(smallDelta < 25000) { // If the error is below this margin, the current linkage is relatively close, so return it
						break;
					}
				}
				//System.out.println(howGoodAmINate(tempLinkage2.getPath(), drawing));
				try {
					//thread.sleep(10);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			linkage = successLinkage;
			linkage.getPath();
		} else if(!menu && mouseX >= WIDTH-18 && mouseX <= WIDTH && mouseY >= 0 && mouseY <= 14) { // If the user is clicking on the debug button
			select = 0;
			debug = !debug; // Toggle the debug setting
		} else if(!menu) {
			if(mouseX > WIDTH-64 && mouseX < WIDTH-32) { // If the user is hitting the plus or minus button to zoom the linkage
				select = 0;
				if(mouseY > HEIGHT/2 - 33 && mouseY < HEIGHT/2 - 1) {
					linkage.setScale(1.33333333333333); // Zoom by a factor of 4/3
				} else if(mouseY > HEIGHT/2 && mouseY < HEIGHT/2 + 32) {
					linkage.setScale(0.75); // Zoom by a factor of 3/4
				} else if(mouseY > HEIGHT/2 - 84 && mouseY < HEIGHT/2 - 52) {
					linkage.speed *= 1.333333333; // Change speed by a factor of 4/3
				} else if(mouseY > HEIGHT/2 + 50 && mouseY < HEIGHT/2 + 82) {
					linkage.speed *= 0.75; // Change speed by a factor of 3/4
				}
			}
			if(mouseY > 32 && mouseY < 64) { // The user is changing linkage variables
				if(mouseX>20 && mouseX<132) {
					select = 1;
					change = linkage.getCrankL();
					typed = false;
				} else if(mouseX>164 && mouseX<276) {
					select = 2;
					change = linkage.getRockerL();
					typed = false;
				} else if(mouseX>308 && mouseX<420) {
					select = 3;
					change = linkage.getCouplerL();
					typed = false;
				} else if(mouseX>452 && mouseX<564) {
					select = 4;
					change = linkage.getRockerXY().getX();
					typed = false;
				} else if(mouseX>596 && mouseX<708) {
					select = 5;
					change = linkage.getRockerXY().getY();
					typed = false;
				} else if(mouseX>740 && mouseX<852) {
					select = 6;
					change = linkage.getCouplerXY().getX();
					typed = false;
				} else if(mouseX>884 && mouseX<996) {
					select = 7;
					change = linkage.getCouplerXY().getY();
					typed = false;
				} else {
					select = 0; // Deselect all linkage variables
				}
			} else {
				
				//Change behavior depending on which linkage variable the user is changing
				switch(select) {
					case 1: if(change != linkage.getCrankL()) {
								linkage.setCrankL(change);
								linkage.clear();
							}
							break;
					case 2: if(change != linkage.getRockerL()) {
								linkage.setRockerL(change);
								linkage.clear();
							}
							break;
					case 3: if(change != linkage.getCouplerL()) {
								linkage.setCouplerL(change);
								linkage.clear();
							}
							break;
					case 4: if(change != linkage.getRockerXY().getX()) {
								linkage.setRockerX(change);
								linkage.clear();
							}
							break;
					case 5: if(change != linkage.getRockerXY().getY()) {
								linkage.setRockerY(change);
								linkage.clear();
							}
							break;
					case 6: if(change != linkage.getCouplerXY().getX()) {
								linkage.setCouplerX(change);
								linkage.clear();
							}
							break;
					case 7: if(change != linkage.getCouplerXY().getY()) {
								linkage.setCouplerY(change);
								linkage.clear();
							}
							break;
				}
				change = 0;
				select = 0;
			}
		}
	}


	
	public void tick() { // Every tick in the simulation, perform a linkage tick as well
		if(showingPath && !menu) {
			linkage.tick();
		}
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs==null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		
		// Set the background to be white by drawing a large white rectangle
		g.setColor(Color.white);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		if(menu) { // If we are in the first menu
			g.setColor(Color.black);
			for(int i=0;i<drawing.size();i++) {
				if(i>0) {
					try { // Show the user's drawing
						g.setStroke(new BasicStroke(5));
						g.drawLine((int) drawing.get(i).getX(), (int) drawing.get(i).getY(), (int) drawing.get(i-1).getX(), (int) drawing.get(i-1).getY());
					} catch(Exception e) {
						
					}
				}
			}
			
			// Draw all of the buttons for the menu
			g.setStroke(new BasicStroke(5));
			g.setColor(Color.darkGray);
			g.fillRect(WIDTH-300, HEIGHT-200, 301, 201);
			g.fillRect(WIDTH/4, 0, WIDTH/2, 50);
			Polygon triangle1 = new Polygon();
			triangle1.addPoint(WIDTH/4 - 50, 0);
			triangle1.addPoint(WIDTH/4, 0);
			triangle1.addPoint(WIDTH/4, 50);
			Polygon triangle2 = new Polygon();
			triangle2.addPoint(3*WIDTH/4 + 50, 0);
			triangle2.addPoint(3*WIDTH/4, 0);
			triangle2.addPoint(3*WIDTH/4, 50);
			g.fill(triangle1);
			g.fill(triangle2);
			g.setColor(Color.red);
			g.drawRect(WIDTH-300, HEIGHT-200, 301, 201);
			g.setStroke(new BasicStroke(1));
			g.setColor(Color.white);
			Font font = new Font("Serif", Font.PLAIN, 32);
			g.setFont(font);
			g.drawString("Draw A Curve!", WIDTH/4 + 160, 32);
			g.drawString("Next", WIDTH - 180, HEIGHT - 105);
		} else { // If we are not in the first menu
			
			linkage.render(g); // Call the linkage render method
			
			// Draw all of the buttons outside of the menu
			g.setColor(Color.darkGray);
			g.fillRect(WIDTH - 64, HEIGHT/2 - 33, 32, 32);
			g.fillRect(WIDTH - 64, HEIGHT/2, 32, 32);
			g.fillRect(WIDTH-18, 0, 18, 14);
			Polygon triangle3 = new Polygon();
			triangle3.addPoint(WIDTH - 48, HEIGHT/2 - 84);
			triangle3.addPoint(WIDTH - 64, HEIGHT/2 - 52);
			triangle3.addPoint(WIDTH - 48, HEIGHT/2 - 64);
			Polygon triangle4 = new Polygon();
			triangle4.addPoint(WIDTH - 48, HEIGHT/2 - 84);
			triangle4.addPoint(WIDTH - 32, HEIGHT/2 - 52);
			triangle4.addPoint(WIDTH - 48, HEIGHT/2 - 64);
			g.fill(triangle3);
			g.fill(triangle4);
			Polygon triangle5 = new Polygon();
			triangle5.addPoint(WIDTH - 48, HEIGHT/2 + 82);
			triangle5.addPoint(WIDTH - 64, HEIGHT/2 + 50);
			triangle5.addPoint(WIDTH - 48, HEIGHT/2 + 62);
			Polygon triangle6 = new Polygon();
			triangle6.addPoint(WIDTH - 48, HEIGHT/2 + 82);
			triangle6.addPoint(WIDTH - 32, HEIGHT/2 + 50);
			triangle6.addPoint(WIDTH - 48, HEIGHT/2 + 62);
			g.fill(triangle5);
			g.fill(triangle6);
			//g.fillRect(WIDTH - 64, HEIGHT/2 + 50, 32, 32);
			g.setColor(Color.white);
			g.fillRect(WIDTH - 52, HEIGHT/2 - 30, 8, 26);
			g.fillRect(WIDTH - 61, HEIGHT/2 - 21, 26, 8);
			g.fillRect(WIDTH - 61, HEIGHT/2 + 12, 26, 8);
			if(!linkage.isRael()) { // If the linkage is undefined, tell the user that there are undefined points
				g.setColor(Color.red);
				Font font2 = new Font("Serif", Font.PLAIN, 40);
				g.setFont(font2);
				g.drawString("There are undefined points", WIDTH/2 - 200, HEIGHT - 70);
			}
			g.setColor(Color.black);
			g.drawRect(20, 32, 112, 32);
			g.drawRect(164, 32, 112, 32);
			g.drawRect(308, 32, 112, 32);
			g.drawRect(452, 32, 112, 32);
			g.drawRect(596, 32, 112, 32);
			g.drawRect(740, 32, 112, 32);
			g.drawRect(884, 32, 112, 32);
			Font font1 = new Font("Serif", Font.PLAIN, 20);
			g.setFont(font1);
			g.drawString("Crank Length", 22, 84);
			g.drawString("Rocker Length", 164, 84);
			g.drawString("Coupler Length", 304, 84);
			g.drawString("Rocker Xpos", 456, 84);
			g.drawString("Rocker Ypos", 598, 84);
			g.drawString("X Offset", 760, 84);
			g.drawString("Y Offset", 900, 84);
			Font font = new Font("Serif", Font.PLAIN, 24);
			g.setFont(font);
			
			// Depending on what the user is editing, highlight a different selection
			switch(select) {
				case 0: g.drawString(Double.toString(linkage.getCrankL()), 30, 58);
						g.drawString(Double.toString(linkage.getRockerL()), 174, 58);
						g.drawString(Double.toString(linkage.getCouplerL()), 318, 58);
						g.drawString(Double.toString(linkage.getRockerXY().getX()), 462, 58);
						g.drawString(Double.toString(linkage.getRockerXY().getY()), 606, 58);
						g.drawString(Double.toString(linkage.getCouplerXY().getX()), 750, 58);
						g.drawString(Double.toString(linkage.getCouplerXY().getY()), 894, 58);
						break;
				case 1: g.setColor(new Color(0, 0, 255, 80));
						g.fillRect(23, 35, 107, 27);
						g.drawString(Double.toString(change), 30, 58);
						g.drawString(Double.toString(linkage.getRockerL()), 174, 58);
						g.drawString(Double.toString(linkage.getCouplerL()), 318, 58);
						g.drawString(Double.toString(linkage.getRockerXY().getX()), 462, 58);
						g.drawString(Double.toString(linkage.getRockerXY().getY()), 606, 58);
						g.drawString(Double.toString(linkage.getCouplerXY().getX()), 750, 58);
						g.drawString(Double.toString(linkage.getCouplerXY().getY()), 894, 58);
						break;
				case 2: g.setColor(new Color(0, 0, 255, 80));
						g.fillRect(167, 35, 107, 27);
						g.drawString(Double.toString(linkage.getCrankL()), 30, 58);
						g.drawString(Double.toString(change), 174, 58);
						g.drawString(Double.toString(linkage.getCouplerL()), 318, 58);
						g.drawString(Double.toString(linkage.getRockerXY().getX()), 462, 58);
						g.drawString(Double.toString(linkage.getRockerXY().getY()), 606, 58);
						g.drawString(Double.toString(linkage.getCouplerXY().getX()), 750, 58);
						g.drawString(Double.toString(linkage.getCouplerXY().getY()), 894, 58);
						break;
				case 3: g.setColor(new Color(0, 0, 255, 80));
						g.fillRect(311, 35, 107, 27);
						g.drawString(Double.toString(linkage.getCrankL()), 30, 58);
						g.drawString(Double.toString(linkage.getRockerL()), 174, 58);
						g.drawString(Double.toString(change), 318, 58);
						g.drawString(Double.toString(linkage.getRockerXY().getX()), 462, 58);
						g.drawString(Double.toString(linkage.getRockerXY().getY()), 606, 58);
						g.drawString(Double.toString(linkage.getCouplerXY().getX()), 750, 58);
						g.drawString(Double.toString(linkage.getCouplerXY().getY()), 894, 58);
						break;
				case 4: g.setColor(new Color(0, 0, 255, 80));
						g.fillRect(455, 35, 107, 27);
						g.drawString(Double.toString(linkage.getCrankL()), 30, 58);
						g.drawString(Double.toString(linkage.getRockerL()), 174, 58);
						g.drawString(Double.toString(linkage.getCouplerL()), 318, 58);
						g.drawString(Double.toString(change), 462, 58);
						g.drawString(Double.toString(linkage.getRockerXY().getY()), 606, 58);
						g.drawString(Double.toString(linkage.getCouplerXY().getX()), 750, 58);
						g.drawString(Double.toString(linkage.getCouplerXY().getY()), 894, 58);
						break;
				case 5: g.setColor(new Color(0, 0, 255, 80));
						g.fillRect(599, 35, 107, 27);
						g.drawString(Double.toString(linkage.getCrankL()), 30, 58);
						g.drawString(Double.toString(linkage.getRockerL()), 174, 58);
						g.drawString(Double.toString(linkage.getCouplerL()), 318, 58);
						g.drawString(Double.toString(linkage.getRockerXY().getX()), 462, 58);
						g.drawString(Double.toString(change), 606, 58);
						g.drawString(Double.toString(linkage.getCouplerXY().getX()), 750, 58);
						g.drawString(Double.toString(linkage.getCouplerXY().getY()), 894, 58);
						break;
				case 6: g.setColor(new Color(0, 0, 255, 80));
						g.fillRect(743, 35, 107, 27);
						g.drawString(Double.toString(linkage.getCrankL()), 30, 58);
						g.drawString(Double.toString(linkage.getRockerL()), 174, 58);
						g.drawString(Double.toString(linkage.getCouplerL()), 318, 58);
						g.drawString(Double.toString(linkage.getRockerXY().getX()), 462, 58);
						g.drawString(Double.toString(linkage.getRockerXY().getY()), 606, 58);
						g.drawString(Double.toString(change), 750, 58);
						g.drawString(Double.toString(linkage.getCouplerXY().getY()), 894, 58);
						break;
				case 7: g.setColor(new Color(0, 0, 255, 80));
						g.fillRect(887, 35, 107, 27);
						g.drawString(Double.toString(linkage.getCrankL()), 30, 58);
						g.drawString(Double.toString(linkage.getRockerL()), 174, 58);
						g.drawString(Double.toString(linkage.getCouplerL()), 318, 58);
						g.drawString(Double.toString(linkage.getRockerXY().getX()), 462, 58);
						g.drawString(Double.toString(linkage.getRockerXY().getY()), 606, 58);
						g.drawString(Double.toString(linkage.getCouplerXY().getX()), 750, 58);
						g.drawString(Double.toString(change), 894, 58);
						break;
			}
		}
		
		g.dispose();
		bs.show(); // Show what has been drawn using Graphics
	}
	
	public synchronized void start() { // Start the thread and main loop
		thread = new Thread(this);
		thread.start();
		running = true;
	}
	
	public synchronized void stop() { // Stop the thread and main loop
		try {
			thread.join();
			running = false;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() { // Main loop
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0; // amount of ticks being how many ticks per second
        double ns = 1000000000 / amountOfTicks; // nanoseconds per tick
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(running) {
        	long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >=1) { // If it has been at least one tick length in nanoseconds, tick and remove that from delta
            	tick();
            	delta--;
            }
            render();
            frames++;
            if(System.currentTimeMillis() - timer > 1000) { // After 1000 ticks, print the FPS
            	timer += 1000;
            	//System.out.println("FPS: "+ frames);
            	frames = 0;
            }
        }
        stop();
    }
	
	public Controller() {
		
		new Window(WIDTH, HEIGHT, "Draw", this); // Create the window
		
		// Setup key and mouse inputs
		addKeyListener(keyInput);
		keyInput = new KeyInput(this);
		mouseInput = new MouseInput(this);
		addMouseListener(mouseInput);
		addKeyListener(keyInput);
		addMouseMotionListener(mouseInput);
		
		rand = new Random();
		
		menu = true; // Start in the menu
		
		debug = false; // Debug menu default position
		
		showingPath = true; // Should the linkage animate
		
		linkage = new Linkage(this); // Create the first linkage
	}
	
	public static void main(String[] args) { // At the start, create a controller object
		new Controller();
	}
}
