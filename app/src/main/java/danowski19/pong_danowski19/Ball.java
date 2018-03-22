package danowski19.pong_danowski19;

import android.graphics.Point;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by Luke on 3/19/2018.
 *
 * contains all information relevant to each Pong Ball in play, no rules here, just physics
 *
 * @author Luke Danowski
 * @version March 2018
 */

public class Ball {

    //public here for easy access and can adjust to fix a bug
    public Point center;
    private final int radius = 50;
    private float slopeX;
    private float slopeY;
    private float velocity;
    private Random gen;

    public Ball(int wall) {
        gen = new Random();

        //random direction
        slopeY = gen.nextFloat()/2+0.5f;
        slopeX = (float) Math.sqrt(1.0 - slopeY * slopeY);

        //random velocity
        velocity = gen.nextInt(20)+10;

        //random starting position
        int cx = gen.nextInt(100) + wall + radius;
        int cy = gen.nextInt(100) + wall + radius;
        center = new Point(cx, cy);
//        rect = new Rect(wall+1,wall+1,wall+2*radius,wall+2*radius);
    }

    private Rect getRect(Point n) {
//        return new Rect(n.y - radius, n.x - radius, n.y + radius, n.x + radius);
        return new Rect(n.x - radius, n.y - radius, n.x + radius, n.y + radius);

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
        slopeX = -slopeX;
    }

    public void hitPaddle() {
        slopeY = -slopeY;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public int getRadius() {
        return radius;
    }

    public Point getCenter() {
        return center;
    }

    public float getVelocity() {
        return velocity;
    }

    public int top(){ return center.y - radius;}

}
