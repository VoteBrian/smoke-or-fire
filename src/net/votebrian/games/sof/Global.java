package net.votebrian.games.sof;

import javax.microedition.khronos.opengles.GL10;

import android.app.Application;

public class Global extends Application {
    private static int something = 0;
    private Deck mDeck;

    private static int mBlendFunc = 0;

    public Global() {
        mDeck = new Deck();
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
        // mod1.setXAngle(angle);
    }

    public void setYAngle(float angle) {
        // mod1.setYAngle(angle);
    }

    public void loadTexture(GL10 gl) {
        mDeck.loadTexture(gl);
    }

    public void reset() {
        // mDeck.reset();
    }
}