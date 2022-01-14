package ppPackage;

import static ppPackage.ppSimParams.*;

import java.awt.Color;

import acm.graphics.GLine;
import acm.graphics.GOval;
import acm.graphics.GPoint;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;

public class ppPaddle extends Thread{
	
	double X;
	double Y;
	ppTable myTable;
	GRect myPaddle;
	GraphicsProgram GProgram;
	double Vx;
	double Vy;
	Color myColor;
	
	/***
	 * ppPaddle constructor
	 * @param X - paddle X positon
	 * @param Y - paddle Y positon
	 * @param myColor - paddle color
	 * @param myTable - pingpong table
	 * @param GProgram - graphics program
	 */
	public ppPaddle(double X, double Y, Color myColor, ppTable myTable, GraphicsProgram GProgram) {

		 this.X = X;
		 this.Y = Y;
		 this.myColor = myColor;
		 this.myTable = myTable;
		 this.GProgram = GProgram;
		 this.Vx = 0;
		 this.Vy = 0;
		 
		 //World Coordinates
		 double upperLeftX = X - ppPaddleW / 2;
		 double upperLeftY = Y + ppPaddleH / 2; 
		 
		 GPoint p = myTable.W2S(new GPoint(upperLeftX, upperLeftY));
		 
		 //Screen Coordinates
		 double ScrX = p.getX();
		 double ScrY = p.getY();

		 this.myPaddle = new GRect(ScrX, ScrY, ppPaddleW*Xs, ppPaddleH*Ys);
		 
		 myPaddle.setColor(myColor);
		 myPaddle.setFilled(true);
		 GProgram.add(myPaddle);
	}
	
	/***
	 * run method for the graphics program
	 */
	public void run() {
		double lastX = this.X;
		double lastY = this.Y;
		while (true) {				//Calculates paddle velocity
			Vx=(X-lastX)/TICK;
			Vy=(Y-lastY)/TICK;
			lastX=X;
			lastY=Y;
			GProgram.pause(TICK*TSCALE); // Time to mS*/
		}
	}
	 /***
	  * Getter method for paddle Velocity
	  * @return paddle velocity in form a GPoint object
	  */
	public GPoint getV() {
		return new GPoint(Vx, Vy);
	}
	
	/***
	 * Setter method for paddle position
	 * @param P - new desired paddle position
	 */
	public void setP(GPoint P) {
		//Update instance variables
		this.X = P.getX();
		this.Y = P.getY();
		
		//Limits paddle movement
		if(this.Y < ppPaddleH / 2) this.Y = ppPaddleH / 2;
		if(this.Y > Ymax - ppPaddleH / 2) this.Y = Ymax - ppPaddleH / 2;
		
		//World Coordinates
		double upperLeftX = X - ppPaddleW / 2;
		double upperLeftY = Y + ppPaddleH / 2; 
		
		GPoint p = myTable.W2S(new GPoint(upperLeftX, upperLeftY));
		
		//Screen Coordinates
		double ScrX = p.getX();
		double ScrY = p.getY();
		
		//Update paddle location
		this.myPaddle.setLocation(ScrX, ScrY);		 
	}
	
	/***
	 * Getter method for paddle position
	 * @return world coordinate of paddle position
	 */
	public GPoint getP() {
		return new GPoint(X, Y);
	}
	
	/***
	 * 
	 * @return Sign of Vy
	 */
	public double getSgnVy() {
		if (Vy >= 0) return 1.0;
		else return -1.0;
	}
	
	/***
	 * A method to verify if contact occured between ball and paddle
	 * @param Sx - X position of ball
	 * @param Sy - Y position of ball
	 * @return true if ball is contact with paddle, false otherwise
	 */
	public boolean contact (double Sx, double Sy) {		
		return (Sy >= Y - ppPaddleH / 2) && (Sy <= Y + ppPaddleH / 2);
	}

}
