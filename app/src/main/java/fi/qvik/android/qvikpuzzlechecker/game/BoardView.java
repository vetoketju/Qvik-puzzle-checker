package fi.qvik.android.qvikpuzzlechecker.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import fi.qvik.android.qvikpuzzlechecker.Config;
import fi.qvik.android.qvikpuzzlechecker.model.Level;

/**
 * Created by villevalta on 19/01/16.
 */
public class BoardView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    private static final String TAG = "BoardView";
    Level level;
    boolean editMode;
    byte brush = Config.SQUARE_EMPTY;

    // the players square on board (will be multiplied by square size)
    private int playerSquareX = 0;
    private int playerSquareY = 0;
    private int playerOrientation = 0; // 0 > // 1 v // 2 < // 3 ^

    private int square_width = 0; // calculate this according to screen and level.width
    private int square_height = 0;

    private int boardWidth;
    private int boardHeight;

    private Paint squarePaint = new Paint();
    private Paint squarePaintStroke = new Paint();
    private Paint playerPaint = new Paint();

    private int initialPlayerX;
    private int initialPlayerY;
    private int initialPlayerOrientation;

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoardView(Context context) {
        super(context);
        init();
    }

    private void init(){
        squarePaint.setStyle(Paint.Style.FILL);

        squarePaintStroke.setStyle(Paint.Style.STROKE);
        squarePaintStroke.setColor(Color.BLACK);
        squarePaintStroke.setStrokeWidth(1f);

        playerPaint.setStyle(Paint.Style.FILL);
        playerPaint.setColor(Config.COLOR_PLAYER);

        getHolder().addCallback(this);
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        if(editMode){
            this.setOnTouchListener(this);
        }
    }

    public void setLevel(Level level) {
        this.level = level;
        drawBoard();
    }

    public Level getLevel() {
        return level;
    }

    public void setBrush(byte brush) {
        this.brush = brush;
    }

    private void drawBoard(){

        if(level == null) return;

        if(getHolder().getSurface().isValid()){
            try {
                Canvas c = getHolder().lockCanvas();

                c.drawColor(Config.COLOR_BACKGROUND); // Clear the canvas with background

                int squareColor;

                for (int y = 0; y < level.getSize(); y++) {
                    for (int x = 0; x < level.getSize(); x++) {

                        if(level.isWall(y, x)){
                            squareColor = Config.COLOR_WALL;
                        }else if(level.isStart(y, x)){
                            squareColor = Config.COLOR_START;
                        }else if(level.isGoal(y, x)){
                            squareColor = Config.COLOR_GOAL;
                        }else{
                            squareColor = Config.COLOR_BACKGROUND;  // when empty
                        }

                        squarePaint.setColor(squareColor);

                        Rect r = new Rect(x * square_width, y * square_height ,(x + 1) * square_width, (y + 1) * square_height);

                        c.drawRect(r, squarePaint);
                        c.drawRect(r, squarePaintStroke);
                    }
                }

                c.drawPath(getPlayer(playerSquareX * square_width, playerSquareY * square_height), playerPaint);

                getHolder().unlockCanvasAndPost(c);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void setPlayerSquare(int x, int y){
        playerSquareX = x;
        playerSquareY = y;
        drawBoard();
    }

    public void setPlayerOrientation(@IntRange(from = 0, to = 3) int rotation){
        playerOrientation = rotation;
        drawBoard();
    }

    public int getPlayerOrientation() {
        return playerOrientation;
    }

    /**
     *
     * X Y point is the upper left corner of the square where player is
     *
     * @param x The lef/top position of square where player is
     * @param y The top position of square where player is
     * @return nice arrow shape positioned correctly representing the player
     */
    private Path getPlayer(int x, int y){

        Path p = new Path();

        p.setFillType(Path.FillType.EVEN_ODD);

        p.moveTo(x + (square_width / 2),y);
        p.lineTo(x + square_width, y + square_height);
        p.lineTo(x + (square_width / 2), y + (square_height / 2));
        p.lineTo(x, y + square_height);
        p.lineTo(x + (square_width / 2),y);
        p.close();

        // Set angle
        Matrix matrix = new Matrix();
        matrix.postRotate(playerOrientation * 90, x + (square_width / 2), y + (square_height / 2));
        p.transform(matrix);

        return p;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec); // aspectratio must be 1:1 (for now...)
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        /*
        boardWidth = this.getMeasuredWidth();
        boardHeight = this.getMeasuredHeight();
        recalculateSizes();
        drawBoard();
        */
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        boardWidth = width;
        boardHeight = height;
        recalculateSizes();
        drawBoard();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void recalculateSizes(){
        square_width = boardWidth / level.getSize();
        square_height = boardHeight / level.getSize();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!editMode)return false;

        if(event.getAction() == MotionEvent.ACTION_UP){
            Log.w(TAG, "onTouch: x="+event.getX()+", y="+event.getY());
            int xInMap = (int) Math.floor(event.getX() / square_width);
            int yInMap = (int) Math.floor(event.getY() / square_height);

            if(level.isInBounds(yInMap, xInMap)){
                level.set(yInMap, xInMap, brush);
                if(brush == Config.SQUARE_START){
                    playerSquareX = xInMap;
                    playerSquareY = yInMap;
                }
                drawBoard();
            }
        }

        return true;
    }

    public void setPlayerStart(int x, int y, int startOrientation) {
        initialPlayerOrientation = startOrientation;
        initialPlayerX = x;
        initialPlayerY = y;

        reset();
    }

    public void reset() {

        playerSquareY = initialPlayerY;
        playerSquareX = initialPlayerX;
        playerOrientation = initialPlayerOrientation;

        drawBoard();
    }

    public void fill(byte brushFill) {
        level.fill(brushFill);
        drawBoard();
    }
}
