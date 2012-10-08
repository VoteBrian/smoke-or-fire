package net.votebrian.games.sof;

import javax.microedition.khronos.opengles.GL10;

import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import android.util.Log;

public class Card {

    private Context mCtx;
    private Global gbl;

    private Random mRandom;

    public int mValue = 0;
    public int mSuit = 0;

    private float mCentX;
    private float mCentY;
    private float mCentZ;

    private float mZBase = 0;

    private float mXAngle = 0;
    private float mYAngle = 0;

    private float mAngleSkew = 0f;
    private float mXSkew = 0f;
    private float mYSkew = 0f;

    private int[] mTextures = new int[2];
    private float[] mVertices;

    private int mState = 0;
    // Describes where the card is placed
    // state = 0;  Card is in deck, face down
    // state = 1;  Card is on table, face up
    // state = 3;  Card is not drawn

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

        mRandom = new Random();

        mSuit = suit;
        mValue = value;

        mAngleSkew = 15 * (mRandom.nextFloat() - 0.5f);
        mXSkew = 0.5f * (mRandom.nextFloat() - 0.5f);
        mYSkew = 0.3f * (mRandom.nextFloat() - 0.5f);
    }

    public void setPosition(float x, float y, float z) {
        mCentX = x;
        mCentY = y;
        mCentZ = z;

        mZBase = z;
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
        if(mState != 3) {       // IF NOT BURNT
            gl.glPushMatrix();

            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

            // TEXTURE 0
            gl.glClientActiveTexture(GL10.GL_TEXTURE0);
            gl.glActiveTexture(GL10.GL_TEXTURE0);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[mSuit]);
            gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
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

            switch(mState) {
                case 0:  // In Deck
                    gl.glTranslatef(-4f, mCentY, mCentZ);
                    gl.glRotatef(180, 0f, 1f, 0f);
                    break;
                case 1:  // On Table
                    gl.glTranslatef(0f + mXSkew, mCentY + mYSkew, mCentZ);
                    gl.glRotatef(0, 0f, 1f, 0f);
                    gl.glRotatef(mAngleSkew, 0f, 0f, 1f);
            }
            // TRANSLATION/ROTATION
            // gl.glTranslatef(mCentX, mCentY, mCentZ);

            // gl.glRotatef(mXAngle, 0f, 1f, 0f);
            // gl.glRotatef(mYAngle, 1f, 0f, 0f);


            // DRAW
            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, mNumVertices);

            gl.glPopMatrix();
        }
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

    public void deal(int place) {
        mState = 1;
        mCentZ = (float) (mZBase + (0.016 * place) );
    }

    // Card goes into deck
    // place determines how high in the stack the card sits.
    // place = 0 would be the card on the bottom of stack
    public void load(int place) {
        mState = 0;
        mCentZ = (float) (mZBase + (0.016 * place) );
    }

    public void burn() {
        mState = 3;
    }
}