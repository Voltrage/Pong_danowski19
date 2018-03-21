package danowski19.pong_danowski19;

import android.graphics.*;
import android.view.MotionEvent;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;


/**
 * class that animates a ball repeatedly moving diagonally on
 * simple background
 *
 * @author Steve Vegdahl
 * @author Andrew Nuxoll
 * @version February 2016
 */
public class PongAnimator implements Animator{

	// instance variables
	private final int wallWidth = 50; // counts the number of logical clock ticks
	ArrayList<Ball> balls = new ArrayList<Ball>();

	int player1score;
	int player2score;

	private int speed = 20;
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
		paddleMid = -1;//changed when touched
		paddleWidth=500;
		player1score=0;
		player2score=0;
	}

	public void setPaddleWidth(int w){
		paddleWidth = w;
	}

	public void setPaddleLoc(int touchX){
		paddleMid = touchX;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
		for(Ball n : balls) {
			n.setVelocity(speed);
		}
	}


	public int getSpeed(){ return speed;}

	/**
	 * Interval between animation frames: .03 seconds (i.e., about 33 times
	 * per second).
	 *
	 * @return the time interval between frames, in milliseconds.
	 */
	public int interval() {
		return 20;
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
		g.drawRect(bottomWallRect,wallPaint);
//		g.drawRect(playVolume, paddlePaint);
//		g.drawRect(paddle1, paddlePaint);

		//		ArrayList<Ball> copy = balls;


		/**
		 * ran into exceptions and weird bugs wehn running
		 *
		 * asked Nuxol
		 *
		 * suggested create an array of indexes and run after the fact
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
            }
            n.moveBall();
            g.drawCircle(n.getCenter().x, n.getCenter().y, n.getRadius(), ballPaint);
            count++;
        }

        //remove out of bounds
		for(Integer n : toRemove) {
			balls.remove(n.intValue());
		}

		//make sure there is at least one
		if(count==0){
			addBall();
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

	/**
	 * when new balls are generated, they come in with random speed
	 * so set the balls and seekBar to that average
	 * @return
	 */
	public int averageBallSpeed(){
		int sum=0;
		for(Ball n: balls){
			sum+=n.getVelocity();
		}
		return sum/balls.size();
	}

	public void addBall() {
		balls.add(new Ball(wallWidth));
	}

	public int getPaddleWidth() {
		return paddleWidth;
	}

//	public int getPlayer1score() {
//
//		return player1score;
//	}

//	public int getPlayer2score() {
//		return player2score;
//	}
}//class TextAnimator
