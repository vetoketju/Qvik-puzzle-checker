package fi.qvik.android.qvikpuzzlechecker;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import fi.qvik.android.qvikpuzzlechecker.model.Level;

/**
 * Created by villevalta on 19/01/16.
 * Usage: QvikPuzzleApplication.getInstace().getLevelUtil();
 */
public class LevelUtil {

    private static final String TAG = "LevelUtil";
    SharedPreferences levels;

    public LevelUtil(Context context){
        levels = context.getSharedPreferences(Config.LEVELS_FILE, Context.MODE_PRIVATE);
    }

    public boolean saveLevelToDisk(Level level){

        try {
            levels.edit().putString(level.getName(), level.toString()).apply(); // Apply is async
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public Level loadLevelFromDisk(String name){

        String map = levels.getString(name, null);
        if(map != null){
            return new Level(name, map);
        }else return null;
    }

    public boolean levelExists(String name){
        return levels.contains(name);
    }

    public Level getDefaultLevel(){

        byte[][] map = new byte[][]{
                {Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL},
                {Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY},
                {Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY},
                {Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY},
                {Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY},
                {Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY},
                {Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY},
                {Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY},
                {Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY},
                {Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY},
                {Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY},
                {Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY},
                {Config.SQUARE_GOAL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY},
                {Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY},
                {Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY},
                {Config.SQUARE_START, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_WALL, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY, Config.SQUARE_EMPTY}
        };

        try {
            Level l =  new Level(map, "Original");
            l.setStartOrientation(1);
            l.setEditable(false);
            return l;
        }catch (IllegalArgumentException e){
            Log.w(TAG, e.getMessage());
            return null;
        }
    }

    public ArrayList<Level> loadAll(){
        ArrayList<Level> result = new ArrayList<>();

        Map<String, ?> all = levels.getAll();

        for (Map.Entry<String, ?> one : all.entrySet()) {
            if(one.getValue() instanceof String){
                try {
                    result.add(new Level(one.getKey(), (String)one.getValue()));
                }catch (IllegalArgumentException e){
                    Log.w(TAG, e.getMessage());
                }
            }
        }

        Collections.sort(result);

        Level defaultLevel = getDefaultLevel();
        if (defaultLevel != null) {
            result.add(0, defaultLevel);
        }

        return result;
    }

}
