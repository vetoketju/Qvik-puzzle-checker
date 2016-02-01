package fi.qvik.android.qvikpuzzlechecker;

import android.app.Application;

/**
 * Created by villevalta on 20/01/16.
 */
public class QvikPuzzleApplication extends Application {

    private LevelUtil levelUtil;
    private static QvikPuzzleApplication instance;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        levelUtil = new LevelUtil(this);
    }

    public static QvikPuzzleApplication getInstance(){
        return instance;
    }

    public LevelUtil getLevelUtil(){
        return levelUtil;
    }
}
