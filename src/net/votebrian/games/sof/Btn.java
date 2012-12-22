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

public class Btn extends Model {
    private Global gbl;
    private float mRotOffset = 0;
    private float mXOffset;
    private float mYOffset;
    private float mZOffset;
    private float[] mRotAxes = {0f, 0f, 0f};

    public Btn(Context context, GL10 gl) {
        super(context, gl);

        gbl = (Global) context.getApplicationContext();
    }

    public void draw(GL10 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(mXOffset, mYOffset, mZOffset);
        gl.glRotatef(mRotOffset, mRotAxes[0], mRotAxes[1], mRotAxes[2]);
        gl.glTranslatef(-mXOffset, -mYOffset, -mZOffset);
        super.draw(gl);
        gl.glPopMatrix();
    }

    public void setOffsets(float x, float y, float z) {
        mXOffset = x;
        mYOffset = y;
        mZOffset = z;
    }

    public void setRotAxes(float[] axes){
        mRotAxes = axes;
    }

    public void setRotOffset(float offset){
        mRotOffset = offset;
    }
}