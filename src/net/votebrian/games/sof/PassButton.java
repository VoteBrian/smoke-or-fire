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

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;

public class PassButton{
    private Context mCtx;
    private Global  gbl;

    private int mCounter = 0;
    private final int mMaxCount = 5;

    private final int mNumSegments = 5;
    private Model[] mSeg = new Model[mNumSegments];

    private float mSegBaseZ = 5.1f;

    public PassButton(Context context, GL10 gl) {
        mCtx = context;
        gbl = (Global) mCtx.getApplicationContext();

        for( int c = 0; c < mNumSegments; c++) {
            mSeg[c] = new Model(mCtx, gl);
        }
    }

    public void draw(GL10 gl) {
        for( int c = 0; c < mNumSegments; c++) {
            mSeg[c].draw(gl);
        }
    }

    public void increment() {
        mCounter++;
        if(mCounter > mMaxCount) {
            mCounter = mMaxCount;
        }

        for(int c = 0; c < mCounter; c++) {
            if(mCounter == mMaxCount) {
                mSeg[c].setModelColor(gbl.BRIGHT);
            } else {
                mSeg[c].setModelColor(gbl.HIGHLIGHT);
            }
        }
    }

    public void reset() {
        for(int c = 0; c < mNumSegments; c++) {
            mSeg[c].setModelColor(gbl.SETTLE);
        }

        mCounter = 0;
    }

    public void setVertices(float viewW, float viewH, float viewAngle) {
        float ratio = viewW / viewH;

        // window half-width and half-height
        float winH = (float) (mSegBaseZ * (Math.tan(Math.toRadians(viewAngle))));
        float winW = winH * ratio;

        float height = winH/7;
        float base   = winW/20;

        float[] mVertices = new float[18];

        mVertices[0] = 0.0f;
        mVertices[1] = height/2;
        mVertices[2] = -mSegBaseZ;

        mVertices[3] = -1 * base;
        mVertices[4] = -1 * height/2;
        mVertices[5] = -mSegBaseZ;

        mVertices[6] = 0.0f;
        mVertices[7] = -1 * height/2;
        mVertices[8] = -mSegBaseZ;


        mVertices[9] = 0.0f;
        mVertices[10] = height/2;
        mVertices[11] = -mSegBaseZ;

        mVertices[12] = -0.0f;
        mVertices[13] = -1 * height/2;
        mVertices[14] = -mSegBaseZ;

        mVertices[15] = base;
        mVertices[16] = height/2;
        mVertices[17] = -mSegBaseZ;

        for(int c = 0; c < mNumSegments; c++) {
            mSeg[c].setVertices(mVertices);
            mSeg[c].setOutlineIndices(new int[] {0, 1, 1, 2, 2, 5, 5, 0});
            mSeg[c].enableOutline();

            float x = winW - ( (mNumSegments - c + 1) * base * 1.1f );
            float y = -1 * winH + height;
            mSeg[c].setPosition(x, y, 0);

            mSeg[c].setModelColor(gbl.SETTLE);
            mSeg[c].setOutlineColor(gbl.BLUE);
        }
    }
}