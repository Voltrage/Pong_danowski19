package danowski19.pong_danowski19;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
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
	//	ArrayList<Ball> balls;
	//	private SparseArray<PointF> mActivePointers;
//    private VelocityTracker mVelocityTracker;
	private SparseArray<Finger> mActivePointers;
	private ArrayList<Integer> mActiveKeysBottom;
	private ArrayList<Integer> mActiveKeysTop;
	public ArrayList<Ball> balls;

	private PointF player1Historical;
	private PointF player2Historical;

	private int player1score;
	private int player2score;
	private boolean is2D;
	private boolean isPaused;
	private boolean is1Player;
	private boolean needsReinitialized;
	//	private boolean is2Player;
	private boolean isCompPlayer;

	private float speed = 20;
	private PointF topPaddleMid;
	private PointF botPaddleMid;

	private int height;
	private int width;
	private int paddleWidth;
	private Rect playVolume;
	private Rect topWallRect;
	private Rect bottomWallRect;
	private Paddle topWallPaddle;
	private Paddle bottomWallPaddle;

	private Rect ballCreationArea;
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
		mActiveKeysBottom = new ArrayList<>();
		mActiveKeysTop = new ArrayList<>();

		//define colors
		wallPaint.setColor(Color.GRAY);
		ballPaint.setColor(Color.RED);
		paddlePaint.setColor(Color.DKGRAY);
		blackPaint.setColor(Color.BLACK);

		wallWidth=-1;
		width=-1;
		height=-1;

		topPaddleMid = new PointF();
		botPaddleMid = new PointF();
		paddleWidth=500;
		player1score=0;
		player2score=0;

		is1Player = true;
		isPaused = true;
		is2D = false;
		needsReinitialized=true;
	}

	/**
	 *  Adjusts paddle width with minimum of 50dp
	 *  to maximum of 1050dp
	 * @param w from width SeekBar
	 */
	public void setPaddleWidth(int w){
		paddleWidth = w+50;
		if(is1Player & bottomWallPaddle!=null){
			bottomWallPaddle.setPaddleWidth(paddleWidth);

		}
		else if( bottomWallPaddle!=null && topWallPaddle!=null){
			bottomWallPaddle.setPaddleWidth(paddleWidth);
			topWallPaddle.setPaddleWidth(paddleWidth);
		}
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
		if(needsReinitialized) {
			height = g.getHeight();
			width = g.getWidth();
			wallWidth = width / 64;
			botPaddleMid.set(width / 2, height * 3 / 4f);
			topPaddleMid.set(width / 2, (is1Player)? 0 : height/4f );
			blackPaint.setTextSize(2 * wallWidth);

			ballCreationArea = new Rect(100, height/2 - height/6, width-100, height/2+height/6);

			//define paddles as walls and adjust them based off current paddleWidth
//			topWallRect = new Rect(0, 0, width, wallWidth);
//			bottomWallRect = new Rect(0, height - wallWidth, width, height);

			Point topStart = new Point(width/2, 0);
			Point bottomStart = new Point(width/2, 3*height/4);

			//define paddles
			topWallPaddle = new Paddle(g,(is1Player)? width : paddleWidth, wallWidth, topStart, true, is2D);
			bottomWallPaddle = new Paddle(g,paddleWidth, wallWidth, bottomStart, false, is2D);

//			//define active spaces
//			if (is2D) {
//				//each can go to 1/3rd of the surfaceview
//				topWallVolume = new Rect(wallWidth, 0, width - wallWidth, (int) (height / 3f));
//				bottomWallVolume = new Rect(wallWidth, height - wallWidth - (int) (height * 2f / 3f), width - wallWidth, height);
//
//			} else {
//				//each limited to back of board
//				topWallVolume = new Rect(wallWidth, 0, width - wallWidth, wallWidth);
//				bottomWallVolume = new Rect(wallWidth, height - wallWidth, width - wallWidth, height);
//			}
//

			resetScore();


			isPaused=true;
			needsReinitialized=false;
		}
//	}

		Rect nextSpot;
		Rect leftWallRect;
		Rect rightWallRect;

		float player1Angle; //1 is straight, with +-1 from how far ball lands from middle
		float player2Angle;
		Finger player1Active = getLowestFinger();
		Finger player2Active = getHighestFinger();

		//number of touch points
		int size = mActivePointers.size();

		//if at least one touch, then need to adjust paddle
		if (size > 0) {

			//might be null
			if (is1Player && player1Active != null) {
				//move to finger, is bounded, tracks change
				bottomWallPaddle.setPaddleMid( pointF2I(player1Active.getPointF()));
			}
			else if( player1Active!=null && player2Active!=null){
				bottomWallPaddle.setPaddleMid( pointF2I(player1Active.getPointF()));
				topWallPaddle.setPaddleMid( pointF2I(player1Active.getPointF()));
			}

			int topCount = 1;

			//average of touches in top and bottom
//			for (int i = 0; i < size; i++) {
//				Finger finger = mActivePointers.valueAt(i);
//				if (finger != null) {
//					//add them up
//					if(finger.getStartY()<(height/2f)){
//						topPaddleMid.offset(finger.x, finger.y);
//						topCount++;
//					}
//					else {
//						botPaddleMid.offset(finger.x, finger.y);
//					}
//				}
//			}
//			//average of touches
//			topPaddleMid.x /= topCount;
//			topPaddleMid.y /= topCount;
//			botPaddleMid.x /= (size + 2 - topCount);
//			botPaddleMid.y /= (size + 2 - topCount);


//			if(topCount>size){
//				is1Player=false;
//			}
//			else{
//				is1Player=true;
//			}

			//update history
//			if(!is1Player && player2Active!=null) {
//				player2Historical.set(player2Active.getPointF());
//			}
//			player1Historical.set(player1Active.getPointF());
		}
//
//		//height
//		if (is2D) {
//			if (topPaddleMid.y > topWallVolume.bottom) {
//				topPaddleMid.y = topWallVolume.bottom;
//			} else if (topPaddleMid.y < 0) {
//				topPaddleMid.y = 0;
//			}
//			if (botPaddleMid.y < bottomWallVolume.top) {
//				botPaddleMid.y = bottomWallVolume.top;
//			} else if (botPaddleMid.y > height) {
//				botPaddleMid.y = height;
//			}
//		} else {
//			topPaddleMid.y = 0;
//			botPaddleMid.y = height - wallWidth;
//		}
//
//		//adjust paddles
//		if (!is1Player) {
//			topWallRect.offsetTo((int) (topPaddleMid.x - paddleWidth / 2f), (int) topPaddleMid.y);
//		}
//		bottomWallRect.offsetTo((int) (botPaddleMid.x - paddleWidth / 2f), (int) botPaddleMid.y);
//

		//define as in between paddles
		playVolume = new Rect(wallWidth, topWallPaddle.box.bottom, width - wallWidth, bottomWallPaddle.box.top);

		//for drawing
		leftWallRect = new Rect(0, 0, wallWidth, height);
		rightWallRect = new Rect(width - wallWidth, 0, width, height);

		//draw it
		g.drawRect(leftWallRect,wallPaint);
		g.drawRect(rightWallRect,wallPaint);
		topWallPaddle.draw(g, (is1Player)? wallPaint: paddlePaint);
		bottomWallPaddle.draw(g, paddlePaint);


		/**
		 * Problem: Ran into exceptions and weird bugs when running
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
//			if (!playVolume.contains(nextSpot)) { //only check when need to
				// collision with wall
				if(Rect.intersects(nextSpot,leftWallRect) || Rect.intersects(nextSpot,rightWallRect)) {
					//change X direction since just hit side wall
					n.hitVerticalWall();
				}
				//collision with a Paddle
				if (Rect.intersects(nextSpot, topWallPaddle.box)) {
					//change X direction since jut hit top or bottom
					n.hitHorizontalWall();
					//handle case of hitting edge
					n.center.y = topWallPaddle.box.bottom + n.getRadius();
				}
				else if (Rect.intersects(nextSpot, bottomWallPaddle.box)) {
					//change X direction since jut hit paddle
					n.hitHorizontalWall();
					//handle case of hitting edge
					n.center.y = bottomWallPaddle.box.top - n.getRadius();
				}
//			}


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
			if(isPaused) {
				n.moveBall();
			}
			n.draw(g, ballPaint);
			count++;
		}

		//remove out of bounds balls
		for(Integer n : toRemove) {
			balls.remove(n.intValue());
		}

		//make sure there is at least one
		if(count==0){
			if(size==0) {
				isPaused = false;
			}
			addBall();
		}

		setSpeed(getAverageBallSpeed());

		g.drawText("Bottom "+player1score + " " + bottomWallPaddle.dy,width/20,height/3,blackPaint);
		g.drawText("Top "+player2score +" "+ topWallPaddle.dy,15*width/20,height*2/3,blackPaint);


		if(!isPaused) {
			g.drawText("READY? TOUCH TO PLAY!", width/2-10*wallWidth, height/2,blackPaint);
		}



	}//tick

	private Finger getLowestFinger() {

		int keyOfLowest=-1;
		float min = 0;
		for (Integer k : mActiveKeysBottom){
			Finger f = mActivePointers.get(k.intValue());
			if(f.y > min){
				min = f.y;
				keyOfLowest = k.intValue();
			}
		}
		if(keyOfLowest>=0)
			return mActivePointers.get(keyOfLowest);
		else
			return null;
	}

	private Finger getHighestFinger() {

		int keyOfLowest=-1;
		float max = height;
		for (Integer k : mActiveKeysTop){
			Finger f = mActivePointers.get(k.intValue());
			if(f.y < max){
				max = f.y;
				keyOfLowest = k.intValue();
			}
		}
		if(keyOfLowest>=0)
			return mActivePointers.get(keyOfLowest);
		else
			return null;
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
	 * adjust paddle location when screen is touched
	 */
	public void onTouch(MotionEvent event)
	{

		//need to run tick method at least once
		if(needsReinitialized){
			return;
		}

		//on a touch, the game will resume
		if(!isPaused){
			isPaused =true;
		}


		/**
		 * what to do with multiple fingers
		 * http://www.vogella.com/tutorials/AndroidTouch/article.html
		 */

		// get pointer index from the event object
		int pointerIndex = event.getActionIndex();

		// get pointer ID
		int pointerId = event.getPointerId(pointerIndex);

		// get masked (not specific to a pointer) action
		int maskedAction = event.getActionMasked();

		switch (maskedAction) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				// We have a new pointer. Lets add it to the list of pointers
				Finger f = new Finger(event.getX(pointerIndex), event.getY(pointerIndex));
				mActivePointers.put(pointerId, f);

				//sorts keys
				if(f.getStartY()<height/2f) {
					mActiveKeysTop.add(pointerId);
				}
				else {
					mActiveKeysBottom.add(pointerId);
				}

				break;
			case MotionEvent.ACTION_MOVE:  // a pointer was moved
				for (int size = event.getPointerCount(), i = 0; i < size; i++) {
					Finger point = mActivePointers.get(event.getPointerId(i));
					if (point != null) {
						point.x = event.getX(i);
						point.y = event.getY(i);
					}
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_CANCEL:
				// pointer no longer touching screen.

				if(mActivePointers.get(pointerId).getStartY()<height/2f) {
					mActiveKeysTop.remove(pointerId);
				}
				else {
					mActiveKeysBottom.remove(pointerId);
				}
				mActivePointers.remove(pointerId);
				break;
		}

//		updateFingers();



	}

	private void updateFingers(){

		mActiveKeysTop.clear();
		mActiveKeysBottom.clear();
		for (int size = mActivePointers.size(), i = 0; i < size; i++) {
			int key = mActivePointers.keyAt(i);

			//sorts keys
			if(mActivePointers.get(key).getStartY()<height/2f)
				mActiveKeysTop.add(key);
			else
				mActiveKeysBottom.add(key);
		}

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
		if(ballCreationArea!=null) {
			balls.add(new Ball(ballCreationArea));
		}
	}

	/**
	 * getter
	 * @return	current PaddleWidth
	 */
	public int getPaddleWidth() {
		return paddleWidth;
	}

	/**
	 * setter
	 * @param yes_no
	 */
	public void setIs2D(boolean yes_no) {
		this.is2D = yes_no;
		needsReinitialized=true;
	}

	/**
	 * setter
	 * @param is1Player
	 */
	public void setIs1Player(boolean is1Player) {
		this.is1Player = is1Player;
		needsReinitialized=true;
	}

	/**
	 * setter
	 * @param isPaused
	 */
	public void setIsPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}

	/**
	 * yep
	 */
	public void resetScore() {
		player1score=0;
		player2score=0;
	}

	/**
	 * helper function that turns PointF into Point
	 * @param p
	 * @return
	 */
	public Point pointF2I(PointF p){
		return new Point((int)p.x, (int)p.y);
	}


}//class TextAnimator
