package fi.qvik.android.qvikpuzzlechecker.game.animation;

import java.util.ArrayList;

import fi.qvik.android.qvikpuzzlechecker.game.BoardView;

/**
 * Created by villevalta on 26/01/16.
 */
public class PlayerAnimator {

    public static final String TAG = "PlayerAnimator";

    private boolean animating;
    ArrayList<Frame> frames = new ArrayList<>();
    BoardView board;
    int timeBetweenFrames = 100; // millis
    AnimationListener animationListener = null;
    AnimationThread thread;

    public PlayerAnimator(BoardView board) {
        this.board = board;
    }

    public PlayerAnimator(BoardView board, ArrayList<Frame> frames) {
        this.board = board;
        this.frames = frames;
    }

    public void addFrame(Frame frame){
        frames.add(frame);
    }

    public void reset(boolean hard){
        if(hard) frames = new ArrayList<>();

        if(thread != null && thread.running){
            thread.running = false;
        }

        board.reset();
    }

    public boolean isAnimating() {
        return animating;
    }

    public void animateFromStart(){
        board.reset();
        animating = true;
        thread = new AnimationThread();
        thread.start();
    }

    public int getStepCount() {
        return frames.size();
    }

    public void setAnimationListener(AnimationListener animationListener) {
        this.animationListener = animationListener;
    }

    class AnimationThread extends Thread {

        boolean running = true;
        int animationIndex = 0;

        @Override
        public void run() {
            while(running && animationIndex < frames.size()){
                try {
                    Frame currentFrame = frames.get(animationIndex);
                    if(currentFrame instanceof MoveFrame){
                        board.setPlayerSquare(((MoveFrame) currentFrame).toX, ((MoveFrame) currentFrame).toY);
                    }else if(currentFrame instanceof OrientationFrame){
                        board.setPlayerOrientation(((OrientationFrame) currentFrame).toOrientation);
                    }
                    animationIndex++;
                    Thread.sleep(timeBetweenFrames);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            running = false;
            animating = false;
            if(animationListener != null) animationListener.onAnimationEnd();
        }
    }



    public interface AnimationListener{
        void onAnimationEnd();

    }


}
