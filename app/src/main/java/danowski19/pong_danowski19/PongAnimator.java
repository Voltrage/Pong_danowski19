package danowski19.pong_danowski19;

import android.graphics.*;
import android.view.MotionEvent;

import java.util.ArrayList;


/**
 * class that animates a ball repeatedly moving diagonally on
 * simple background
 * 
 * @author Steve Vegdahl
 * @author Andrew Nuxoll
 * @version February 2016
 */
public class PongAnimator implements Animator {


	// instance variables
	private final int wallWidth = 50; // counts the number of logical clock ticks
	private int speed = 20;

	ArrayList<Ball> balls = new ArrayList<Ball>();

	private Rect playVolume;
	private Paint wallPaint = new Paint(); //walls
	private Rect leftWallRect;
	private Rect rightWallRect;
	private Rect topWallRect;
	private Rect bottomWallRect;
	private int paddleMid;
	private int paddleWidth;
	private Paint ballPaint = new Paint(); //ball
	private Paint paddlePaint = new Paint(); //paddle
	private Rect nextSpot;

//backgroundPaint.setARGB(255, 180, 200, 255);

	public PongAnimator() {
		wallPaint.setColor(Color.GRAY);
//				.setARGB(255, 200, 200, 200);
		ballPaint.setColor(Color.RED);
//				.setARGB(255, 255, 0, 0);
		paddlePaint.setColor(Color.DKGRAY);
//				.setARGB(255, 255, 255, 255);
        balls.add(new Ball(wallWidth));

		setPaddleWidth(200);

	}

	public void setPaddleWidth(int w){
		paddleWidth = w;
	}

	public void setPaddleLoc(int touchX){
		bottomWallRect.offsetTo(touchX-paddleWidth/2, bottomWallRect.top);
	}

	public void setSpeed(int speed) {
		this.speed = speed;
		for(Ball n : balls) {
			n.setVelocity(speed);
		}
	}

	/**
	 * Interval between animation frames: .03 seconds (i.e., about 33 times
	 * per second).
	 * 
	 * @return the time interval between frames, in milliseconds.
	 */
	public int interval() {
		return 30;
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
	 * @param g the graphics object on which to draw
	 */
	public void tick(Canvas g) {
//		// bump our count either up or down by one, depending on whether
//		// we are in "backwards mode".
//		if (goBackwards) {
//			count--;
//		}
//		else {
//			count++;
//		}

		//define if undefined
		if(leftWallRect==null || rightWallRect==null || topWallRect==null || playVolume==null) {
			playVolume = new Rect(wallWidth, wallWidth, g.getWidth()-wallWidth, g.getHeight()-wallWidth);
			leftWallRect = new Rect(0, 0, wallWidth, g.getHeight());
			rightWallRect = new Rect(g.getWidth() - wallWidth, 0, g.getWidth(), g.getHeight());
			topWallRect = new Rect(0, 0, g.getWidth(), wallWidth);
			bottomWallRect = new Rect((g.getWidth()-paddleWidth)/2, g.getHeight() - wallWidth, (g.getWidth()+paddleWidth)/2, g.getHeight());
		}

		//draw it
		g.drawRect(leftWallRect,wallPaint);
		g.drawRect(rightWallRect,wallPaint);
		g.drawRect(topWallRect,wallPaint);
		g.drawRect(bottomWallRect,wallPaint);
		g.drawRect(playVolume, paddlePaint);
//		g.drawRect(paddle1, paddlePaint);
		
		// Determine the pixel position of our ball.  Multiplying by 15
		// has the effect of moving 15 pixel per frame.  Modding by 600
		// (with the appropriate correction if the value was negative)
		// has the effect of "wrapping around" when we get to either end
		// (since our canvas size is 600 in each dimension).
		for (Ball n : balls) {


			if(!playVolume.contains(nextSpot = n.getShadow())) {
				if (nextSpot.left <= leftWallRect.right || nextSpot.right >= rightWallRect.left) {
					//change Y direction since just hit side wall
					n.hitSide();
					//recalculate
					nextSpot = n.getShadow();

				}
				if (nextSpot.top <= topWallRect.bottom || nextSpot.bottom >= bottomWallRect.top) {
					//change X direction since jut hit top or bottom
					n.hitPaddle();
					//recalculate
					nextSpot = n.getShadow();
//					g.drawCircle(nextSpot.centerX(), nextSpot.centerY(),n.getRadius(), ballPaint);

				}
			}
				n.moveBall();
				g.drawCircle(n.getCenter().y, n.getCenter().x, n.getRadius(), ballPaint);

		}
//		// Draw the ball in the correct position.
//		Paint redPaint = new Paint();
//		redPaint.setColor(Color.RED);
//		g.drawCircle(num, num, 60, redPaint);
//		redPaint.setColor(0xff0000ff);
	}


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
	 * reverse the ball's direction when the screen is tapped
	 */
	public void onTouch(MotionEvent event)
	{

		setPaddleLoc(Math.round(event.getX()));


	}


	public void addBall() {
		balls.add(new Ball(wallWidth));
	}
}//class TextAnimator
