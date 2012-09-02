package net.votebrian.games.sof;

import javax.microedition.khronos.opengles.GL10;

import android.app.Application;

public class Global extends Application {
    private static int something = 0;
    private static Deck mDeck;

    private static int mBlendFunc = 0;

    private static float mXAngle = 0;
    private static float mYAngle = 0;

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

    public void setXAngle(float angle) {
        mDeck.setXAngle(angle);
        // mXAngle += angle;
    }

    public void setYAngle(float angle) {
        mDeck.setYAngle(angle);
        // mYAngle += angle;
    }

    public void loadTexture(GL10 gl) {
        mDeck.loadTexture(gl);
    }

    public void reset() {
        // mDeck.reset();
    }

    public float getXAngle() {
        return mXAngle;
    }

    public float getYAngle() {
        return mYAngle;
    }

    public void deal() {
        mDeck.deal();
    }
}