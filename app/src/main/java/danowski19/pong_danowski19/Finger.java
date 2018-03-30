package danowski19.pong_danowski19;

import android.graphics.PointF;

/**
 *
 * used to store everything needed to now about a finger on the screen
 *
 * Created by Luke on 3/27/2018.
 */

public class Finger {
    //variables
    public float x;
    public float y;
    private float startY;

    public Finger(float x, float y){
        this.startY =y;
        this.x=x;
        this.y=y;
    }

    //since this is set when created, needed a getter
    public float getStartY(){
        return startY;
    }

    //do the dirty work
    public PointF getPointF(){
        return new PointF(x, y);
    }
}
