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

import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class Labels {
    private Context mCtx;
    private Global gbl;

    private static int BYTES_PER_VERTEX = 4;

    private Model mSmoke;
    private Model mFire;
    private Model mHigher;
    private Model mLower;

    private Bitmap mLblBitmap;

    private float mLblWidth = 0.5f;
    private float mLblBaseZ = 5.1f;

    public int[] mTexture = new int[1];

    public Boolean mRelative = false;

    public Labels(Context context, GL10 gl) {
        mCtx = context;
        gbl = (Global) mCtx.getApplicationContext();

        mSmoke  = new Model(context, gl);
        mSmoke.enableModel();

        mFire = new Model(context, gl);
        mFire.enableModel();

        mHigher = new Model(context, gl);
        mHigher.enableModel();

        mLower = new Model(context, gl);
        mLower.enableModel();

        loadTexture(gl);

        mSmoke.setTexture(mTexture[0]);
        mSmoke.setTextureBuffer( makeFloatBuffer(genTexCoords(0)) );

        mFire.setTexture(mTexture[0]);
        mFire.setTextureBuffer( makeFloatBuffer(genTexCoords(1)) );

        mHigher.setTexture(mTexture[0]);
        mHigher.setTextureBuffer( makeFloatBuffer(genTexCoords(2)) );

        mLower.setTexture(mTexture[0]);
        mLower.setTextureBuffer( makeFloatBuffer(genTexCoords(3)) );

        disable();
    }

    public void draw(GL10 gl) {
        mSmoke.draw(gl);
        mFire.draw(gl);
        mHigher.draw(gl);
        mLower.draw(gl);
    }

    public void enable() {
        mSmoke.enableModel();
        mFire.enableModel();

        if(mRelative) {
            mHigher.enableModel();
            mLower.enableModel();
        }
    }

    public void disable() {
        mSmoke.disableModel();
        mFire.disableModel();
        mHigher.disableModel();
        mLower.disableModel();
    }

    public void enableRelative() {
        mRelative = true;
    }

    public void disableRelative() {
        mRelative = false;
    }

    public void setVertices(float viewW, float viewH, float viewAngle) {
        float ratio = viewW / viewH;

        float h = (float) (mLblBaseZ * (Math.tan(Math.toRadians(viewAngle))));
        float w = h * ratio;

        float[] lbl = new float[18];

        // SMOKE
        lbl[0] = -mLblWidth/2;
        lbl[1] =  mLblWidth/2;
        lbl[2] =  0;

        lbl[3] = -mLblWidth/2;
        lbl[4] = -mLblWidth/2;
        lbl[5] =  0;

        lbl[6] =  mLblWidth/2;
        lbl[7] = -mLblWidth/2;
        lbl[8] =  0;


        lbl[9]  = -mLblWidth/2;
        lbl[10] =  mLblWidth/2;
        lbl[11] =  0;

        lbl[12] =  mLblWidth/2;
        lbl[13] = -mLblWidth/2;
        lbl[14] =  0;

        lbl[15] =  mLblWidth/2;
        lbl[16] =  mLblWidth/2;
        lbl[17] =  0;

        mSmoke.setVertices(lbl);
        mSmoke.setPosition(-w + mLblWidth, 0, -mLblBaseZ);
        mSmoke.setModelColor(gbl.WHITE_FULL);

        mFire.setVertices(lbl);
        mFire.setPosition( w - mLblWidth, 0, -mLblBaseZ);
        mFire.setModelColor(gbl.WHITE_FULL);

        mHigher.setVertices(lbl);
        mHigher.setPosition( 0, h - (0.75f * mLblWidth), -mLblBaseZ);
        mHigher.setModelColor(gbl.WHITE_FULL);

        mLower.setVertices(lbl);
        mLower.setPosition( 0, -h + (0.75f * mLblWidth), -mLblBaseZ);
        mLower.setModelColor(gbl.WHITE_FULL);
    }

    private void loadTexture(GL10 gl) {
        mLblBitmap = loadBitmap(R.drawable.labels);

        gl.glGenTextures(1, mTexture, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mLblBitmap, 0);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);

        mLblBitmap.recycle();
    }

    public Bitmap loadBitmap(int id) {
        InputStream is = mCtx.getResources().openRawResource(id);

        try {
            return BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
                is = null;
            } catch (IOException e) {
                is = null;
            }
        }
    }

    private float[] genTexCoords(int index) {
        float[] texCoords = new float[12];
        texCoords[0] = (float) (index/2) * 0.5f;
        texCoords[1] = (float) (index%2) * 0.5f;

        texCoords[2] = texCoords[0];
        texCoords[3] = texCoords[1] + 0.5f;

        texCoords[4] = texCoords[0] + 0.5f;
        texCoords[5] = texCoords[3];

        texCoords[6] = texCoords[0];
        texCoords[7] = texCoords[1];

        texCoords[8] = texCoords[4];
        texCoords[9] = texCoords[5];

        texCoords[10] = texCoords[0] + 0.5f;
        texCoords[11] = texCoords[1];

        return texCoords;
    }

    private FloatBuffer makeFloatBuffer(float[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * BYTES_PER_VERTEX);
        bb.order(ByteOrder.nativeOrder());

        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(array);
        fb.position(0);
        return fb;
    }
}