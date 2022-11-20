package org.jyman;

public class Toy {
    private long message;
    private int angle;
    private boolean reversed;
    private int stuckCount;
    public Toy() {
        this.message = 0;
    }

    public long getMessage() {
        return message;
    }

    public void setMessage(long message) {
        if (message == this.message) {
            this.stuckCount += 1;
        } else {
            this.resetStuckCount();
        }
        this.message = message;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getAngle() {
        return angle;
    }

    public boolean isReversed() {
        return reversed;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    public void resetStuckCount() {
        this.stuckCount = 0;
    }

    public int getStuckCount() {
        return this.stuckCount;
    }
}
