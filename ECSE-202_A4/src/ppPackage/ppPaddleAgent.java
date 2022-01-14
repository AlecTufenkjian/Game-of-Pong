package ppPackage;

import java.awt.Color;

import acm.graphics.GPoint;
import acm.program.GraphicsProgram;
import static ppPackage.ppSimParams.*;

public class ppPaddleAgent extends ppPaddle{
	
	ppBall myBall;
	
	/***
	 * ppPaddleAgent constructor
	 * @param X - paddle X positon
	 * @param Y - paddle Y positon
	 * @param myColor - paddle color
	 * @param myTable - pingpong table
	 * @param GProgram - graphics program
	 */
	public ppPaddleAgent(double X, double Y, Color myColor, ppTable myTable, GraphicsProgram GProgram) {
		super(X, Y, myColor,  myTable, GProgram); //Initializes parent class
	}
	
	/***
	 * run method for paddle's GProgram
	 */
	public void run() {
		
		int ballSkip = 0;
		int AgentLag = agentLag.getValue(); //Variable agent paddle lag
		
		double lastX = X;
		double lastY = Y;
		while(true) {
			//Updates position after every nth(AgentLag value) iteration
			AgentLag = agentLag.getValue();
			if(ballSkip++ >= AgentLag) {
				
				Vx = (X - lastX) / TICK;
				Vy = (Y - lastY) / TICK;
				lastX = X;
				lastY = Y;
				
				//Get the ball Y position
				double Y = myBall.getP().getY();	
				
				//Set paddle position to that Y
				this.setP(new GPoint(this.getP().getX(), Y));
				
				//Reset skip counter
				ballSkip = 0;
			}
			GProgram.pause(TICK*TSCALE);
		}
		
		
	}

	/***
	 * Attaches ball instance to ppPaddleAgent to update accordingly paddle's positon
	 * @param myBall - Simulation ball
	 */
	public void attachBall(ppBall myBall) {
		this.myBall = myBall;	
	}
	
}