package net.votebrian.games.sof;

import javax.microedition.khronos.opengles.GL10;

import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class Model {
    private static int VERTEX_PER_TRIANGLE = 3;
    private static int BYTES_PER_VERTEX = 4;

    private Context mCtx;

    private int mCentX;
    private int mCentY;
    private int mCentZ;

    private float mXAngle = 0;
    private float mYAngle = 0;

    // Buffers
    private ByteBuffer mVbb;
    private ByteBuffer mNbb;
    private ByteBuffer mTbb;
    private ByteBuffer mIndexBuffer;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mNormalBuffer;
    private FloatBuffer mTextureBuffer;

    private int[] mTextures = new int[2];
    private Bitmap mBitmap;
    private Bitmap mBitmap2;

    public Model(int centX, int centY, int centZ, Context context) {
        mCentX = centX;
        mCentY = centY;
        mCentZ = centZ;

        mCtx = context;
    }

    private void buildBuffers() {
        mVbb = ByteBuffer.allocateDirect(mNumVertices * VERTEX_PER_TRIANGLE * BYTES_PER_VERTEX);
        mVbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = mVbb.asFloatBuffer();
        mVertexBuffer.put(mVertices);
        mVertexBuffer.position(0);

        mNbb = ByteBuffer.allocateDirect(mNumNormals * VERTEX_PER_TRIANGLE * BYTES_PER_VERTEX);
        mNbb.order(ByteOrder.nativeOrder());
        mNormalBuffer = mNbb.asFloatBuffer();
        mNormalBuffer.put(mNormals);
        mNormalBuffer.position(0);

        mTbb = ByteBuffer.allocateDirect(mNumTexCoordinates * VERTEX_PER_TRIANGLE * BYTES_PER_VERTEX);
        mTbb.order(ByteOrder.nativeOrder());
        mTextureBuffer = mTbb.asFloatBuffer();
        mTextureBuffer.put(mTexCoordinates);
        mTextureBuffer.position(0);

        mIndexBuffer = ByteBuffer.allocateDirect(mNumIndices);
        mIndexBuffer.put(mIndices);
        mIndexBuffer.position(0);
    }

    public void loadTexture(GL10 gl) {
        InputStream is = mCtx.getResources().openRawResource(R.drawable.squares);
        mBitmap = null;

        try {
            mBitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
                is = null;
            } catch (IOException e) {
                is = null;
            }
        }

        is = mCtx.getResources().openRawResource(R.drawable.overlay);
        mBitmap2 = null;

        try {
            mBitmap2 = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
                is = null;
            } catch (IOException e) {
                is = null;
            }
        }

        gl.glGenTextures(2, mTextures, 0);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[0]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);


        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[1]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap2, 0);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);

        mBitmap.recycle();
        mBitmap2.recycle();

        buildBuffers();
    }

    public void draw(GL10 gl) {
        gl.glPushMatrix();

        // TEXTURE 0
        /*
        gl.glClientActiveTexture(GL10.GL_TEXTURE0);
        gl.glActiveTexture(GL10.GL_TEXTURE0);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[0]);
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
        gl.glEnable(GL10.GL_TEXTURE_2D);

        // TEXTURE 1
        gl.glClientActiveTexture(GL10.GL_TEXTURE1);
        gl.glActiveTexture(GL10.GL_TEXTURE1);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[1]);
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_DECAL);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        */


        // NORMALS
        gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalBuffer);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);


        // VERTICES
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);


        // TRANSLATION/ROTATION
        gl.glTranslatef(mCentX, mCentY, mCentZ);

        gl.glRotatef(mXAngle, 0f, 1f, 0f);
        gl.glRotatef(mYAngle, 1f, 0f, 0f);


        // DRAW
        //gl.glDrawArrays(GL10.GL_TRIANGLES, 0, mNumVertices);
        gl.glDrawElements(GL10.GL_TRIANGLES, mNumIndices, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);


        gl.glPopMatrix();
    }

    public void setXAngle(float angle) {
        mXAngle += angle;
    }

    public void setYAngle(float angle) {
        mYAngle += angle;
    }

    public void setBlendFunction(int pos) {

    }

    public void reset() {
        mXAngle = 0;
        mYAngle = 0;
    }


    /**
    VERTICES
    **/
    private float[] mVertices = {
        // FRONT
        -1.2000f,  1.7500f, 0.0050f,    // 0
        -0.5000f,  1.7500f, 0.0050f,
         0.5000f,  1.7500f, 0.0050f,
         1.2000f,  1.7500f, 0.0050f,

        -1.2500f,  1.7000f, 0.0050f,    // 4
        -1.2000f,  1.7000f, 0.0050f,
        -0.5000f,  1.7000f, 0.0050f,
         0.5000f,  1.7000f, 0.0050f,
         1.2000f,  1.7000f, 0.0050f,
         1.2500f,  1.7000f, 0.0050f,

        -1.2500f,  1.0000f, 0.0050f,    // 10
        -1.2000f,  1.0000f, 0.0050f,
        -0.5000f,  1.0000f, 0.0050f,
         0.5000f,  1.0000f, 0.0050f,
         1.2000f,  1.0000f, 0.0050f,
         1.2500f,  1.0000f, 0.0050f,

        -1.2500f, -1.0000f, 0.0050f,    // 16
        -1.2000f, -1.0000f, 0.0050f,
        -0.5000f, -1.0000f, 0.0050f,
         0.5000f, -1.0000f, 0.0050f,
         1.2000f, -1.0000f, 0.0050f,
         1.2500f, -1.0000f, 0.0050f,

        -1.2500f, -1.7000f, 0.0050f,    // 22
        -1.2000f, -1.7000f, 0.0050f,
        -0.5000f, -1.7000f, 0.0050f,
         0.5000f, -1.7000f, 0.0050f,
         1.2000f, -1.7000f, 0.0050f,
         1.2500f, -1.7000f, 0.0050f,

        -1.2000f, -1.7500f, 0.0050f,    // 28
        -0.5000f, -1.7500f, 0.0050f,
         0.5000f, -1.7500f, 0.0050f,
         1.2000f, -1.7500f, 0.0050f,

        // Rounded corners
        // TOP LEFT
        -1.2492f,  1.7086f, 0.0050f,    // 32
        -1.2469f,  1.7171f, 0.0050f,
        -1.2433f,  1.7250f, 0.0050f,
        -1.2383f,  1.7321f, 0.0050f,
        -1.2321f,  1.7383f, 0.0050f,
        -1.2250f,  1.7433f, 0.0050f,
        -1.2171f,  1.7469f, 0.0050f,
        -1.2086f,  1.7492f, 0.0050f,

        // TOP RIGHT
         1.2086f,  1.7492f, 0.0050f,    // 40
         1.2171f,  1.7469f, 0.0050f,
         1.2250f,  1.7433f, 0.0050f,
         1.2321f,  1.7383f, 0.0050f,
         1.2383f,  1.7321f, 0.0050f,
         1.2433f,  1.7250f, 0.0050f,
         1.2469f,  1.7171f, 0.0050f,
         1.2492f,  1.7086f, 0.0050f,

        // BOTTOM RIGHT
         1.2492f, -1.7086f, 0.0050f,    // 48
         1.2469f, -1.7171f, 0.0050f,
         1.2433f, -1.7250f, 0.0050f,
         1.2383f, -1.7321f, 0.0050f,
         1.2321f, -1.7383f, 0.0050f,
         1.2250f, -1.7433f, 0.0050f,
         1.2171f, -1.7469f, 0.0050f,
         1.2086f, -1.7492f, 0.0050f,

        // BOTTOM LEFT
        -1.2086f, -1.7492f, 0.0050f,    // 56
        -1.2171f, -1.7469f, 0.0050f,
        -1.2250f, -1.7433f, 0.0050f,
        -1.2321f, -1.7383f, 0.0050f,
        -1.2383f, -1.7321f, 0.0050f,
        -1.2433f, -1.7250f, 0.0050f,
        -1.2469f, -1.7171f, 0.0050f,
        -1.2492f, -1.7086f, 0.0050f,


        // BACK
        -1.2000f,  1.7500f, -0.0050f,    // 64
        -0.5000f,  1.7500f, -0.0050f,
         0.5000f,  1.7500f, -0.0050f,
         1.2000f,  1.7500f, -0.0050f,

        -1.2500f,  1.7000f, -0.0050f,    // 68
        -1.2000f,  1.7000f, -0.0050f,
        -0.5000f,  1.7000f, -0.0050f,
         0.5000f,  1.7000f, -0.0050f,
         1.2000f,  1.7000f, -0.0050f,
         1.2500f,  1.7000f, -0.0050f,

        -1.2500f,  1.0000f, -0.0050f,    // 74
        -1.2000f,  1.0000f, -0.0050f,
        -0.5000f,  1.0000f, -0.0050f,
         0.5000f,  1.0000f, -0.0050f,
         1.2000f,  1.0000f, -0.0050f,
         1.2500f,  1.0000f, -0.0050f,

        -1.2500f, -1.0000f, -0.0050f,    // 80
        -1.2000f, -1.0000f, -0.0050f,
        -0.5000f, -1.0000f, -0.0050f,
         0.5000f, -1.0000f, -0.0050f,
         1.2000f, -1.0000f, -0.0050f,
         1.2500f, -1.0000f, -0.0050f,

        -1.2500f, -1.7000f, -0.0050f,    // 86
        -1.2000f, -1.7000f, -0.0050f,
        -0.5000f, -1.7000f, -0.0050f,
         0.5000f, -1.7000f, -0.0050f,
         1.2000f, -1.7000f, -0.0050f,
         1.2500f, -1.7000f, -0.0050f,

        -1.2000f, -1.7500f, -0.0050f,    // 92
        -0.5000f, -1.7500f, -0.0050f,
         0.5000f, -1.7500f, -0.0050f,
         1.2000f, -1.7500f, -0.0050f,

        // Rounded corners
        // TOP LEFT
        -1.2492f,  1.7086f, -0.0050f,    // 96
        -1.2469f,  1.7171f, -0.0050f,
        -1.2433f,  1.7250f, -0.0050f,
        -1.2383f,  1.7321f, -0.0050f,
        -1.2321f,  1.7383f, -0.0050f,
        -1.2250f,  1.7433f, -0.0050f,
        -1.2171f,  1.7469f, -0.0050f,
        -1.2086f,  1.7492f, -0.0050f,

        // TOP RIGHT
         1.2086f,  1.7492f, -0.0050f,    // 104
         1.2171f,  1.7469f, -0.0050f,
         1.2250f,  1.7433f, -0.0050f,
         1.2321f,  1.7383f, -0.0050f,
         1.2383f,  1.7321f, -0.0050f,
         1.2433f,  1.7250f, -0.0050f,
         1.2469f,  1.7171f, -0.0050f,
         1.2492f,  1.7086f, -0.0050f,

        // BOTTOM RIGHT
         1.2492f, -1.7086f, -0.0050f,    // 112
         1.2469f, -1.7171f, -0.0050f,
         1.2433f, -1.7250f, -0.0050f,
         1.2383f, -1.7321f, -0.0050f,
         1.2321f, -1.7383f, -0.0050f,
         1.2250f, -1.7433f, -0.0050f,
         1.2171f, -1.7469f, -0.0050f,
         1.2086f, -1.7492f, -0.0050f,

        // BOTTOM LEFT
        -1.2086f, -1.7492f, -0.0050f,    // 120
        -1.2171f, -1.7469f, -0.0050f,
        -1.2250f, -1.7433f, -0.0050f,
        -1.2321f, -1.7383f, -0.0050f,
        -1.2383f, -1.7321f, -0.0050f,
        -1.2433f, -1.7250f, -0.0050f,
        -1.2469f, -1.7171f, -0.0050f,
        -1.2492f, -1.7086f, -0.0050f,
    };
    private int mNumVertices = mVertices.length;


    /**
    INDICES
    **/
    private byte[] mIndices = {
        // -----
        // FRONT
        // -----
        0,5,6,// 1
        0,6,1,
        1,6,7,
        1,7,2,
        2,7,8,
        2,8,3,

        4,10,11,// 7
        4,11,5,
        5,11,12,
        5,12,6,
        6,12,13,
        6,13,7,
        7,13,14,
        7,14,8,
        8,14,15,
        8,15,9,

        10,16,17,// 17
        10,17,11,
        11,17,18,
        11,18,12,
        12,18,19,
        12,19,13,
        13,19,20,
        13,20,14,
        14,20,21,
        14,21,15,

        16,22,23,// 27
        16,23,17,
        17,23,24,
        17,24,18,
        18,24,25,
        18,25,19,
        19,25,26,
        19,26,20,
        20,26,27,
        20,27,21,

        23,28,29,// 37
        23,29,24,
        24,29,30,
        24,30,25,
        25,30,31,
        25,31,26,


        4,5,32,// 43
        32,5,33,
        33,5,34,
        34,5,35,
        35,5,36,
        36,5,37,
        37,5,38,
        38,5,39,
        39,5,0,

        3,8,40,// 52
        40,8,41,
        41,8,42,
        42,8,43,
        43,8,44,
        44,8,45,
        45,8,46,
        46,8,47,
        47,8,9,

        27,26,48,// 61
        48,26,49,
        49,26,50,
        50,26,51,
        51,26,52,
        52,26,53,
        53,26,54,
        54,26,55,
        55,26,31,

        28,23,56,// 70
        56,23,57,
        57,23,58,
        58,23,59,
        59,23,60,
        60,23,61,
        61,23,62,
        62,23,63,
        63,23,22,

        // ----
        // BACK
        // ----
        70,69,64,// 79
        65,70,64,
        71,70,65,
        66,71,65,
        72,71,66,
        67,72,66,

        75,74,68,// 85
        69,75,68,
        76,75,69,
        70,76,69,
        77,76,70,
        71,77,70,
        78,77,71,
        72,78,71,
        79,78,72,
        73,79,72,

        81,80,74,// 95
        75,81,74,
        82,81,75,
        76,82,75,
        83,82,76,
        77,83,76,
        84,83,77,
        78,84,77,
        85,84,78,
        79,85,78,

        87,86,80,// 105
        81,87,80,
        88,87,81,
        82,88,81,
        89,88,82,
        83,89,82,
        90,89,83,
        84,90,83,
        91,90,84,
        85,91,84,

        93,92,87,// 115
        88,93,87,
        94,93,88,
        89,94,88,
        95,94,89,
        90,95,89,


        96,69,68,// 121
        97,69,96,
        98,69,97,
        99,69,98,
        100,69,99,
        101,69,100,
        102,69,101,
        103,69,102,
        64,69,103,

        104,72,67,// 130
        105,72,104,
        106,72,105,
        107,72,106,
        108,72,107,
        109,72,108,
        110,72,109,
        111,72,110,
        73,72,111,

        112,90,91,// 139
        113,90,112,
        114,90,113,
        115,90,114,
        116,90,115,
        117,90,116,
        118,90,117,
        119,90,118,
        95,90,119,

        120,87,92,// 148
        121,87,120,
        122,87,121,
        123,87,122,
        124,87,123,
        125,87,124,
        126,87,125,
        127,87,126,
        86,87,127,

        // -----
        // SIDES
        // -----
        // TOP
        64,0,1,
        64,1,65,
        65,1,2,
        65,2,66,
        66,2,3,
        66,3,67,

        // RIGHT
        73,9,15,
        73,15,79,
        79,15,21,
        79,21,85,
        85,21,27,
        85,27,91,

        // BOTTOM
        95,31,30,
        95,30,94,
        94,30,29,
        94,29,93,
        93,29,28,
        93,28,92,

        // LEFT
        86,22,16,
        86,16,80,
        80,16,10,
        80,10,74,
        74,10,4,
        74,4,68
    };
    private int mNumIndices = mIndices.length;


    /**
    NORMALS
    **/
    private float[] mNormals = {
         0,  0,  1,     // 0
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,

         0,  0,  1,     // 4
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,

         0,  0,  1,     // 10
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,

         0,  0,  1,     // 16
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,

         0,  0,  1,     // 22
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,

         0,  0,  1,     // 28
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,

         0,  0,  1,     // 32
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,

         0,  0,  1,     // 40
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,

         0,  0,  1,     // 48
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,

         0,  0,  1,     // 56
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,
         0,  0,  1,



         0,  0, -1,     // 0
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,

         0,  0, -1,     // 4
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,

         0,  0, -1,     // 10
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,

         0,  0, -1,     // 16
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,

         0,  0, -1,     // 22
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,

         0,  0, -1,     // 28
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,

         0,  0, -1,     // 32
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,

         0,  0, -1,     // 40
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,

         0,  0, -1,     // 48
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,

         0,  0, -1,     // 56
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
         0,  0, -1,
    };
    private int mNumNormals = mNormals.length;


    /**
    TEXTURE COORDINATES
    **/
    private float[] mTexCoordinates = {
        0.25f, 0.00f,      // Front
        0.25f, 0.25f,
        0.50f, 0.25f,

        0.25f, 0.00f,
        0.50f, 0.25f,
        0.50f, 0.00f,

        0.25f, 0.75f,      // Top
        0.25f, 1.00f,
        0.50f, 1.00f,

        0.25f, 0.75f,
        0.50f, 1.00f,
        0.50f, 0.75f,

        0.75f, 0.25f,      // Right
        0.50f, 0.25f,
        0.50f, 0.50f,

        0.75f, 0.25f,
        0.50f, 0.50f,
        0.75f, 0.50f,

        0.25f, 0.25f,      // Bottom
        0.25f, 0.50f,
        0.50f, 0.50f,

        0.25f, 0.25f,
        0.50f, 0.50f,
        0.50f, 0.25f,

        0.00f, 0.50f,      // Left
        0.25f, 0.50f,
        0.25f, 0.25f,

        0.00f, 0.50f,
        0.25f, 0.25f,
        0.00f, 0.25f,

        0.50f, 0.75f,      // Back
        0.50f, 0.50f,
        0.25f, 0.50f,

        0.50f, 0.75f,
        0.25f, 0.50f,
        0.25f, 0.75f
    };
    private int mNumTexCoordinates = mTexCoordinates.length;
}