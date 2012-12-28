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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import java.io.InputStream;
import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import android.util.Log;

public class Model {
    // -------
    // GLOBALS
    // -------
    private Context mCtx;

    // Enables
    private boolean mEnableModel = false;
    private boolean mEnableTexture = false;
    private boolean mEnableColor = false;
    private boolean mEnableOutline = false;

    // Model
    private float[] mVertices = {
         0.0f,  0.5f, 0f,
        -0.5f, -0.5f, 0f,
         0.5f, -0.5f, 0f
    };
    private int mNumVertices = mVertices.length/3;
    private FloatBuffer mVertexBuffer;

    // Model Color
    private float[] mColor = {
        1.0f, 1.0f, 1.0f, 1.0f
    };
    private FloatBuffer mColorBuffer;

    // Textures
    private int mTexSet = 0;
    private int[] mTexture = new int[2];

    private Bitmap mBitmap1;
    private Bitmap mBitmap2;

    private float[] mTexCoord1 = {
        0.5f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f
    };
    private FloatBuffer mTexCoordBuffer1;

    private float[] mTexCoord2 = {
        0.5f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f
    };
    private FloatBuffer mTexCoordBuffer2;

    // Outline
    private float[] mOutline = {
        0.0f, 0.0f, 0f,
        1.0f, 1.0f, 0f
    };
    private int mNumOutlineVertices = mOutline.length/3;
    private FloatBuffer mOutlineBuffer;

    // Outline Color
    private float[] mOutlineColor = {
        1.0f, 0.0f, 0.0f, 1.0f
    };
    private FloatBuffer mOutlineColorBuffer;

    // Translation
    private float mCentX = 0f;
    private float mCentY = 0f;
    private float mCentZ = 0f;

    // Rotation
    private float mRotX = 0f;
    private float mRotY = 0f;
    private float mRotZ = 0f;



    // -----------
    // CONSTRUCTOR
    // -----------
    public Model(Context context, GL10 gl) {
        mCtx = context;
        enableModel();
    }

    public Model(Context context, GL10 gl, Boolean loadDefaults) {
        mCtx = context;

        enableModel();

        // default model and textures
        if(loadDefaults) {
            initBuffers();
            initTextures(gl);
        }
    }

    public void draw(GL10 gl) {
        gl.glPushMatrix();


        // Translations
        gl.glTranslatef(mCentX, mCentY, mCentZ);

        // Rotations
        gl.glRotatef(mRotX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(mRotY, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(mRotZ, 0.0f, 0.0f, 1.0f);

        /* ------------
            DRAW MODEL
           ------------ */
        if(mEnableModel) {
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

            // Select between a solid color, one texture, or two textures
            switch(mTexSet) {
                case 0:
                    /* -----------------------------------
                        Draw model with solid color.
                        Disabled when setTexture() is called.
                        Run setModelColor( new float[]{r,g,b,a} ) to set color.
                       ----------------------------------- */

                    gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
                    gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
                    break;
                case 1:
                    /* -----------------------------------
                        Draw model with a single texture
                        Enabled when calling setTexture( int mTexture )
                            First run:
                            gl.glGenTextures(1, mTexture, 0);
                            gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture);
                            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mTexBitmap, 0);
                        Run setTextureBuffer()
                       ----------------------------------- */

                    // Texture 1
                    gl.glClientActiveTexture(GL10.GL_TEXTURE0);
                    gl.glActiveTexture(GL10.GL_TEXTURE0);
                    gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
                    gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);
                    gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
                    gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexCoordBuffer1);
                    gl.glEnable(GL10.GL_TEXTURE_2D);
                    break;
                case 2:
                    /* -----------------------------------
                        Draw model with a two textures
                        Enabled when calling setTexture( int, int )
                            First run:
                            gl.glGenTextures(1, mTexture, 0);
                            gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture);
                            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mTexBitmap, 0);
                        Run setTextureBuffer()
                       ----------------------------------- */

                    // Texture 1
                    gl.glClientActiveTexture(GL10.GL_TEXTURE0);
                    gl.glActiveTexture(GL10.GL_TEXTURE0);
                    gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
                    gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);
                    gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
                    gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexCoordBuffer1);
                    gl.glEnable(GL10.GL_TEXTURE_2D);

                    // Texture 2
                    gl.glClientActiveTexture(GL10.GL_TEXTURE1);
                    gl.glActiveTexture(GL10.GL_TEXTURE1);
                    gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
                    gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[1]);
                    gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_DECAL);
                    gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexCoordBuffer2);
                    gl.glEnable(GL10.GL_TEXTURE_2D);

                    // Revert to avoid conflicts with other draws
                    gl.glActiveTexture(GL10.GL_TEXTURE0);
                    break;
            }

            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, mNumVertices);


            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glDisable(GL10.GL_TEXTURE_2D);
        }


        /* ---------------
            DRAW OUTLINES

            Requires the following subroutines be called:
                enableOutlines()
                setOutlineIndicies()
           --------------- */
        if(mEnableOutline) {
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

            gl.glColorPointer(4, GL10.GL_FLOAT, 0, mOutlineColorBuffer);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mOutlineBuffer);
            gl.glLineWidth(2f);
            gl.glDrawArrays(GL10.GL_LINES, 0, mNumOutlineVertices);

            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        }

        gl.glPopMatrix();
    }


    /* -------
        MODEL
       ------- */
    public void disableModel() {
        mEnableModel = false;
    }

    public void enableModel() {
        mEnableModel = true;
    }

    public void setModelColor(float[] array) {
        mColor = new float[array.length];
        mColor = array;

        mColorBuffer = buildColorBuffer(mColor, mNumVertices);
    }

    public void setVertices(float[] vertices) {
        mVertices = new float[vertices.length];
        mVertices = vertices;
        mNumVertices = mVertices.length/3;

        mVertexBuffer = makeFloatBuffer(mVertices);
    }

    // Bypass setting the vertex array.
    // Set only what is necessary for draw.
    // Passing vertex array required for drawing an outline, however.
    public void setVertexBuffer(FloatBuffer buffer, int num) {
        mVertexBuffer = buffer;
        mNumVertices = num;
    }


    /* ----------
        TEXTURES
       ---------- */

    // Enable textures without assigning.  Used mainly for testing using default textures.
    public void setTexture(boolean tex1, boolean tex2) {
        if(tex1 && tex2) {
            mTexSet = 2;
        } else if (tex1) {
            mTexSet = 1;
        }
    }

    public void setTexture(int texture) {
        mTexture[0] = texture;

        // only one texture will be drawn
        mTexSet = 1;
    }

    // texture 1 is set using GL_MODULATE
    // texture 2 is set using GL_DECAL
    public void setTexture(int texture1, int texture2) {
        mTexture[0] = texture1;
        mTexture[1] = texture2;

        // enable multi-texturing
        mTexSet = 2;
    }

    public void setTextureBuffer(FloatBuffer buffer) {
        mTexCoordBuffer1 = buffer;
    }

    public void setTextureBuffer(FloatBuffer buffer1, FloatBuffer buffer2) {
        mTexCoordBuffer1 = buffer1;
        mTexCoordBuffer2 = buffer2;
    }

    // Initializes mTexture[0:1] and Texture Coordinate Buffers using images
    // deafault1 and default2.  Routine setTexture() bypasses loading the bitmap
    // for cases when a texture is used on many models.
    public void initTextures(GL10 gl) {
        // Generate textures
        gl.glGenTextures(2, mTexture, 0);

        // Texture 1
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);
        mBitmap1 = loadBitmap(R.drawable.default1);
        if(mBitmap1 != null) {
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap1, 0);
        }

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);


        // Texture 2
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[1]);
        mBitmap2 = loadBitmap(R.drawable.default2);
        if(mBitmap1 != null) {
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap2, 0);
        }

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);


        // Buffers
        mTexCoordBuffer1 = makeFloatBuffer(mTexCoord1);
        mTexCoordBuffer2 = makeFloatBuffer(mTexCoord2);
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


    /* ----------
        OUTLINES
       ---------- */
    public void disableOutline() {
        mEnableOutline = false;
    }

    public void enableOutline() {
        mEnableOutline = true;
    }

    public void setOutlineIndices(int[] array) {
        int numIndices = array.length;

        mOutline = new float[numIndices * 3];

        for(int c = 0; c < numIndices; c++) {
            mOutline[c*3] = mVertices[array[c]*3];
            mOutline[c*3 + 1] = mVertices[array[c]*3 + 1];
            mOutline[c*3 + 2] = mVertices[array[c]*3 + 2];
        }

        mNumOutlineVertices = mOutline.length/3;

        mOutlineBuffer = makeFloatBuffer(mOutline);
        mOutlineColorBuffer = buildColorBuffer(mOutlineColor, mNumOutlineVertices);
    }

    public void setOutlineColor(float[] array) {
        mOutlineColor = array;

        mOutlineColorBuffer = buildColorBuffer(mOutlineColor, mNumOutlineVertices);
    }


    /* ---------
        GENERAL
       --------- */
    public FloatBuffer buildColorBuffer(float[] array, int numVertices) {
        float[] tempArray = new float[numVertices*4];

        for(int c = 0; c < numVertices; c++) {
            tempArray[c*4 + 0] = array[0];
            tempArray[c*4 + 1] = array[1];
            tempArray[c*4 + 2] = array[2];
            tempArray[c*4 + 3] = array[3];
        }

        return makeFloatBuffer(tempArray);
    }

    private void initBuffers() {
        mVertexBuffer = makeFloatBuffer(mVertices);
        mColorBuffer = makeFloatBuffer(mColor);
        mOutlineColorBuffer = makeFloatBuffer(mOutlineColor);
    }

    private FloatBuffer makeFloatBuffer(float[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
        bb.order(ByteOrder.nativeOrder());

        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(array);
        fb.position(0);
        return fb;
    }

    public void setPosition(float x, float y, float z) {
        mCentX = x;
        mCentY = y;
        mCentZ = z;
    }

    public void setRotation(float x, float y, float z) {
        mRotX = x;
        mRotY = y;
        mRotZ = z;
    }
}