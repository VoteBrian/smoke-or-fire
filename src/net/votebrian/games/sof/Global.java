package net.votebrian.games.sof;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class Global extends Application {
    // context for the calling activity
    private Activity mAct;
    private Context mCtx;

    private static int something = 0;
    private static Deck mDeck;

    private static int mBlendFunc = 0;

    private static float mXAngle = 0;
    private static float mYAngle = 0;

    private int mClearable = 0;

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

    public enum Suit {
        HEARTS, DIAMONDS, CLUBS, SPADES;
    }

    public Global() {
        //mDeck = new Deck(this);
    }

    public void createDeck(GL10 gl) {
        mDeck = new Deck(this, gl);
    }

    public int getSomething() {
        return something;
    }

    public void setSomething(int num) {
        something = num;
    }

    public void draw(GL10 gl) {
        mDeck.draw(gl);
    }

    public void loadTexture(GL10 gl) {
        mDeck.loadTexture(gl);
    }

    public void reset() {
        mDeck.reset();
    }

    public float getXAngle() {
        return mXAngle;
    }

    public float getYAngle() {
        return mYAngle;
    }

    public void burnTable() {
        if(mClearable == 1) {
            mDeck.burnTable();
            mClearable = 0;

            // whenever the table is cleared, counters should be cleared
            // mCtx.resetCounters();
        }
    }

    public int getClearable() {
        return mClearable;
    }

    public void setClearable(int c) {
        if(c == 1) {
            mClearable = 1;
        } else {
            mClearable = 0;
        }
    }
}