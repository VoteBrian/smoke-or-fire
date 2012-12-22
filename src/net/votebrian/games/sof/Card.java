/*
 *  Copyright 2012 Brian Flores
 *
 *  This file is part of SmokeOrFire.
 *
 *  SmokeOrFire is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SmokeOrFire is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

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