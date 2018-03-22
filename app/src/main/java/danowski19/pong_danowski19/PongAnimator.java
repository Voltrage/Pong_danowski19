package danowski19.pong_danowski19;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

import java.util.ArrayList;


/**
 * class that animates a ball repeatedly moving diagonally on
 * simple background
 *
 * @author Steve Vegdahl
 * @author Andrew Nuxoll
 * @version February 2016
 *
 * @author Luke Danowski
 * @version March 2018
 */
public class PongAnimator implements Animator{

	// instance variables
	private final int wallWidth = 50; // counts the number of logical clock ticks
	ArrayList<Ball> balls = new ArrayList<>();

	//used later in part B
	private int player1score;
	private int player2score;

	private float speed = 20;
	private int paddleMid;
	private int paddleWidth;

	private Rect playVolume;

	private Paint wallPaint = new Paint(); //walls
	private Paint ballPaint = new Paint(); //ball
	private Paint paddlePaint = new Paint(); //paddle
	private Paint blackPaint = new Paint();


	/**
	 * constructor that initializes colors, paddle, and scores
	 */
	public PongAnimator( ) {

		wallPaint.setColor(Color.GRAY);
		ballPaint.setColor(Color.RED);
		paddlePaint.setColor(Color.DKGRAY);
		blackPaint.setColor(Color.BLACK);

		paddleMid = -1;//changed when touched
		paddleWidth=500;
		player1score=0;
		player2score=0;

	}

	/**
	 *  Adjusts paddle width with minimum of 50dp
	 *  to maximum of 1050dp
	 * @param w from width SeekBar
	 */
	public void setPaddleWidth(int w){
		paddleWidth = w+50;
	}

	/**
	 * Adjusts the middle of the paddle
	 * Called from a touch event
	 * @param touchX from user
	 */
	public void setPaddleLoc(int touchX){
		paddleMid = touchX;
	}

	/**
	 * Sets each ball to the same speed in the range 0.000-100.000
	 * @param speed
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
		for(Ball n : balls) {
			n.setVelocity(speed);
		}
	}

	/**
	 * getter
	 * @return Animator speed value
	 */
	public float getSpeed(){ return speed;}

	/**
	 * Interval between animation frames: .03 seconds (i.e., about 33 times
	 * per second).
	 *
	 * @return the time interval between frames, in milliseconds.
	 */
	public int interval() {
		return 10;
	}

	/**
	 * The background color: a light blue.
	 *
	 * @return the background color onto which we will draw the image.
	 */
	public int backgroundColor() {
		// create/return the background color
		return Color.rgb(180, 200, 255);
	}

	/**
	 * Action to perform on clock tick
	 *
	 * defines various Rect objects and compares them, adjusting each Ball objects's slope
	 * values with respect to these walls before calling the moveBall() method on them.
	 *
	 * draws walls, paddle, and ball
	 *
	 * @param g the graphics object on which to draw
	 */
	public void tick(Canvas g) {
		Rect nextSpot;
		Rect leftWallRect;
		Rect rightWallRect;
		Rect topWallRect;
		Rect bottomWallRect;

		//define spaces
		playVolume = new Rect(wallWidth, wallWidth, g.getWidth()-wallWidth, g.getHeight()-wallWidth);
		leftWallRect = new Rect(0, 0, wallWidth, g.getHeight());
		rightWallRect = new Rect(g.getWidth() - wallWidth, 0, g.getWidth(), g.getHeight());
		topWallRect = new Rect(0, 0, g.getWidth(), wallWidth);
		bottomWallRect = new Rect((g.getWidth()-paddleWidth)/2, g.getHeight() - wallWidth, (g.getWidth()+paddleWidth)/2, g.getHeight());

		//account for touch input
		if(paddleMid>=0) {
			int rightLimit = g.getWidth()-wallWidth-paddleWidth/2;
			int leftLimit = wallWidth+paddleWidth/2;
			if(paddleMid > rightLimit){
				paddleMid = rightLimit;
			}
			else if( paddleMid < leftLimit){
				paddleMid = leftLimit;
			}
			bottomWallRect.offsetTo(paddleMid - paddleWidth / 2, bottomWallRect.top);
		}

		//draw it
		g.drawRect(leftWallRect,wallPaint);
		g.drawRect(rightWallRect,wallPaint);
		g.drawRect(topWallRect,wallPaint);
		g.drawRect(bottomWallRect,paddlePaint);

		/**
		 * Problem: Ran into exceptions and weird bugs wehn running
		 *
		 * Help From: asked Nuxoll
		 *
		 * Solution: suggested create an array of indexes and run the "remove" after the loop
		 *
		 * supported by https://codereview.stackexchange.com/questions/64011/removing-elements-on-a-list-while-iterating-through-it
		 */

		ArrayList<Integer> toRemove = new ArrayList<>();
        int count=0;
		for(Ball n : balls) {
            nextSpot = n.getShadow();
            if (!playVolume.contains(nextSpot)) {
                if (nextSpot.left <= leftWallRect.right || nextSpot.right >= rightWallRect.left) {
                    //change Y direction since just hit side wall
                    n.hitSide();
                    //recalculate
                    nextSpot = n.getShadow();

                } else if (nextSpot.top <= topWallRect.bottom) {
                    //change X direction since jut hit top or bottom
                    n.hitPaddle();
                    //recalculate
                    nextSpot = n.getShadow();
                }
                if (nextSpot.intersect(bottomWallRect)) {
                    //change X direction since jut hit paddle
                    n.hitPaddle();
                    //handle case of hitting edge
                    n.center.y = bottomWallRect.top - n.getRadius();
                }
            }

            //if outside of surface, record index to get rid of it
            if (n.top() > bottomWallRect.bottom) {
                toRemove.add(count);
				player1score++;
			}
			//now that physics is set for this ball, set next position, draw and move on to next
			n.moveBall();
			g.drawCircle(n.getCenter().x, n.getCenter().y, n.getRadius(), blackPaint);
			g.drawCircle(n.getCenter().x, n.getCenter().y, n.getRadius()-6, ballPaint);

			count++;
		}

		//remove out of bounds balls
		for(Integer n : toRemove) {
			balls.remove(n.intValue());
		}

		//make sure there is at least one
		if(count==0){
			addBall();
		}

		setSpeed(getAverageBallSpeed());

	}//tick


	/**
	 * Tells that we never pause.
	 *
	 * @return indication of whether to pause
	 */
	public boolean doPause() {
		return false;
	}

	/**
	 * Tells that we never stop the animation.
	 *
	 * @return indication of whether to quit.
	 */
	public boolean doQuit() {
		return false;
	}

	/**
	 * adjust paddle location when screen is touched
	 */
	public void onTouch(MotionEvent event)
	{
		setPaddleLoc(Math.round(event.getX()));
	}

	/**
	 * when new balls are generated, they come in with random speed
	 * so set all the balls and seekBar to their average
	 */
	public float getAverageBallSpeed(){
		float sum=0;
		for(Ball n: balls){
			sum+=n.getVelocity();
		}
		sum = (sum/balls.size());
		return sum;
	}

	/**
	 * helper method for adding a Ball
	 */
	public void addBall() {
		balls.add(new Ball(playVolume));
	}

	/**
	 * getter
	 * @return	current PaddleWidth
	 */
	public int getPaddleWidth() {
		return paddleWidth;
	}

	/**
	 * unused getter, will be used in Part B
	 * @return
	 */
	public int getPlayer1score() {
		return player1score;
	}

	/**
	 * unused getter, will be used in Part B
	 * @return
	 */
	public int getPlayer2score() {
		return player2score;
	}
}//class TextAnimator
