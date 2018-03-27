package danowski19.pong_danowski19;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * PongMainActivity
 *
 * This is the activity for the Pong game. It attaches a PongAnimator to
 * an AnimationSurface.
 *
 *
 *
 *
 * @author Andrew Nuxoll
 * @author Steven R. Vegdahl
 * @version July 2013
 *
 *
 * @author Luke Danowski
 * @version March 2018
 *
 * added multiple ball support
 * animated the paddle
 * can change size of paddle
 * can change speed of balls
 *
 */
public class PongMainActivity extends Activity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, RadioGroup.OnCheckedChangeListener {

	TextView score1TV;
	TextView score2TV;
	SeekBar speedSB;
	SeekBar widthSB;
	Button reset;
	Button addBall;
	Button pause;
	PongAnimator Pong;
	RadioGroup selects;
	CheckBox undock;



	/**
	 * creates an AnimationSurface containing a PongAnimator.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pong_main);

		Pong = new PongAnimator();

		// get the animation surface
		AnimationSurface mySurface = (AnimationSurface) this.findViewById(R.id.animationSurface);
		// link it to the animator
		mySurface.setAnimator(Pong);

		// get variables in xml
		score1TV = (TextView) this.findViewById(R.id.playerOneScore);
		score2TV = (TextView) this.findViewById(R.id.playerTwoScore);
		speedSB = (SeekBar) this.findViewById(R.id.ballSpeed);
		widthSB = (SeekBar) this.findViewById(R.id.paddleWidth);
		reset = (Button) this.findViewById(R.id.resetBTN);
		addBall = (Button) this.findViewById(R.id.addBallBTN);
		selects = (RadioGroup) this.findViewById(R.id.selects);
		undock = (CheckBox) this.findViewById(R.id.undock);
		pause = (Button) this.findViewById(R.id.pause);

		// link to listener
		speedSB.setOnSeekBarChangeListener(this);
		widthSB.setOnSeekBarChangeListener(this);
		reset.setOnClickListener(this);
		addBall.setOnClickListener(this);
		selects.setOnCheckedChangeListener(this);
		undock.setOnClickListener(this);
		pause.setOnClickListener(this);

		speedSB.setProgress((int) Pong.getSpeed()*1000);
		widthSB.setProgress(Pong.getPaddleWidth());

	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.resetBTN:
				Pong.balls.clear();
				Pong.resetScore();
			case R.id.addBallBTN:
				Pong.addBall();
				speedSB.setProgress((int)Pong.getAverageBallSpeed()*1000);
				break;
			case R.id.undock:
				if(v instanceof CheckBox) {
					Pong.setIs3D(((CheckBox) v).isChecked());
				}
				break;
			case R.id.pause:
				Pong.setIsReady(false);
				break;
			default:
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if(seekBar.getId()==R.id.ballSpeed) {
			Pong.setSpeed(progress/1000f);
		}
		else if(seekBar.getId()==R.id.paddleWidth){
			Pong.setPaddleWidth(progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
//		if(group.find;
//		//for drawing
//		leftWallRect = new Rect(0, 0, wallWidth, height);
//		rightWallRect = new Rect(width - wallWidth, 0, width, height);
//

		// This will get the radiobutton that has changed in its check state
		RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
		switch (checkedId){
			case R.id.One:
				Pong.setIs1Player(true);
				break;
			case R.id.Two:
				Pong.setIs1Player(false);
				break;
		}
	}
}
