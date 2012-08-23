package net.votebrian.games.sof;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

public class Deck() {
    private Context mCtx;
    private int mZBase;
    private Card[] mCards;
    private mNumCards = 52;

    public Deck(Context context) {
        int counter = 0;
        mCtx = context;

        // generate the 52 cards
        for (int v = 0; v < 13; v++) {
            for (int s = 0; s < 4; s++) {
                mCards[counter] = new Card(0,0,mZBase + 0.016*counter, mCtx);
                counter++;
            }
        }
    }

    public void draw(GL10 gl) {
        for(int counter = 0; counter < mNumCards; counter++) {
            mCards[counter].draw(gl);
        }
    }

    public void loadTexture(GL10 gl) {
        for(int counter = 0; counter < mNumCards; counter++) {
            mCards.[counter].loadTexture(gl);
        }
    }
}