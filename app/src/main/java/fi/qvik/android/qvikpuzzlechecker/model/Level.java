package fi.qvik.android.qvikpuzzlechecker.model;

import android.graphics.Bitmap;

import fi.qvik.android.qvikpuzzlechecker.Config;

/**
 * Created by villevalta on 19/01/16.
 * LEVEL IS ALWAYS SQUARE
 */
public class Level implements Comparable {

    private byte[][] map;
    private int size; // count of squares = size^2
    private String name;
    private boolean isEditable = true;
    private int startOrientation = 0; // TODO MAKE EDITABLE

    public Level(){
        map = new byte[Config.LEVEL_DEFAULT_SIZE][Config.LEVEL_DEFAULT_SIZE];
        size = Config.LEVEL_DEFAULT_SIZE;
    }

    public Level(int size){ // w = x, h = y
        this.size = size;
        map = new byte[size][size];
    }

    public Bitmap getPreviewImage(){

        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        int[] pixels = new int[size * size];

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int color = Config.COLOR_BACKGROUND;
                switch (map[y][x]){
                    case Config.SQUARE_WALL: color = Config.COLOR_WALL; break;
                    case Config.SQUARE_START: color = Config.COLOR_START; break;
                    case Config.SQUARE_GOAL: color = Config.COLOR_GOAL; break;
                }
                pixels[(y * size) + x] = color;
            }
        }

        bitmap.setPixels(pixels, 0, size, 0, 0, size, size);

        return bitmap;
    }

    public Level(byte[][] map, String name) throws IllegalArgumentException{
        this.map = map;
        this.size = map.length;
        this.name = name;

        for (int i = 0; i < map.length; i++) {
            if(map[i].length != size) throw new IllegalArgumentException("map is not square (name="+name+")");
        }
    }

    public Level(String name, String mapString) throws IllegalArgumentException{
        double len =  Math.sqrt(mapString.length());
        if(len % 1 == 0){
            this.size = (int)len;
            this.name = name;
            map = new byte[size][size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    map[y][x] = (byte) mapString.charAt((y * size) + x);
                }
            }
        }else{
            throw new IllegalArgumentException("Malformed mapString, map is not square (name="+name+")");
        }

    }

    public void fill(byte brush){
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(isEmpty(i,j) || isWall(i,j)){
                    set(i,j,brush);
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public int[] getStart(){ // 0 = Y, 1 = X
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map.length; x++) {
                if(isStart(y,x)) return new int[]{y,x};
            }
        }
        // Not found...
        return new int[] {0,0};
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                sb.append((char) map[y][x]); // converts byte to char
            }
        }

        return sb.toString();
    }

    public int getSize() {
        return size;
    }

    public byte[][] getMap() {
        return map;
    }

    public void set(int y, int x, byte value){
        if(value == Config.SQUARE_GOAL || value == Config.SQUARE_START){
            replaceAll(value, Config.SQUARE_EMPTY); // Remove old goal or start
        }
        map[y][x] = value;
    }

    public byte get(int y, int x){
        return map[y][x];
    }

    public boolean isEmpty(int y, int x){
        return isInBounds(y, x) && map[y][x] == Config.SQUARE_EMPTY;
    }

    public boolean isWall(int y, int x){
        return !isInBounds(y,x) || map[y][x] == Config.SQUARE_WALL;
    }

    public boolean isStart(int y, int x){
        return isInBounds(y,x) && map[y][x] == Config.SQUARE_START;
    }

    public boolean isGoal(int y, int x){
        return isInBounds(y,x) && map[y][x] == Config.SQUARE_GOAL;
    }

    public boolean isOpen(int y, int x){
        return isEmpty(y,x) || isGoal(y,x) || isStart(y,x);
    }

    public boolean isInBounds(int y, int x){
        return x >= 0 && y >= 0 && x < size && y < size;
    }

    private void replaceAll(byte oldVal, byte newVal){
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map.length; x++) {
                if(map[y][x] == oldVal) map[y][x] = newVal;
            }
        }
    }

    public void setStartOrientation(int startOrientation) {
        this.startOrientation = startOrientation;
    }

    public int getStartOrientation() {
        return startOrientation;
    }

    @Override
    public int compareTo(Object another) {
        return name.compareTo(((Level)another).getName());
    }
}