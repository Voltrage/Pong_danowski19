package danowski19.pong_danowski19;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.SparseArray;
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
	private int wallWidth=-1;
	ArrayList<Ball> balls;
	private SparseArray<PointF> mActivePointers;
//    private VelocityTracker mVelocityTracker;

	//used later in part B
	private int player1score;
	private int player2score;
	private boolean is1Player;
	private boolean is3D;
	private boolean isReady;

	private float speed = 20;
	private PointF topPaddleMid;
	private PointF botPaddleMid;

	private int height;
	private int width;
	private int paddleWidth;
	private Rect playVolume;
	private Rect topWallRect;
	private Rect bottomWallRect;

	private Rect topWallVolume;
	private Rect bottomWallVolume;
	private Paint wallPaint = new Paint(); //walls
	private Paint ballPaint = new Paint(); //ball
	private Paint paddlePaint = new Paint(); //paddle
	private Paint blackPaint = new Paint();


	/**
	 * constructor that initializes colors, paddle, and scores
	 */
	public PongAnimator( ) {

		//initialize arrays
		balls = new ArrayList<>();
		mActivePointers = new SparseArray<>();

		//define colors
		wallPaint.setColor(Color.GRAY);
		ballPaint.setColor(Color.RED);
		paddlePaint.setColor(Color.DKGRAY);
		blackPaint.setColor(Color.BLACK);

		topPaddleMid = new PointF();
		botPaddleMid = new PointF();
		paddleWidth=500;
		player1score=0;
		player2score=0;

		isReady=false;
		is1Player = true;
		is3D = false;

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


		//define if undefined
		if (wallWidth < 0) {
			height = g.getHeight();
			width = g.getWidth();
			wallWidth = width / 64;
			botPaddleMid.set(width/2, height*3/4f);
			topPaddleMid.set(width/2, height/4f);
			blackPaint.setTextSize(2*wallWidth);
		}


		Rect nextSpot;
		Rect leftWallRect;
		Rect rightWallRect;


		//define paddles;
		if (is1Player) {
			//set to wall
			topWallRect = new Rect(0, 0, width, wallWidth);
		} else {
			//set to paddle
			topWallRect = new Rect((width - paddleWidth) / 2, 0, (width + paddleWidth) / 2, wallWidth);
		}
		//always a paddle
		bottomWallRect = new Rect((width - paddleWidth) / 2, height - wallWidth, (width + paddleWidth) / 2, height);

		//define active spaces
		if (is3D) {
			//each can go to 1/3rd of the surfaceview
			topWallVolume = new Rect(wallWidth, 0, width - wallWidth, (int) (height / 3f));
			bottomWallVolume = new Rect(wallWidth, height - wallWidth - (int) (height * 2f / 3f), width - wallWidth, height);
		} else {
			//each limited to back of board
			topWallVolume = new Rect(wallWidth, 0, width - wallWidth, wallWidth);
			bottomWallVolume = new Rect(wallWidth, height - wallWidth, width - wallWidth, height);
		}


		//number of touch points
		int size = mActivePointers.size();
		//keep track of motion
		float topVel = topPaddleMid.y, bottomVel = botPaddleMid.y;


		//if at least one touch, then need to adjust paddle
		if (size > 0) {

			//keep track of number in top
			int topCount = 1;

			//average of touches in top and bottom
			for (int i = 0; i < size; i++) {
				PointF finger = mActivePointers.valueAt(i);
				if (finger != null) {
					//add them up
					if(finger.y<(height/2f)){
						topPaddleMid.offset(finger.x, finger.y);
						topCount++;
					}
					else {
						botPaddleMid.offset(finger.x, finger.y);
					}
				}
			}

			int rightLimit = width - wallWidth - paddleWidth / 2;
			int leftLimit = wallWidth + paddleWidth / 2;

			//average of touches
			topPaddleMid.x /= topCount;
			topPaddleMid.y /= topCount;
			botPaddleMid.x /= (size + 2 - topCount);
			botPaddleMid.y /= (size + 2 - topCount);

			topVel = topPaddleMid.y-topVel;
			bottomVel = bottomVel- botPaddleMid.y;

//			if(topCount>size){
//				is1Player=false;
//			}
//			else{
//				is1Player=true;
//			}

			if(is1Player&&topCount>1){
				botPaddleMid.x=(botPaddleMid.x+topPaddleMid.x)/2f;
			}

			//saturate edges
			if (topPaddleMid.x > rightLimit) {
				topPaddleMid.x = rightLimit;
			} else if (topPaddleMid.x < leftLimit) {
				topPaddleMid.x = leftLimit;
			}
			if (botPaddleMid.x > rightLimit) {
				botPaddleMid.x = rightLimit;
			} else if (botPaddleMid.x < leftLimit) {
				botPaddleMid.x = leftLimit;
			}

		}
		//height
		if (is3D) {
			if (topPaddleMid.y > topWallVolume.bottom) {
				topPaddleMid.y = topWallVolume.bottom;
			} else if (topPaddleMid.y < 0) {
				topPaddleMid.y = 0;
			}
			if (botPaddleMid.y < bottomWallVolume.top) {
				botPaddleMid.y = bottomWallVolume.top;
			} else if (botPaddleMid.y > height) {
				botPaddleMid.y = height;
			}
		} else {
			topPaddleMid.y = 0;
			botPaddleMid.y = height - wallWidth;
		}

		//adjust paddles
		if (!is1Player) {
			topWallRect.offsetTo((int) (topPaddleMid.x - paddleWidth / 2f), (int) topPaddleMid.y);
		}
		bottomWallRect.offsetTo((int) (botPaddleMid.x - paddleWidth / 2f), (int) botPaddleMid.y);


		//define as in between paddles
		playVolume = new Rect(wallWidth, topWallRect.bottom, width - wallWidth, bottomWallRect.top);

		//for drawing
		leftWallRect = new Rect(0, 0, wallWidth, height);
		rightWallRect = new Rect(width - wallWidth, 0, width, height);

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
//				if (nextSpot.left <= leftWallRect.right || nextSpot.right >= rightWallRect.left) {
				if(Rect.intersects(nextSpot,leftWallRect) || Rect.intersects(nextSpot,rightWallRect)) {
					//change Y direction since just hit side wall
					n.hitVerticalWall();

				}
				if (Rect.intersects(nextSpot, topWallRect)) {
//					if(is3D){
//						n.hitPaddle(topWallRect,topPaddleMid,paddleWidth,topVel);
//					}
//					else {
						//change X direction since jut hit top or bottom
						n.hitHorizontalWall();
						//handle case of hitting edge
						n.center.y = topWallRect.bottom + n.getRadius();
//					}

				} else if (Rect.intersects(nextSpot, bottomWallRect)) {
					if(is3D){
						n.hitPaddle(botPaddleMid,paddleWidth,bottomVel);
					}
					else {
						//change X direction since jut hit paddle
						n.hitHorizontalWall();
					}
					//handle case of hitting edge
					n.center.y = bottomWallRect.top - n.getRadius();
				}
			}


			//if outside of surface, record index to get rid of it
			if (n.top() > height) {
				toRemove.add(count);
				player2score++;
			}
			else if(n.bottom() < 0){
				toRemove.add(count);
				player1score++;
			}
			//now that physics is set for this ball, set next position, draw and move on to next
			if(isReady) {
				n.moveBall();
			}
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
			if(size==0) {
				isReady = false;
			}
			addBall();
		}

		setSpeed(getAverageBallSpeed());

		g.drawText("Bottom "+player1score + " " + bottomVel,width/20,height/3,blackPaint);
		g.drawText("Top "+player2score +" "+ topVel,15*width/20,height*2/3,blackPaint);


		if(!isReady) {
			g.drawText("READY? TOUCH TO PLAY!", width/2-10*wallWidth, height/2,blackPaint);
		}

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


		if(!isReady){
			isReady=true;
		}

		/**
		 * what to do with multiple fingers
		 * http://www.vogella.com/tutorials/AndroidTouch/article.html
		 *
		 * how to track velocity
		 * https://developer.android.com/training/gestures/movement.html
		 */

		// get pointer index from the event object
		int pointerIndex = event.getActionIndex();

		// get pointer ID
		int pointerId = event.getPointerId(pointerIndex);

		// get masked (not specific to a pointer) action
		int maskedAction = event.getActionMasked();

		switch (maskedAction) {

			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN: {
				// We have a new pointer. Lets add it to the list of pointers
				PointF f = new PointF();
				f.x = event.getX(pointerIndex);
				f.y = event.getY(pointerIndex);
				mActivePointers.put(pointerId, f);


//                if(mVelocityTracker == null) {
//                    // Retrieve a new VelocityTracker object to watch the
//                    // velocity of a motion.
//                    mVelocityTracker = VelocityTracker.obtain();
//                }
//                else {
//                    // Reset the velocity tracker back to its initial state.
//                    mVelocityTracker.clear();
//                }
//                // Add a user's movement to the tracker.
//                mVelocityTracker.addMovement(event);
//				break;
			}

			case MotionEvent.ACTION_MOVE: { // a pointer was moved
				for (int size = event.getPointerCount(), i = 0; i < size; i++) {
					PointF point = mActivePointers.get(event.getPointerId(i));
					if (point != null) {
						point.x = event.getX(i);
						point.y = event.getY(i);
					}
				}
				break;
			}
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_CANCEL: {
				// pointer no longer touching screen.
				mActivePointers.remove(pointerId);
//                // Return a VelocityTracker object back to be re-used by others.
//                mVelocityTracker.recycle();
				break;
			}
		}
//
//		setPaddleLoc(Math.round(event.getX()));
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

	public void setIs3D(boolean whoami) {
		this.is3D = whoami;
	}

	public void setIs1Player(boolean is1Player) {
		this.is1Player = is1Player;
	}

	public void setIsReady(boolean isReady) {
		this.isReady = isReady;
	}

	public void resetScore() {
		player1score=0;
		player2score=0;
	}
}//class TextAnimator
