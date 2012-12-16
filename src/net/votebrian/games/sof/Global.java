package net.votebrian.games.sof;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.app.Application;

public class Global extends Application {

    public static final int SMOKE  = 0;
    public static final int FIRE   = 1;
    public static final int HIGHER = 2;
    public static final int LOWER  = 3;
    public static final int PASS   = 4;

    public static final int HEARTS   = 0;
    public static final int DIAMONDS = 1;
    public static final int CLUBS    = 2;
    public static final int SPADES   = 3;

    public static final int BAD    = 0;
    public static final int GOOD   = 1;
    public static final int SOCIAL = 2;

    public static final int IN_DECK  = 0;
    public static final int ON_TABLE = 1;
    public static final int BURNT    = 3;

    public float[] SETTLE = {0.2f, 0.7f, 0.9f, 0.05f};
    public float[] HIGHLIGHT = {0.2f, 0.7f, 0.9f, 0.5f};
    public float[] OFF = {0.0f, 0.0f, 0.0f, 0.0f};
    public float[] BLUE = {0.2f, 0.7f, 0.9f, 0.5f};
    public float[] BRIGHT = {0.2f, 0.7f, 0.9f, 1.0f};

    public Global() {
        //mDeck = new Deck(this);
    }
}