package net.votebrian.games.sof;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

public class Model{

    private float mCentX = 0f;
    private float mCentY = 0f;
    private float mCentZ = 0f;

    private float mRotX = 0f;
    private float mRotY = 0f;
    private float mRotZ = 0f;

    private float mAlpha = 0;

    private boolean mTexSet = false;

    private float[] mVertices = {
         0f, 1f, -5f,
        -1f, 0f, -5f,
         1f, 0f, -5f
    };
    private int mLengthVertices = mVertices.length;
    private FloatBuffer mVertexBuffer;

    private float[] mColor = {
        0.0f, 1.0f, 0.0f, 1.0f
    };
    private FloatBuffer mColorBuffer;

    // CONSTRUCTOR
    public Model() {
        buildBuffers();
    }

    public void draw(GL10 gl) {
        gl.glPushMatrix();

        // Texture
        if(mTexSet) {
            /*
            //GL_TEXTURE0, mTextures, and mTexBuffer need to be defined
            gl.glClientActiveTexture(GL10.GL_TEXTURE0);
            gl.glActiveTexture(GL10.GL_TEXTURE0);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures);
            gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureSuitBuffer);
            gl.glEnable(GL10.GL_TEXTURE_2D);
            */
        } else {
            // stuff
        }

        // Color
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);

        // Vertices
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);

        // Draw
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, mLengthVertices);

        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glPopMatrix();
    }

    public void bindTexture(GL10 gl) {
        // TODO: stuff
    }

    private void buildBuffers() {
        mVertexBuffer = makeFloatBuffer(mVertices);

        mColorBuffer = makeFloatBuffer(mColor);
    }

    private FloatBuffer makeFloatBuffer(float[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
        bb.order(ByteOrder.nativeOrder());

        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(array);
        fb.position(0);
        return fb;
    }

    public void setVertices(float[] array) {
        mLengthVertices = array.length;
        mVertices = new float[mLengthVertices];

        mVertices = array;

        buildBuffers();
    }

    // POSITION
    public void setPosition(float x, float y, float z) {
        mCentX = x;
        mCentY = y;
        mCentZ = z;
    }

    public void setPositionX(float x) {
        mCentX = x;
    }

    public void setPositionY(float y) {
        mCentY = y;
    }

    public void setPositionZ(float z) {
        mCentZ = z;
    }

    // ROTATION
    public void setRotation(float x, float y, float z) {
        mRotX = x;
        mRotY = y;
        mRotZ = z;
    }

    public void setRotationX(float x) {
        mRotX = x;
    }

    public void setRotationY(float y) {
        mRotY = y;
    }

    public void setRotationZ(float z) {
        mRotZ = z;
    }

    public void setColor(float r, float g, float b, float a) {
        int numVertices = mLengthVertices/3;

        mColor = new float[numVertices*4];

        for(int c = 0; c < numVertices; c++) {
            mColor[c*4] = r;
            mColor[c*4 + 1] = g;
            mColor[c*4 + 2] = b;
            mColor[c*4 + 3] = a;
        }

        buildBuffers();
    }

    public void setAlpha(float a) {
        mAlpha = a;
    }
}