package fi.qvik.android.qvikpuzzlechecker;

import android.graphics.Color;

/**
 * Created by villevalta on 19/01/16.
 */
public class Config {

    // Use A-z0-9.. because not all bytes are allowed in SharedPreferences XML file.
    public static final byte SQUARE_EMPTY = (byte) 'e';
    public static final byte SQUARE_WALL = (byte) 'w';
    public static final byte SQUARE_START = (byte) 's';
    public static final byte SQUARE_GOAL = (byte) 'g';

    public static final byte CMD_FWD = (byte) 'F';
    public static final byte CMD_BWDS = (byte) 'B';
    public static final byte CMD_TURN_LEFT = (byte) 'L';
    public static final byte CMD_TURN_RIGHT = (byte) 'R';
    public static final byte CMD_F1 = (byte) '1';
    public static final byte CMD_F2 = (byte) '2';

    public static final int MAX_STEPS = 10000;

    public static final int LEVEL_DEFAULT_SIZE = 16;

    public static final int COLOR_BACKGROUND = Color.WHITE;
    public static final int COLOR_WALL = Color.rgb(0x9c,0x27,0xb0);//Color.RED;//0x9c27b0;
    public static final int COLOR_START = Color.BLACK;
    public static final int COLOR_GOAL = Color.BLUE;

    public static final int COLOR_PLAYER = Color.RED;

    public static final String LEVELS_FILE = "levels";

}
