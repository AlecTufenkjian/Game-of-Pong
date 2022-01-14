package ppPackage;
import static ppPackage.ppSimParams.*;

import java.awt.Color;

import acm.graphics.GPoint;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;

public class ppTable {
	
	GraphicsProgram GProgram;
	
	/***
	 * ppTable Contructor
	 * @param GProgram - a reference to the ppSim class used to manage the display
	 */
	public ppTable(GraphicsProgram GProgram) {
		this.GProgram = GProgram;
		this.GProgram.resize(WIDTH,HEIGHT+OFFSET);    // initialize window size
		
    	drawGroundLine();
    	
    	
	}
	
	/***
	 * Converts world P to screen p to
	 * @param P - World Coordinates
	 * @return - Screen Coordinates
	 */
	public GPoint W2S (GPoint P) {  
		return new GPoint((P.getX()-Xmin)*Xs,ymax-(P.getY()-Ymin)*Ys);
	}
	
	/***
	 * Converts screen p to world P
	 * @param p - Screen Coordinates
	 * @return - World Coordinates
	 */
	public GPoint S2W (GPoint p) {
		double ScrX = p.getX();
		double ScrY = p.getY();
		double WorldX = ScrX / Xs + Xmin;
		double WorldY = (ymax - ScrY) / Ys + Ymin;
		return new GPoint(WorldX, WorldY);
	}
	
	/***
	 * Erase all objects on the display (except buttons) and draws a new ground plane
	 */
	public void newScreen() {
		GProgram.removeAll();
		drawGroundLine();
	}
	
	/***
	 * Creates and adds the ground plane
	 */
	public void drawGroundLine() {
    	GRect gPlane = new GRect(0,HEIGHT,WIDTH+OFFSET,3);	// A thick line HEIGHT pixels down from the top
    	gPlane.setColor(Color.BLACK);
    	gPlane.setFilled(true);
    	GProgram.add(gPlane);
	}
	
}
