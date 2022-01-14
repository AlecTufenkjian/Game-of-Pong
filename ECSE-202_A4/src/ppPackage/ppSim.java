package ppPackage;
import static ppPackage.ppSimParams.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import acm.graphics.GPoint;
import acm.program.*;
import acm.util.RandomGenerator;

/**
 * 
 * @author ferrie, Katrina, and Alec Tufenkjian
 * 
 * This class alongside all the other classes are based on ferrie's and Katrina's base code (structure) and filled in by Alec Tufenkjian
 *
 */

public class ppSim extends GraphicsProgram{
	
	ppTable myTable;
	ppPaddle RPaddle;
	ppPaddleAgent LPaddle;
	ppBall myBall;
	RandomGenerator rgen;
	
	/***
	 * Main Method
	 * @param args
	 */
	public static void main(String[] args) {
		new ppSim().start(args);
	}
	
	/***
	 * init method for ppSim graphics program
	 */
	public void init() {
		this.resize(WIDTH+OFFSET,HEIGHT+OFFSET);
		
		//Creating and/or initialize Buttons
		JButton clearButton = new JButton("Clear");
		JButton newServeButton = new JButton("New Serve");
		JButton quitButton = new JButton("Quit");
		traceButton = new JToggleButton("Trace");
		
		//Initialize sliders
		agentLag = new JSlider(0, 50, 5);					//Lag Slider
		tickModifier = new JSlider(1000, 8000, 3000);		//Tick Slider
		
		//Initialize Score board test fields and labels
		txtAgent = new JTextField("Agent");
		txtHuman = new JTextField("Human");
		lblAgent = new JLabel("0");
		lblHuman = new JLabel("0");
		
		//Displays score board
		add(txtAgent, NORTH);
		add(lblAgent, NORTH);
		add(txtHuman, NORTH);
		add(lblHuman, NORTH);
		
		//displays Buttons
		add(clearButton, SOUTH);
		add(newServeButton, SOUTH);
		add(quitButton, SOUTH);
		add(traceButton, SOUTH);
		
		//displays tick slider
		add(new JLabel("+t"), SOUTH);
		add(tickModifier, SOUTH);
		add(new JLabel("-t"), SOUTH);
		
		//displays lag slider
		add(new JLabel("-lag"), SOUTH);
		add(agentLag, SOUTH);
		add(new JLabel("+lag"), SOUTH);
		
		//add event listeners
		addMouseListeners();
		addActionListeners();
		
		//Initializes and sets up random generator
		this.rgen = RandomGenerator.getInstance();
		rgen.setSeed(RSEED);
		
		//sets up table and ball
		this.myTable = new ppTable(this);
		myBall = newBall();
		pause(1000);
		newGame();
	}
	
		/***
		 * Mouse Handler - a moved event moves the right paddle up and down in Y
		 */
		public void mouseMoved(MouseEvent e) {	
			if(myTable == null || RPaddle == null) return;
			GPoint Pm = myTable.S2W(new GPoint(e.getX(), e.getY()));
			double PaddleX = RPaddle.getP().getX();
			double PaddleY = Pm.getY();
			RPaddle.setP(new GPoint(PaddleX,PaddleY));
		}	
		
		/***
		 * runs respective clicked button code
		 */
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if(command.equals("New Serve")) {
				newGame();
			}else if(command.equals("Quit")) {
				System.exit(0);
			}
			else if(command.equals("Clear")) {
				clear();
			}
		}
		
		/***
		 * Creates a new ball for a new round
		 * @return new Ball
		 */
		public ppBall newBall() {
			//Generate some parameters for ppBall			
			Color iColor = Color.RED;
			double iYinit = rgen.nextDouble(YinitMIN,YinitMAX);
			double iLoss = rgen.nextDouble(EMIN,EMAX);
			double iVel = rgen.nextDouble(VoMIN,VoMAX);
			double iTheta = rgen.nextDouble(ThetaMIN,ThetaMAX);
			
			myBall = new ppBall(Xinit, iYinit, iVel, iTheta, iColor, iLoss, myTable, LPaddle, this);
			return myBall;
		}
		
		/***
		 * Sets up and starts a new round
		 */
		public void newGame() {
			if(myBall != null) myBall.kill();
			myTable.newScreen();
			myBall = newBall();
			RPaddle = new ppPaddle(ppPaddleXinit, ppPaddleYinit,Color.GREEN, myTable, this);
			LPaddle = new ppPaddleAgent(LPaddleXinit, LPaddleYinit, Color.BLUE, myTable, this);
			LPaddle.attachBall(myBall);
			myBall.setRightPaddle(RPaddle);
			myBall.setLeftPaddle(LPaddle);
			pause(STARTDELAY);
			myBall.start();
			LPaddle.start();
			RPaddle.start();
		}
		
		/***
		 * Clears score board and starts a new round
		 */
		public void clear() {
			agentScore = 0;
			humanScore = 0;
			newGame();
		}
}
