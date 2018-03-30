package danowski19.pong_danowski19;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by Luke on 3/29/2018.
 */

public class Paddle {

    public Rect box;
//    public Point paddleMid;
    public int paddleWidth;
    private int wallWidth;
    private Canvas ref;
    public boolean isTop;
    public boolean is2D;
    public float dy;

    public Paddle(Canvas g, int paddleWidth, int wallWidth, Point mid, boolean isTop, boolean is2D) {
        this.isTop = isTop;
        this.is2D = is2D;
        this.ref = g;
        this.paddleWidth=paddleWidth;
//        this.paddleMid=mid;
        this.wallWidth=wallWidth;

        box = new Rect(0, 0, paddleWidth, wallWidth);

        setPaddleMid(mid);
//
//        int right = (mid.x + paddleWidth + mid.x)/2;
//        int left = right - paddleWidth;
//
//        if(isTop) {
//            box = new Rect(left, 0, right, wallWidth);
//        }
//        else {
//            box = new Rect( left, g.getHeight() - wallWidth, right, g.getHeight());
//        }

    }

//    private void updateBox(){
//        box.offsetTo(paddleMid.x-paddleWidth, paddleMid.y);
//    }

    public void setPaddleWidth(int newWidth) {

        int difference = paddleWidth - newWidth;
        paddleWidth = newWidth;
        box.inset(difference,0);
        setPaddleMid(new Point(box.centerX(),box.centerY()));

    }

    /**
     * given a point, the paddle is set to within a bounded area
     * @param newLoc
     */
    public void setPaddleMid(Point newLoc) {
        //

        int rightLimit = ref.getWidth() - wallWidth - (paddleWidth / 2);
        int leftLimit = wallWidth + paddleWidth / 2;
        int yLimitTop;
        int yLimitBottom;
        int x=0;
        int y=0;
        if(is2D) { //case of 2D
            if (isTop) {
                yLimitTop = 0;
                yLimitBottom = ref.getHeight()/3;
            }
            else{
                yLimitTop = ref.getHeight() - ref.getHeight()/3;
                yLimitBottom = ref.getHeight()+wallWidth;
            }

            x = (newLoc.x > leftLimit)? ((newLoc.x < rightLimit)? newLoc.x : rightLimit) : leftLimit;
            y = (newLoc.y < yLimitBottom)? ((newLoc.y > yLimitTop)? newLoc.y : yLimitTop) : yLimitBottom;

        }
        else { //case of 1D
            if (isTop) {
                yLimitTop = 0;
                yLimitBottom = 0;
            }
            else{
                yLimitTop = ref.getHeight() - wallWidth;
                yLimitBottom = ref.getHeight() - wallWidth;
            }

            x = (newLoc.x > leftLimit)? ((newLoc.x < rightLimit)? newLoc.x : rightLimit) : leftLimit;
            y = (newLoc.y < yLimitBottom)? ((newLoc.y > yLimitTop)? newLoc.y : yLimitTop) : yLimitBottom;

        }

        //record change in Y
        dy = (y - box.centerY())/wallWidth;

        box.offsetTo(x-paddleWidth/2, y);
    }

    /**
     * draw method for paddles
     * @param g
     * @param paddleCol
     */
    public void draw(Canvas g, Paint paddleCol) {
        ref = g;
        g.drawRect(box, paddleCol);
    }

}
