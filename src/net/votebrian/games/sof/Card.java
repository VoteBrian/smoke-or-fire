package net.votebrian.games.sof;


import javax.microedition.khronos.opengles.GL10;
import java.util.Random;

import android.content.Context;

import android.util.Log;


public class Card extends Model {

    private Global gbl;
    private Random mRandom;

    public int mValue = 0;
    public int mSuit = 0;

    private float mAngleZSkew = 0f;
    private float mPosXSkew = 0f;
    private float mPosYSkew = 0f;

    private int mState = 0;  // IN_DECK


    public Card(Context context, GL10 gl, int suit, int value) {
        super(context, gl);

        gbl = (Global) context.getApplicationContext();

        mSuit = suit;
        mValue = value;

        // Assign card random offsets
        mRandom = new Random();
        mAngleZSkew = 15 * (mRandom.nextFloat() - 0.5f);
        mPosXSkew = 0.5f * (mRandom.nextFloat() - 0.5f);
        mPosYSkew = 0.3f * (mRandom.nextFloat() - 0.5f);
    }

    @Override
    public void draw(GL10 gl) {
        if(mState != gbl.BURNT) {
            super.draw(gl);
        }
    }

    public void setState(int state) {
        mState = state;
    }

    @Override
    public void setRotation(float x, float y, float z) {
        if(mState == gbl.ON_TABLE) {
            super.setRotation(x, y, z + mAngleZSkew);
        } else {
            super.setRotation(x, y, z);
        }
    }

    @Override
    public void setPosition(float x, float y, float z) {
        if(mState == gbl.ON_TABLE) {
            super.setPosition(x + mPosXSkew, y + mPosYSkew, z);
        } else {
            super.setPosition(x, y, z);
        }
    }
}