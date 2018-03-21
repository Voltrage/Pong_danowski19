package danowski19.pong_danowski19;

import android.graphics.Rect;

/**
 * Created by Luke on 3/19/2018.
 */

public class Paddle {
    private int width;
    private int location;

    private Rect paddle;

    public Paddle(int height, int location) {
        this.width = height;
        this.location = location;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }
}
