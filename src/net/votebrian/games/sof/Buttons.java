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
 *  along with SmokeOrFire.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.votebrian.games.sof;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.util.Log;

public class Buttons {
    private Context mCtx;
    private Global  gbl;

    private Btn mBtnHigher;
    private Btn mBtnLower;
    private Btn mBtnSmoke;
    private Btn mBtnFire;

    private float mBtnBaseZ = 5.2f;
    private float[] mBtn;

    public Buttons(Context context, GL10 gl) {
        mCtx = context;
        gbl = (Global) mCtx.getApplicationContext();

        mBtnHigher = new Btn(mCtx, gl);
        mBtnLower = new Btn(mCtx, gl);
        mBtnSmoke = new Btn(mCtx, gl);
        mBtnFire = new Btn(mCtx, gl);

        mBtnHigher.setModelColor(gbl.SETTLE);
        mBtnLower.setModelColor(gbl.SETTLE);
        mBtnSmoke.setModelColor(gbl.SETTLE);
        mBtnFire.setModelColor(gbl.SETTLE);

        enableAll();
        disableRelative();
    }

    public void draw(GL10 gl) {
        mBtnHigher.draw(gl);
        mBtnLower.draw(gl);
        mBtnSmoke.draw(gl);
        mBtnFire.draw(gl);
    }

    public void highlightBtn(int btn) {

        mBtnHigher.setModelColor(gbl.SETTLE);
        mBtnLower.setModelColor(gbl.SETTLE);
        mBtnSmoke.setModelColor(gbl.SETTLE);
        mBtnFire.setModelColor(gbl.SETTLE);

        switch (btn) {
            case Global.SMOKE:
                mBtnSmoke.setModelColor(gbl.BLACK);
                sink(btn);
                // mBtnSmoke.setRotOffset(2f);
                break;
            case Global.FIRE:
                mBtnFire.setModelColor(gbl.RED);
                sink(btn);
                break;
            case Global.HIGHER:
                mBtnHigher.setModelColor(gbl.WHITE);
                sink(btn);
                break;
            case Global.LOWER:
                mBtnLower.setModelColor(gbl.WHITE);
                sink(btn);
                break;
            default:
                break;
        }
    }

    private void sink(final int index) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                long curr = System.currentTimeMillis();
                long startTime = curr;

                int duration = 50;
                float fullRotation = 35f;
                while(curr < startTime + duration) {
                    float x = fullRotation*(curr-startTime)/duration;

                    switch(index) {
                        case Global.SMOKE:
                            mBtnSmoke.setRotOffset(x);
                            break;
                        case Global.FIRE:
                            mBtnFire.setRotOffset(x);
                            break;
                        case Global.HIGHER:
                            mBtnHigher.setRotOffset(x);
                            break;
                        case Global.LOWER:
                            mBtnLower.setRotOffset(x);
                            break;
                    }

                    curr = System.currentTimeMillis();
                }

                switch(index) {
                    case Global.SMOKE:
                        mBtnSmoke.setRotOffset(fullRotation);
                        break;
                    case Global.FIRE:
                        mBtnFire.setRotOffset(fullRotation);
                        break;
                    case Global.HIGHER:
                        mBtnHigher.setRotOffset(fullRotation);
                        break;
                    case Global.LOWER:
                        mBtnLower.setRotOffset(fullRotation);
                        break;
                }
            }
        });

        t.run();
    }

    public void highlightAll() {
        mBtnHigher.setModelColor(gbl.WHITE);
        mBtnFire.setModelColor(gbl.RED);
        mBtnLower.setModelColor(gbl.WHITE);
        mBtnSmoke.setModelColor(gbl.BLACK);
    }

    public void highlightAbsolute() {
        mBtnFire.setModelColor(gbl.RED);
        mBtnSmoke.setModelColor(gbl.BLACK);
    }

    public void disableAll() {
        mBtnHigher.disableModel();
        mBtnHigher.disableOutline();

        mBtnLower.disableModel();
        mBtnLower.disableOutline();

        mBtnSmoke.disableModel();
        mBtnSmoke.disableOutline();

        mBtnFire.disableModel();
        mBtnFire.disableOutline();
    }

    public void disableRelative() {
        // mBtnHigher.disableModel();
        mBtnHigher.disableOutline();

        // mBtnLower.disableModel();
        mBtnLower.disableOutline();
    }

    public void enableAll() {
        mBtnHigher.enableModel();
        mBtnHigher.enableOutline();
        mBtnHigher.setModelColor(gbl.SETTLE);
        mBtnHigher.setOutlineColor(gbl.BLUE);

        mBtnLower.enableModel();
        mBtnLower.enableOutline();
        mBtnLower.setModelColor(gbl.SETTLE);
        mBtnLower.setOutlineColor(gbl.BLUE);

        mBtnSmoke.enableModel();
        mBtnSmoke.enableOutline();
        mBtnSmoke.setModelColor(gbl.SETTLE);
        mBtnSmoke.setOutlineColor(gbl.BLUE);

        mBtnFire.enableModel();
        mBtnFire.enableOutline();
        mBtnFire.setModelColor(gbl.SETTLE);
        mBtnFire.setOutlineColor(gbl.BLUE);
    }

    public void enableRelative() {
        mBtnHigher.enableModel();
        mBtnHigher.enableOutline();
        mBtnHigher.setModelColor(gbl.SETTLE);
        mBtnHigher.setOutlineColor(gbl.BLUE);

        mBtnLower.enableModel();
        mBtnLower.enableOutline();
        mBtnLower.setModelColor(gbl.SETTLE);
        mBtnLower.setOutlineColor(gbl.BLUE);
    }

    public void enableAbsolute() {
        mBtnSmoke.enableModel();
        mBtnSmoke.enableOutline();
        mBtnSmoke.setModelColor(gbl.SETTLE);
        mBtnSmoke.setOutlineColor(gbl.BLUE);

        mBtnFire.enableModel();
        mBtnFire.enableOutline();
        mBtnFire.setModelColor(gbl.SETTLE);
        mBtnFire.setOutlineColor(gbl.BLUE);
    }


    public void settle() {
        mBtnHigher.setModelColor(gbl.SETTLE);
        mBtnHigher.setRotOffset(0f);

        mBtnFire.setModelColor(gbl.SETTLE);
        mBtnFire.setRotOffset(0f);

        mBtnLower.setModelColor(gbl.SETTLE);
        mBtnLower.setRotOffset(0f);

        mBtnSmoke.setModelColor(gbl.SETTLE);
        mBtnSmoke.setRotOffset(0f);
    }

    public void setVertices(float viewW, float viewH, float viewAngle) {
        float ratio = viewW / viewH;

        float h = (float) (mBtnBaseZ * (Math.tan(Math.toRadians(viewAngle))));
        float w = h * ratio;

        // HIGHER
        mBtn = new float[9];

        mBtn[0] = -w;
        mBtn[1] =  h;
        mBtn[2] = -mBtnBaseZ;

        mBtn[3] =  0;
        mBtn[4] =  h/10;
        mBtn[5] = -mBtnBaseZ;

        mBtn[6] =  w;
        mBtn[7] =  h;
        mBtn[8] = -mBtnBaseZ;

        mBtnHigher.setVertices(mBtn);
        mBtnHigher.setOffsets(0, h, -mBtnBaseZ);
        mBtnHigher.setRotAxes(new float[] {1f, 0f, 0f});


        // LOWER
        mBtn = new float[9];

        mBtn[0] = -w;
        mBtn[1] = -h;
        mBtn[2] = -mBtnBaseZ;

        mBtn[3] =  w;
        mBtn[4] = -h;
        mBtn[5] = -mBtnBaseZ;

        mBtn[6] =  0;
        mBtn[7] = -h/10;
        mBtn[8] = -mBtnBaseZ;

        mBtnLower.setVertices(mBtn);
        mBtnLower.setOffsets(0, -h, -mBtnBaseZ);
        mBtnLower.setRotAxes(new float[] {-1f, 0f, 0f});


        // SMOKE
        mBtn = new float[9];

        mBtn[0] = -w;
        mBtn[1] =  h;
        mBtn[2] = -mBtnBaseZ;

        mBtn[3] = -w;
        mBtn[4] = -h;
        mBtn[5] = -mBtnBaseZ;

        mBtn[6] = -w/10;
        mBtn[7] =  0;
        mBtn[8] = -mBtnBaseZ;

        mBtnSmoke.setVertices(mBtn);
        mBtnSmoke.setOffsets(-w, 0, -mBtnBaseZ);
        mBtnSmoke.setRotAxes(new float[] {0f, 1f, 0f});


        // FIRE
        mBtn = new float[9];

        mBtn[0] =  w;
        mBtn[1] =  h;
        mBtn[2] = -mBtnBaseZ;

        mBtn[3] =  w/10;
        mBtn[4] =  0;
        mBtn[5] = -mBtnBaseZ;

        mBtn[6] =  w;
        mBtn[7] = -h;
        mBtn[8] = -mBtnBaseZ;

        mBtnFire.setVertices(mBtn);
        mBtnFire.setOffsets(w, 0, -mBtnBaseZ);
        mBtnFire.setRotAxes(new float[] {0f, -1f, 0f});



        mBtnHigher.setOutlineIndices(new int[] {0, 1, 1, 2});
        mBtnLower.setOutlineIndices(new int[] {0, 2, 1, 2});
        mBtnSmoke.setOutlineIndices(new int[] {0, 2, 1, 2});
        mBtnFire.setOutlineIndices(new int[] {0, 1, 2, 1});
    }
}