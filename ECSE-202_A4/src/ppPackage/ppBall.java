package ppPackage;
import static ppPackage.ppSimParams.*;
import acm.graphics.GOval;
import acm.graphics.GPoint;
import acm.program.GraphicsProgram;

import java.awt.Color;

public class ppBall extends Thread {
	
	// Instance variables
	private double Xinit; 					// Initial position of ball - X
	private double Yinit; 					// Initial position of ball - Y
	private double Vo; 						// Initial velocity (Magnitude)
	private double theta; 					// Initial direction
	private double loss; 					// Energy loss on collision
	private Color color; 					// Color of ball
	private GraphicsProgram GProgram; 		// Instance of ppSim class (this)
	private GOval myBall; 					// Graphics object representing ball
	private ppPaddle RPaddle, LPaddle; 		// Right and Left Paddle
	private double X, Xo, Y, Yo; 			//Ball position and travel parameters
	private double Vx, Vy; 					//Ball velocities
	private boolean running; 				//Simulation is running
	private ppTable myTable; 				// Table
	
	/***
	 * The constructor for the ppBall class copies parameters to instance variables, creates an
	 * instance of a GOval to represent the ping-pong ball, and adds it to the display.
	 * 
	 * @param Xinit - starting position of the ball X (meters)
	 * @param iYinit - starting position of the ball Y (meters)
	 * @param iVel - initial velocity (meters/second)
	 * @param iTheta - initial angle to the horizontal (degrees)
	 * @param iColor - ball color (Color)
	 * @param iLoss - loss on collision ([0,1])
	 * @param myTable - Some table graphics and exporting functions
	 * @param myPaddle - Paddle
	 * @param GProgram - a reference to the ppSim class used to manage the display
	 */
	public ppBall(double Xinit, double iYinit, double iVel, double iTheta, Color iColor, double iLoss, ppTable myTable, ppPaddle myPaddle, GraphicsProgram GProgram) {
		this.Xinit=Xinit; // Copy constructor parameters to instance variables
		this.Yinit=iYinit;
		this.Vo=iVel;
		this.theta=iTheta;
		this.loss=iLoss;
		this.color=iColor;
		this.myTable = myTable;
		this.GProgram = GProgram;
		
		GPoint p = myTable.W2S(new GPoint(Xinit, Yinit));		
    	double ScrX = p.getX();											// Convert simulation to screen coordinates
    	double ScrY = p.getY();	
		this.myBall = new GOval(ScrX, ScrY, 2*bSize*Xs, 2*bSize*Ys); 	//Draw the ball based on the point p
    	myBall.setColor(this.color);
    	myBall.setFilled(true);
    	GProgram.add(myBall);
	}
	
	//Run method for ppBall
	public void run() {
		
		// Initialize simulation parameters
		Xo = Xinit;											// Set initial X position
		Yo = Yinit;											// Set initial Y position
		double time = 0;									// Time starts at 0 and counts up
		double Vt = bMass*g / (4*Math.PI*bSize*bSize*k); 	// Terminal velocity
		double Vox = Vo*Math.cos(theta*Math.PI/180);		// X component of velocity
		double Voy = Vo*Math.sin(theta*Math.PI/180);		// Y component of velocity
		double KEx = ETHR, KEy = ETHR;						// Kinetic energy in X and Y directions
		double PE=ETHR;										// Potential energy
		running = true;										// Initial state = falling.
		
		GProgram.pause(1000);
		
		if (MESG) System.out.printf("\t\t\t Ball Position and Velocity\n");
		
		// Main simulation loop
		while (running) {
			X = Vox*Vt/g*(1-Math.exp(-g*time/Vt));				// Update relative position
			Y = Vt/g*(Voy+Vt)*(1-Math.exp(-g*time/Vt))-Vt*time;
    		Vx = Vox*Math.exp(-g*time/Vt);						// Update velocity
    		Vy = (Voy+Vt)*Math.exp(-g*time/Vt)-Vt;
    		
    		incrementScores();
    		
    		// Check to see if we hit the ground yet.
    		if (Vy < 0 && Yo+Y <= bSize) {
    			KEx=0.5*bMass*Vx*Vx*(1-loss);					//Update energy
    			KEy=0.5*bMass*Vy*Vy*(1-loss);
    			PE=0;
    			
    			checkBallEnergy(KEx, KEy, PE);
    			
    			Vox=Math.sqrt(2*KEx/bMass);
    			Voy=Math.sqrt(2*KEy/bMass);
    			
    			if (Vx < 0) Vox = -Vox;
    			
    			time = 0; 			  //Reset time at every collision
    			Xo+=X; 				  //Accumulate distance between collisions
    			Yo = bSize;     	  //When the ball hits the ground, the height of the center is the radius of the ball.
    			
    			X = 0;                // (X,Y) is the instantaneous position along an arc,
    			Y = 0; 	              // Absolute position is (Xo+X,Yo+Y).
			}
    		
    		//Right Paddle Collision
    		if(Vx > 0 && (Xo + X) >= RPaddle.getP().getX() - bSize - ppPaddleW / 2) {
    			if(RPaddle.contact(X + Xo, Y + Yo)) {
    			
	    			KEx=0.5*bMass*Vx*Vx*(1-loss);
	    			KEy=0.5*bMass*Vy*Vy*(1-loss);
	    			PE=bMass*g*Y;
	    			
	    			checkBallEnergy(KEx, KEy, PE);
	    			
	    			Vox = -Math.sqrt(2*KEx/bMass);
	    			Voy = Math.sqrt(2*KEy/bMass);
	    				
	    			//V signs
	    			Vox = Vox * ppPaddleXgain;
	    			Voy = Voy * ppPaddleYgain * RPaddle.getV().getY();
	    			
	    			Vox = limitBallVelocity(Vox);
	    			
	    			if(Vy < 0) Voy = -Voy; //Voy will depend on the sign of Vy

	    			time=0;
	    			Xo = RPaddle.getP().getX()-ppPaddleW/2;  
	    			Yo+=Y;
	    			
	    			X=0;
	    			Y=0;
    			
    			} else { 					//Human misses
    				running = false;
	    			agentScore++;
    			}	
    		}
    		
    		//Left Paddle Collision
    		if(Vx<0 && Xo+X<=LPaddle.getP().getX() + bSize + ppPaddleW / 2) {
    			if(LPaddle.contact(X + Xo,Y + Yo)) {
	    			KEx=0.5*bMass*Vx*Vx*(1-loss);
	    			KEy=0.5*bMass*Vy*Vy*(1-loss);
	    			PE=bMass*g*Y;
	    			
	    			checkBallEnergy(KEx, KEy, PE);
	    			
	    			Vox=Math.sqrt(2*KEx/bMass);
	    			Voy=Math.sqrt(2*KEy/bMass);
	    			
	    			//V signs
	    			Vox = Vox * ppPaddleXgain;
	    			Voy = Voy * ppPaddleYgain * LPaddle.getV().getY();
	    			
	    			Vox = limitBallVelocity(Vox);
	    			
	    			if(Vy < 0) Voy = -Voy; //Voy will depend on the sign of Vy
	    			
	    			time=0;
	    			Xo = XwallL + bSize;
	    			Yo+=Y;
	    			
	    			X=0;
	    			Y=0;   
    			} else {				//Agent misses
    				running = false;
    				humanScore++;
    			}
			}
	    			
	    	// Display current values (1 time/second)	    		
    		if (MESG) System.out.printf("t: %.2f\t\t X: %.2f\t Y: %.2f\t Vx: %.2f\t Vy: %.2f\n",
						time,X+Xo,Y+Yo,Vx,Vy);

			updateBallLocation();
	
			trace();
			
			time += TICK; //Update time
			
			// Pause display
			GProgram.pause(TICK * tickModifier.getValue());
			
			checkBallEnergy(KEx, KEy, PE);
			
			updateScoreBoard();
		}
	}

	/***
	 * Ball loses energy only during collisions, so this is when we check if simulation should stop.
	 * @param KEx - Kinetic energy in direction X
	 * @param KEy - Kinetic energy in direction Y
	 * @param PE - Potential energy
	 */
	public void checkBallEnergy(double KEx, double KEy, double PE) {
		if ((KEx + KEy + PE) < ETHR) running = false;
	}
	
	/***
	 * A method to set right paddle
	 * @param myPaddle - Right Paddle
	 */
	public void setRightPaddle(ppPaddle myPaddle) {
		this.RPaddle = myPaddle;
	}
	
	/***
	 * A method to set left paddle
	 * @param myPaddle - Left Paddle
	 */
	public void setLeftPaddle(ppPaddle myPaddle) {
		this.LPaddle = myPaddle;
	}
	
	/***
	 * Getter for ball velocity
	 * @return Ball velocity in form of a GPoint Object
	 */
	public GPoint getV() {
		return new GPoint(Vx, Vy);
	}
	
	/***
	 * Getter for ball positon
	 * @return Ball position in form of a GPoint object
	 */
	public GPoint getP() {
		return new GPoint(X+Xo, Y+Yo);
	}
	
	/***
	 * This method updates the ball location after every change. It also slightly alters real position for aesthethic purposes
	 */
	public void updateBallLocation() {
		GPoint p =  myTable.W2S(new GPoint(Xo + X - bSize,Yo + Y + bSize));		// Get current position in screen coordinates
		
		double ScrX = p.getX(); 
		double ScrY = p.getY();
			
		if(ScrX > (XwallR - 2*bSize - ppPaddleW) * Xs && !running) {			//Ball is out of bounds on the right side (edge case)
			this.myBall.setLocation(XwallR * Xs, ScrY);	
		}else if(ScrX <= (XwallL + ppPaddleW + bSize) * Xs && !running){		//Ball is out of bounds on the left side (edge case)
			this.myBall.setLocation((XwallL - ppPaddleW - 2*bSize) * Xs, ScrY);	
		}else {
			if(ScrX > (XwallR - bSize - ppPaddleW) * Xs) {						//Ball hits right paddle (edge case)
				this.myBall.setLocation((XwallR - 2*bSize - ppPaddleW) * Xs, ScrY);
			}else {
				this.myBall.setLocation(ScrX,ScrY);								//Updates to real location (regular case)
			}
		}
	}
	
	/***
	 * Updates Score board text labels
	 */
	public void updateScoreBoard() {
		lblAgent.setText("" + agentScore); 
		lblHuman.setText("" + humanScore);		
	}
		
	/***
	 * Increments scores when ball goes beyond the Ymax (ceiling)
	 */
	public void incrementScores() {
		if(Yo + Y > Ymax) { 
			running = false;
			
			if(Vx > 0) {
				humanScore++;
			}else {
    			agentScore++;  				
			}
		}
	}
	
	/***
	 * 
	 * @param Vox - Ball velocity in direction X
	 * @return Ball velocity limited to maximum possible velocity
	 */
	public double limitBallVelocity(double Vox) {
		if(Vox > VoxMAX) {
			return VoxMAX;
		} else if(Vox < -VoxMAX) {
			return -VoxMAX;
		}else {
			return Vox;
		}
	}
	
	/***
	 * Kills simulation, ball stops
	 */
	void kill() {
		running = false;
	}
	
	/***
	 * A simple method to plot a dot at the current location in screen coordinates
	 */
    private void trace() {
		GPoint p = myTable.W2S(new GPoint(Xo + X, Yo + Y));
		double ScrX = p.getX();
		double ScrY = p.getY();
    	
    	if(traceButton.isSelected()) {
			GOval pt = new GOval(ScrX,ScrY,PD,PD); //Create trace point in the middle of the ball based on coordinates
			pt.setColor(Color.BLACK);
			pt.setFilled(true);
			GProgram.add(pt);	
    	}
	}
}
