package fi.qvik.android.qvikpuzzlechecker;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.RandomAccess;

import fi.qvik.android.qvikpuzzlechecker.model.Level;

/**
 * Created by villevalta on 21/01/16.
 */
public class SolutionChecker {

    private static final String TAG = "SolutionChecker";

    PlayerListener listener;

    Level level;
    Player player;

    byte[] f_main;
    byte[] f_1;
    byte[] f_2;

    ByteArrayOutputStream allCommands;
    boolean completed;

    // loop detection (player moving the same pattern)

    public SolutionChecker(Level level) {
        this.level = level;
        resetPlayer();
    }

    public void setListener(PlayerListener listener) {
        this.listener = listener;
    }

    public void setF_main(byte[] f_main) {
        this.f_main = f_main;
    }

    public void setF_1(byte[] f_1) {
        this.f_1 = f_1;
    }

    public void setF_2(byte[] f_2) {
        this.f_2 = f_2;
    }

    // TODO: Return "Report" that has bool finishes, int all steps, int score, bool infinite loop detected
    public boolean check(){ // Todo: add "speed" as parameter -> 0 instant, 1000ms -> reports player position every second, (step by step?)

        if(f_main == null) return false;

        allCommands = new ByteArrayOutputStream();
        completed = false;
        resetPlayer();

        runMethod(f_main);

        try {
            allCommands.close(); // does nothing...
        } catch (IOException e) {
            e.printStackTrace();
        }

        return completed;
    }

    private void runMethod(byte[] method){

        for (int i = 0; i < method.length; i++) {
            if(allCommands.size() >= Config.MAX_STEPS || completed) {
                return;
            }

            byte command = method[i];

            switch (command){
                case Config.CMD_FWD:
                    completed = movePlayer(true);
                    allCommands.write(command);
                    break;
                case Config.CMD_BWDS:
                    completed = movePlayer(false);
                    allCommands.write(command);
                    break;
                case Config.CMD_TURN_RIGHT:
                    player.rotate(true);
                    allCommands.write(command);
                    break;
                case Config.CMD_TURN_LEFT:
                    player.rotate(false);
                    allCommands.write(command);
                    break;
                case Config.CMD_F1:
                    runMethod(f_1);
                    break;
                case Config.CMD_F2:
                    runMethod(f_2);
                    break;
            }

        }

    }


    /**
     * Moves player by one square
     * @param forwards direction
     * @return true if player moved to goal
     */
    private boolean movePlayer(boolean forwards){

        int moveX = 0;
        int moveY = 0;

        switch (player.orientation){
            case 0: moveY = -1; break;
            case 1: moveX = 1; break;
            case 2: moveY = 1; break;
            case 3: moveX = -1; break;
        }

        if(!forwards){
            moveX = -moveX;
            moveY = -moveY;
        }

        // Shift to be the target location
        moveX += player.x;
        moveY += player.y;

        if(level.isOpen(moveY, moveX)){
            player.y = moveY;
            player.x = moveX;
            if(listener != null) listener.onPlayerChanged(player, false);
            if(level.isGoal(player.y,player.x)){
                return true;
            }
        }
        return false;
    }

    private void resetPlayer(){
        int[] start = level.getStart();
        player = new Player(start[0], start[1], level.getStartOrientation());
    }

    public void reset() {
        resetPlayer();
        f_main = null;
        f_1 = null;
        f_2 = null;
    }

    public class Player{

        public int orientation;
        public int y;
        public int x;

        public Player(int y, int x, int orientation){
            this.y = y;
            this.x = x;
            this.orientation = orientation;
        }

        public void rotate(boolean toRight){
            if(toRight){
                orientation++;
                if(orientation > 3) orientation = 0;
            }else{
                orientation--;
                if(orientation < 0) orientation = 3;
            }
            if(listener != null) listener.onPlayerChanged(this, true);
        }
    }

    public interface PlayerListener
    {
        void onPlayerChanged(Player player, boolean isOrientationChange);
    }
}
