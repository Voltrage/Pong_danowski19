package danowski19.pong_danowski19;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Luke on 3/28/2018.
 */

public class InvisibleBall extends Ball {

    public InvisibleBall(Rect validArea){
        super(validArea);
    }

    @Override
    public void draw(Canvas g, Paint ballColor){
    //do nothing
    }

}
