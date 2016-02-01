package fi.qvik.android.qvikpuzzlechecker.game.animation;

/**
 * Created by villevalta on 26/01/16.
 */
public class MoveFrame extends Frame{

    public int toX;
    public int toY;

    public MoveFrame(int toX, int toY) {
        this.toX = toX;
        this.toY = toY;
    }
}
