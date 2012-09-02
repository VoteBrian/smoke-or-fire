package net.votebrian.games.sof;

import javax.microedition.khronos.opengles.GL10;

import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Array;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import android.util.Log;

public class Card {

    private Context mCtx;
    private Global gbl;

    private int mValue = 0;
    private int mSuit = 0;

    private float mCentX;
    private float mCentY;
    private float mCentZ;

    private float mXAngle = 0;
    private float mYAngle = 0;

    private int[] mTextures = new int[2];
    private float[] mVertices;

    // Buffers
    private ByteBuffer mVbb;
    private ByteBuffer mNbb;
    private ByteBuffer mTbb;
    private ByteBuffer mTVbb;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mNormalBuffer;
    private FloatBuffer mTextureSuitBuffer;
    private FloatBuffer mTextureValueBuffer;

    private int mNumVertices;

    public Card(Context context, int suit, int value) {
        mCtx = context;
        gbl = (Global) context.getApplicationContext();

        mSuit = suit;
        mValue = value;
    }

    public void setPosition(float x, float y, float z) {
        mCentX = x;
        mCentY = y;
        mCentZ = z;
    }

    public void setVertices(float[] vertices) {
        mVertices = vertices;
        mNumVertices = mVertices.length;
    }

    public void setTextures(int textures[]) {
        mTextures = textures;
    }

    public void setVertexBuffer(FloatBuffer buffer) {
        mVertexBuffer = buffer;
    }

    public void setTextureBuffers(FloatBuffer suit, FloatBuffer value) {
        mTextureSuitBuffer = suit;
        mTextureValueBuffer = value;
    }

    public void draw(GL10 gl) {
        gl.glPushMatrix();

        // TEXTURE 0
        gl.glClientActiveTexture(GL10.GL_TEXTURE0);
        gl.glActiveTexture(GL10.GL_TEXTURE0);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[mSuit]);
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureSuitBuffer);
        gl.glEnable(GL10.GL_TEXTURE_2D);

        // TEXTURE 1
        gl.glClientActiveTexture(GL10.GL_TEXTURE1);
        gl.glActiveTexture(GL10.GL_TEXTURE1);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[4]);
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_DECAL);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureValueBuffer);
        gl.glEnable(GL10.GL_TEXTURE_2D);


        // NORMALS
        // gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalBuffer);
        // gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);


        // VERTICES
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);


        // TRANSLATION/ROTATION
        gl.glTranslatef(mCentX, mCentY, mCentZ);

        // gl.glRotatef(mXAngle, 0f, 1f, 0f);
        // gl.glRotatef(mYAngle, 1f, 0f, 0f);


        // DRAW
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, mNumVertices);
        // gl.glDrawElements(GL10.GL_TRIANGLES, mNumIndices, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);

        gl.glPopMatrix();
    }

    public void setXAngle(float angle) {
        mCentX += angle;
    }

    public void setYAngle(float angle) {
        mCentY += angle;
    }

    public void reset() {
        mXAngle = 0;
        mYAngle = 0;
    }
}