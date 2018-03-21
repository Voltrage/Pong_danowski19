package danowski19.pong_danowski19;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * PongMainActivity
 * 
 * This is the activity for the Pong game. It attaches a PongAnimator to
 * an AnimationSurface.
 * 
 * @author Andrew Nuxoll
 * @author Steven R. Vegdahl
 * @version July 2013
 * 
 */
public class PongMainActivity extends Activity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener{

	TextView score1TV;
	TextView score2TV;
	SeekBar speedSB;
	SeekBar widthSB;
	Button reset;
	Button addBall;
	PongAnimator Pong;


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

		// link to listener
		speedSB.setOnSeekBarChangeListener(this);
		widthSB.setOnSeekBarChangeListener(this);
		reset.setOnClickListener(this);
		addBall.setOnClickListener(this);

		speedSB.setProgress(Pong.getSpeed());
		widthSB.setProgress(Pong.getPaddleWidth());


	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.resetBTN:
				Pong.balls.clear();
			case R.id.addBallBTN:
				Pong.addBall();
				speedSB.setProgress(Pong.averageBallSpeed());
				break;

			default:
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if(seekBar.getId()==R.id.ballSpeed) {
			Pong.setSpeed(progress);
		}
		else if(seekBar.getId()==R.id.paddleWidth){
			Pong.setPaddleWidth(progress+50);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}
}