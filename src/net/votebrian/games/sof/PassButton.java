package net.votebrian.games.sof;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;

public class PassButton{
    private Context mCtx;

    private int mCounter = 0;
    private final int mMaxCount = 5;

    private float[] mSettle = {0.0f, 0.0f, 0.0f, 1.0f};
    private float[] mHighlight = {0.2f, 0.7f, 0.9f, 1.0f};
    private float[] mOff = {0.0f, 0.0f, 0.0f, 0.0f};
    private float[] mBlue = {0.2f, 0.7f, 0.9f, 0.5f};

    private final int mNumSegments = 5;
    private Model[] mSeg = new Model[mNumSegments];

    private float mSegBaseZ = 5.1f;

    public PassButton(Context context, GL10 gl) {
        mCtx = context;

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
            mSeg[c].setModelColor(mHighlight);
        }
    }

    public void reset() {
        for(int c = 0; c < mNumSegments; c++) {
            mSeg[c].setModelColor(mSettle);
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

            mSeg[c].setModelColor(mSettle);
            mSeg[c].setOutlineColor(mBlue);
        }
    }
}