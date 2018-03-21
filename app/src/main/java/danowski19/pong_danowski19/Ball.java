package danowski19.pong_danowski19;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Random;

/**
 * Created by Luke on 3/19/2018.
 */

public class Ball {

    private Point center;
    private final int radius = 50;
    private float slopeX;
    private float slopeY;
    private float velocity;

    public Ball(int wall) {
        Random gen = new Random();
        slopeY = gen.nextFloat();
        slopeX = (float) Math.sqrt(1.0 - slopeY * slopeY);
//        velocity = gen.nextInt(91)+10;
        velocity = 10;
//        rect = new Rect(g.getHeight()/2-radius, g.getWidth()/2-radius, g.getHeight()/2+radius, g.getWidth()/2+radius);
        int cx = wall + radius;
        int cy = wall + radius;
        center = new Point(cx, cy);
//        rect = new Rect(wall+1,wall+1,wall+2*radius,wall+2*radius);
    }

    public Rect getRect(Point n) {
        return new Rect(n.y - radius, n.x - radius, n.y + radius, n.x + radius);
    }

    /**
     * Used to modify slopes before actually moving the ball
     *
     * @return a Rect of where the next moveBall will land
     */
    public Rect getShadow(){
        Point copy = new Point(center);
        copy.offset(Math.round(velocity*slopeX), Math.round(velocity*slopeY));
        return getRect(copy);
    }

    /**
     * changes the position based off slope and velocity
     */
    public void moveBall() {
        center.offset(Math.round(velocity*slopeX), Math.round(velocity*slopeY));
    }


    public void hitSide() {
        slopeY = -slopeY;
    }


    public void hitPaddle() {
        slopeX = -slopeX;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public int getRadius() {
        return radius;
    }

    public Point getCenter() {
        return center;
    }
}
