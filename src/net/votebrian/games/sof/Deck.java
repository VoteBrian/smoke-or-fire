package net.votebrian.games.sof;

import javax.microedition.khronos.opengles.GL10;

import java.io.InputStream;
import java.io.IOException;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

public class Deck {
    private Context mCtx;
    private Global gbl;

    private Random random;

    private int mNumCards = 52;
    private Card[] mCards = new Card[mNumCards];

    private int[] mInDeck = new int[52];
    private int mNumInDeck = 0;
    private int[] mOnTable = new int[52];
    private int mNumOnTable = 0;
    private int[] mBurnt = new int[52];
    private int mNumBurnt = 0;

    private int mResult = Global.GOOD;

    private int mCurrent = 0;

    private int mZBase = -10;

    private Bitmap mHeartBitmap;
    private Bitmap mDiamondBitmap;
    private Bitmap mClubBitmap;
    private Bitmap mSpadeBitmap;
    private Bitmap[] mSuitBitmap;

    private Bitmap mValueBitmap;

    private ByteBuffer mVbb;    // Vertices
    private ByteBuffer mNbb;    // Normals
    private ByteBuffer mTbb1;   // Suit Texture
    private ByteBuffer mTV2bb;   // Value Texture
    private ByteBuffer mTV3bb;
    private ByteBuffer mTV4bb;
    private ByteBuffer mTV5bb;
    private ByteBuffer mTV6bb;
    private ByteBuffer mTV7bb;
    private ByteBuffer mTV8bb;
    private ByteBuffer mTV9bb;
    private ByteBuffer mTV10bb;
    private ByteBuffer mTVJbb;
    private ByteBuffer mTVQbb;
    private ByteBuffer mTVKbb;
    private ByteBuffer mTVAbb;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mNormalBuffer;
    private FloatBuffer mSuitBuffer;
    private FloatBuffer mTextureValue2Buffer;
    private FloatBuffer mTextureValue3Buffer;
    private FloatBuffer mTextureValue4Buffer;
    private FloatBuffer mTextureValue5Buffer;
    private FloatBuffer mTextureValue6Buffer;
    private FloatBuffer mTextureValue7Buffer;
    private FloatBuffer mTextureValue8Buffer;
    private FloatBuffer mTextureValue9Buffer;
    private FloatBuffer mTextureValue10Buffer;
    private FloatBuffer mTextureValueJBuffer;
    private FloatBuffer mTextureValueQBuffer;
    private FloatBuffer mTextureValueKBuffer;
    private FloatBuffer mTextureValueABuffer;

    private static int VERTEX_PER_TRIANGLE = 3;
    private static int BYTES_PER_VERTEX = 4;

    public int[] mTextures = new int[5];



    // Constructor
    public Deck(Context context, GL10 gl) {
        int counter = 0;
        float x = 0f;
        float y = 0f;
        float z = 0f;

        int suit = 0;
        int value = 0;

        mCtx = context;

        gbl = (Global) mCtx.getApplicationContext();

        random = new Random();

        loadTexture(gl);

        // generate the 52 cards
        for (counter = 0; counter < mNumCards; counter++) {
            x = 0f;
            y = 0f;
            z = mZBase;

            suit = counter % 4;
            value = counter/4 + 2;

            mCards[counter] = new Card(mCtx, suit, value);
            mCards[counter].setPosition(x, y, z);
            mCards[counter].setVertices(mVertices);
            mCards[counter].setTextures(mTextures);
            mCards[counter].setVertexBuffer(mVertexBuffer);
            switch(value) {
                case 2:
                    mCards[counter].setTextureBuffers(mSuitBuffer, mTextureValue2Buffer);
                    break;
                case 3:
                    mCards[counter].setTextureBuffers(mSuitBuffer, mTextureValue3Buffer);
                    break;
                case 4:
                    mCards[counter].setTextureBuffers(mSuitBuffer, mTextureValue4Buffer);
                    break;
                case 5:
                    mCards[counter].setTextureBuffers(mSuitBuffer, mTextureValue5Buffer);
                    break;
                case 6:
                    mCards[counter].setTextureBuffers(mSuitBuffer, mTextureValue6Buffer);
                    break;
                case 7:
                    mCards[counter].setTextureBuffers(mSuitBuffer, mTextureValue7Buffer);
                    break;
                case 8:
                    mCards[counter].setTextureBuffers(mSuitBuffer, mTextureValue8Buffer);
                    break;
                case 9:
                    mCards[counter].setTextureBuffers(mSuitBuffer, mTextureValue9Buffer);
                    break;
                case 10:
                    mCards[counter].setTextureBuffers(mSuitBuffer, mTextureValue10Buffer);
                    break;
                case 11:
                    mCards[counter].setTextureBuffers(mSuitBuffer, mTextureValueJBuffer);
                    break;
                case 12:
                    mCards[counter].setTextureBuffers(mSuitBuffer, mTextureValueQBuffer);
                    break;
                case 13:
                    mCards[counter].setTextureBuffers(mSuitBuffer, mTextureValueKBuffer);
                    break;
                case 14:
                    mCards[counter].setTextureBuffers(mSuitBuffer, mTextureValueABuffer);
                    break;
            }
        }

        // initialize deck
        for(int c = 0; c < mNumCards; c++) {
            mBurnt[c] = c;
        }
        mNumBurnt = mNumCards;
        mNumInDeck = 0;
        mNumOnTable = 0;

        shuffle();      // shuffle burnt cards.  All cards, in this case
        reloadDeck();   // move all cards from burnt to deck.
    }

    public void draw(GL10 gl) {
        for(int counter = 0; counter < mNumCards; counter++) {
            mCards[counter].draw(gl);
        }
    }

    public void setXAngle(float angle) {
        for(int counter = 0; counter < mNumCards; counter++) {
            mCards[counter].setXAngle( (float) (counter * angle * 0.0005));
        }
    }

    public void setYAngle(float angle) {
        for(int counter = 0; counter < mNumCards; counter++) {
            mCards[counter].setYAngle( (float) (counter * angle * 0.0005));
        }
    }

    public int deal(int pick) {
        // first check to make sure we have a card in deck to deal
        if(mNumInDeck == 0) {
            return -1;
        }

        int selSuit = 0;
        int selValue = 0;

        int prevSuit = 0;
        int prevValue = 0;

        if(mNumOnTable == 0) {      // first selection
            selSuit = mCards[mInDeck[mNumInDeck-1]].mSuit;
            selValue = mCards[mInDeck[mNumInDeck-1]].mValue;

            Log.v("Deal", "Suit: " + selSuit + ", Value: " + selValue);
        } else {                    // card already on table
            prevSuit = mCards[mOnTable[mNumOnTable-1]].mSuit;
            prevValue = mCards[mOnTable[mNumOnTable-1]].mValue;

            selSuit = mCards[mInDeck[mNumInDeck-1]].mSuit;
            selValue = mCards[mInDeck[mNumInDeck-1]].mValue;

            Log.v("Deal Prev", "Suit: " + prevSuit + ", Value: " + prevValue);
            Log.v("Deal Curr", "Suit: " + selSuit + ", Value: " + selValue);
        }

        // Transfer card from top of deck to table
        // change card state and animation
        mCards[ mInDeck[mNumInDeck-1] ].deal(mNumOnTable);

        mNumOnTable++;
        mOnTable[mNumOnTable-1] = mInDeck[mNumInDeck-1];
        mInDeck[mNumInDeck-1] = -1;
        mNumInDeck--;


        // Check against selection
        switch(pick) {
            case Global.SMOKE:
                if(selSuit > 1) {
                    mResult = Global.GOOD;
                } else {
                    mResult = Global.BAD;
                }
                break;
            case Global.FIRE:
                if(selSuit < 1) {
                    mResult = Global.GOOD;
                } else {
                    mResult = Global.BAD;
                }
                break;
            case Global.HIGHER:
                if(selValue > prevValue) {
                    mResult = Global.GOOD;
                } else if(selValue < prevValue) {
                    mResult = Global.BAD;
                } else {
                    mResult = Global.SOCIAL;
                }
                break;
            case Global.LOWER:
                if(selValue < prevValue) {
                    mResult = Global.GOOD;
                } else if(selValue > prevValue) {
                    mResult = Global.BAD;
                } else {
                    mResult = Global.SOCIAL;
                }
                break;
        }
        return mResult;
    }

    public void reset() {
        burnAll();
        shuffle();
        reloadDeck();
    }

    public void burnAll() {
        for(int c = 0; c < mNumCards; c++) {
            mBurnt[c] = c;
            mCards[c].burn();

            mInDeck[c] = -1;
            mOnTable[c] = -1;
        }

        mNumBurnt = mNumCards;
        mNumInDeck = 0;
        mNumOnTable = 0;
    }

    private void shuffle() {
        for(int c = 0; c < mNumBurnt; c++) {
            int intRand = random.nextInt(mNumBurnt-1);
            int temp = mBurnt[c];
            mBurnt[c] = mBurnt[intRand];
            mBurnt[intRand] = temp;
        }
    }

    private void reloadDeck() {
        // push up the cards already in the deck
        for(int c = 0; c < mNumInDeck; c++) {
            mInDeck[c] = mInDeck[c+mNumBurnt];
        }

        for(int c = 0; c < mNumBurnt; c++) {
            mInDeck[c] = mBurnt[c];
            mBurnt[c] = -1;
        }

        mNumInDeck = mNumInDeck + mNumBurnt;
        mNumBurnt = 0;

        for(int c = 0; c < mNumInDeck; c++) {
            mCards[ mInDeck[c] ].load(c);
        }
    }

    public void burnTable() {
        if(mNumOnTable != 0) {
            Log.v("Burn Table", "mNumOnTable: " + mNumOnTable);
            for(int c = 0; c < mNumOnTable; c++) {
                Log.v("Burn", String.valueOf(mOnTable[c]));

                // make sure it hasn't already been burnt
                // for some reason
                if(mOnTable[c] != -1) {

                    mCards[mOnTable[c]].burn();
                    mBurnt[mNumBurnt] = mOnTable[c];
                    mNumBurnt++;
                    mOnTable[c] = -1;
                }
            }

            mNumOnTable = 0;
        }
    }

    // Load texture bitmaps
    public void loadTexture(GL10 gl) {
        // Load Suit Bitmaps
        InputStream is = mCtx.getResources().openRawResource(R.drawable.cardbaseheart);
        mHeartBitmap = null;

        try {
            mHeartBitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
                is = null;
            } catch (IOException e) {
                is = null;
            }
        }

        is = mCtx.getResources().openRawResource(R.drawable.cardbasediamond);
        mDiamondBitmap = null;

        try {
            mDiamondBitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
                is = null;
            } catch (IOException e) {
                is = null;
            }
        }

        is = mCtx.getResources().openRawResource(R.drawable.cardbaseclub);
        mClubBitmap = null;

        try {
            mClubBitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
                is = null;
            } catch (IOException e) {
                is = null;
            }
        }

        is = mCtx.getResources().openRawResource(R.drawable.cardbasespade);
        mSpadeBitmap = null;

        try {
            mSpadeBitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
                is = null;
            } catch (IOException e) {
                is = null;
            }
        }

        // Load Value Bitmaps
        is = mCtx.getResources().openRawResource(R.drawable.cardvalue);
        mValueBitmap = null;

        try {
            mValueBitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
                is = null;
            } catch (IOException e) {
                is = null;
            }
        }

        // Generate Texture Buffers
        gl.glGenTextures(5, mTextures, 0);

        // Suit Textures
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[0]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mHeartBitmap, 0);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[1]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mDiamondBitmap, 0);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[2]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mClubBitmap, 0);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[3]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mSpadeBitmap, 0);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);



        // Value Texture
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[4]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mValueBitmap, 0);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);

        mHeartBitmap.recycle();
        mDiamondBitmap.recycle();
        mClubBitmap.recycle();
        mSpadeBitmap.recycle();
        mValueBitmap.recycle();

        buildBuffers();
    }

    private void buildBuffers() {
        // Vertices
        mVbb = ByteBuffer.allocateDirect(mNumVertices * VERTEX_PER_TRIANGLE * BYTES_PER_VERTEX);
        mVbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = mVbb.asFloatBuffer();
        mVertexBuffer.put(mVertices);
        mVertexBuffer.position(0);

        // Normals
        /* mNbb = ByteBuffer.allocateDirect(mNumNormals * VERTEX_PER_TRIANGLE * BYTES_PER_VERTEX);
        mNbb.order(ByteOrder.nativeOrder());
        mNormalBuffer = mNbb.asFloatBuffer();
        mNormalBuffer.put(mNormals);
        mNormalBuffer.position(0);
        */

        // Textures
        // Suit Coordinates
        mTbb1 = ByteBuffer.allocateDirect(mNumTexCoordinatesA * VERTEX_PER_TRIANGLE * BYTES_PER_VERTEX);
        mTbb1.order(ByteOrder.nativeOrder());
        mSuitBuffer = mTbb1.asFloatBuffer();
        mSuitBuffer.put(mTexCoordinatesA);
        mSuitBuffer.position(0);

        // Value Coordinates
        mTV2bb = ByteBuffer.allocateDirect(mNumTexCoordinatesB * VERTEX_PER_TRIANGLE * BYTES_PER_VERTEX);
        mTV2bb.order(ByteOrder.nativeOrder());
        mTextureValue2Buffer = mTV2bb.asFloatBuffer();
        mTextureValue2Buffer.put(mTexCoordinatesB);
        mTextureValue2Buffer.position(0);

        newTexCoords(3);
        mTV3bb = ByteBuffer.allocateDirect(mNumTexCoordinatesB * VERTEX_PER_TRIANGLE * BYTES_PER_VERTEX);
        mTV3bb.order(ByteOrder.nativeOrder());
        mTextureValue3Buffer = mTV3bb.asFloatBuffer();
        mTextureValue3Buffer.put(mTexCoordinatesB);
        mTextureValue3Buffer.position(0);

        newTexCoords(4);
        mTV4bb = ByteBuffer.allocateDirect(mNumTexCoordinatesB * VERTEX_PER_TRIANGLE * BYTES_PER_VERTEX);
        mTV4bb.order(ByteOrder.nativeOrder());
        mTextureValue4Buffer = mTV4bb.asFloatBuffer();
        mTextureValue4Buffer.put(mTexCoordinatesB);
        mTextureValue4Buffer.position(0);

        newTexCoords(5);
        mTV5bb = ByteBuffer.allocateDirect(mNumTexCoordinatesB * VERTEX_PER_TRIANGLE * BYTES_PER_VERTEX);
        mTV5bb.order(ByteOrder.nativeOrder());
        mTextureValue5Buffer = mTV5bb.asFloatBuffer();
        mTextureValue5Buffer.put(mTexCoordinatesB);
        mTextureValue5Buffer.position(0);

        newTexCoords(6);
        mTV6bb = ByteBuffer.allocateDirect(mNumTexCoordinatesB * VERTEX_PER_TRIANGLE * BYTES_PER_VERTEX);
        mTV6bb.order(ByteOrder.nativeOrder());
        mTextureValue6Buffer = mTV6bb.asFloatBuffer();
        mTextureValue6Buffer.put(mTexCoordinatesB);
        mTextureValue6Buffer.position(0);

        newTexCoords(7);
        mTV7bb = ByteBuffer.allocateDirect(mNumTexCoordinatesB * VERTEX_PER_TRIANGLE * BYTES_PER_VERTEX);
        mTV7bb.order(ByteOrder.nativeOrder());
        mTextureValue7Buffer = mTV7bb.asFloatBuffer();
        mTextureValue7Buffer.put(mTexCoordinatesB);
        mTextureValue7Buffer.position(0);

        newTexCoords(8);
        mTV8bb = ByteBuffer.allocateDirect(mNumTexCoordinatesB * VERTEX_PER_TRIANGLE * BYTES_PER_VERTEX);
        mTV8bb.order(ByteOrder.nativeOrder());
        mTextureValue8Buffer = mTV8bb.asFloatBuffer();
        mTextureValue8Buffer.put(mTexCoordinatesB);
        mTextureValue8Buffer.position(0);

        newTexCoords(9);
        mTV9bb = ByteBuffer.allocateDirect(mNumTexCoordinatesB * VERTEX_PER_TRIANGLE * BYTES_PER_VERTEX);
        mTV9bb.order(ByteOrder.nativeOrder());
        mTextureValue9Buffer = mTV9bb.asFloatBuffer();
        mTextureValue9Buffer.put(mTexCoordinatesB);
        mTextureValue9Buffer.position(0);

        newTexCoords(10);
        mTV10bb = ByteBuffer.allocateDirect(mNumTexCoordinatesB * VERTEX_PER_TRIANGLE * BYTES_PER_VERTEX);
        mTV10bb.order(ByteOrder.nativeOrder());
        mTextureValue10Buffer = mTV10bb.asFloatBuffer();
        mTextureValue10Buffer.put(mTexCoordinatesB);
        mTextureValue10Buffer.position(0);

        newTexCoords(11);
        mTVJbb = ByteBuffer.allocateDirect(mNumTexCoordinatesB * VERTEX_PER_TRIANGLE * BYTES_PER_VERTEX);
        mTVJbb.order(ByteOrder.nativeOrder());
        mTextureValueJBuffer = mTVJbb.asFloatBuffer();
        mTextureValueJBuffer.put(mTexCoordinatesB);
        mTextureValueJBuffer.position(0);

        newTexCoords(12);
        mTVQbb = ByteBuffer.allocateDirect(mNumTexCoordinatesB * VERTEX_PER_TRIANGLE * BYTES_PER_VERTEX);
        mTVQbb.order(ByteOrder.nativeOrder());
        mTextureValueQBuffer = mTVQbb.asFloatBuffer();
        mTextureValueQBuffer.put(mTexCoordinatesB);
        mTextureValueQBuffer.position(0);

        newTexCoords(13);
        mTVKbb = ByteBuffer.allocateDirect(mNumTexCoordinatesB * VERTEX_PER_TRIANGLE * BYTES_PER_VERTEX);
        mTVKbb.order(ByteOrder.nativeOrder());
        mTextureValueKBuffer = mTVKbb.asFloatBuffer();
        mTextureValueKBuffer.put(mTexCoordinatesB);
        mTextureValueKBuffer.position(0);

        newTexCoords(14);
        mTVAbb = ByteBuffer.allocateDirect(mNumTexCoordinatesB * VERTEX_PER_TRIANGLE * BYTES_PER_VERTEX);
        mTVAbb.order(ByteOrder.nativeOrder());
        mTextureValueABuffer = mTVAbb.asFloatBuffer();
        mTextureValueABuffer.put(mTexCoordinatesB);
        mTextureValueABuffer.position(0);
    }

    private void newTexCoords(int count) {
        // TOP LEFT
        // X Coordinates
        mTexCoordinatesB[48] = Math.round( (count-2)/4 ) * 0.25f;
        mTexCoordinatesB[50] = mTexCoordinatesB[48];
        mTexCoordinatesB[52] = mTexCoordinatesB[48] + 0.25f;

        mTexCoordinatesB[54] = mTexCoordinatesB[48];
        mTexCoordinatesB[56] = mTexCoordinatesB[52];
        mTexCoordinatesB[58] = mTexCoordinatesB[52];

        // Y Coordinates
        mTexCoordinatesB[49] = (float) 0.75 - (( (count-2) % 4) * 0.25f);
        mTexCoordinatesB[51] = (float) mTexCoordinatesB[49] + 0.25f;
        mTexCoordinatesB[53] = mTexCoordinatesB[51];

        mTexCoordinatesB[55] = mTexCoordinatesB[49];
        mTexCoordinatesB[57] = mTexCoordinatesB[51];
        mTexCoordinatesB[59] = mTexCoordinatesB[49];

        //BOTTOM RIGHT
        // X Coordinates
        mTexCoordinatesB[192] = mTexCoordinatesB[52];
        mTexCoordinatesB[194] = mTexCoordinatesB[52];
        mTexCoordinatesB[196] = mTexCoordinatesB[48];

        mTexCoordinatesB[198] = mTexCoordinatesB[52];
        mTexCoordinatesB[200] = mTexCoordinatesB[48];
        mTexCoordinatesB[202] = mTexCoordinatesB[48];


        mTexCoordinatesB[193] = mTexCoordinatesB[51];
        mTexCoordinatesB[195] = mTexCoordinatesB[49];
        mTexCoordinatesB[197] = mTexCoordinatesB[49];

        mTexCoordinatesB[199] = mTexCoordinatesB[51];
        mTexCoordinatesB[201] = mTexCoordinatesB[49];
        mTexCoordinatesB[203] = mTexCoordinatesB[51];
    }



    /**
    IMPORTED DATA
    **/

    //VERTICES
    private float[] mVertices = {
        -1.125000f, 1.625000f, 0.016000f,
        -0.500000f, 1.625000f, 0.016000f,
        -1.125000f, 1.750000f, 0.016000f,
        -0.500000f, 1.750000f, 0.016000f,
        -1.125000f, 1.750000f, 0.016000f,
        -0.500000f, 1.625000f, 0.016000f,
        -0.500000f, 1.750000f, 0.016000f,
        -0.500000f, 1.625000f, 0.016000f,
        0.500000f, 1.625000f, 0.016000f,
        -0.500000f, 1.750000f, 0.016000f,
        0.500000f, 1.625000f, 0.016000f,
        0.500000f, 1.750000f, 0.016000f,
        0.500000f, 1.750000f, 0.016000f,
        0.500000f, 1.625000f, 0.016000f,
        1.125000f, 1.625000f, 0.016000f,
        0.500000f, 1.750000f, 0.016000f,
        1.125000f, 1.625000f, 0.016000f,
        1.125000f, 1.750000f, 0.016000f,
        -1.250000f, 1.625000f, 0.016000f,
        -1.250000f, 1.000000f, 0.016000f,
        -1.125000f, 1.000000f, 0.016000f,
        -1.250000f, 1.625000f, 0.016000f,
        -1.125000f, 1.000000f, 0.016000f,
        -1.125000f, 1.625000f, 0.016000f,
        -1.125000f, 1.625000f, 0.016000f,
        -1.125000f, 1.000000f, 0.016000f,
        -0.500000f, 1.000000f, 0.016000f,
        -1.125000f, 1.625000f, 0.016000f,
        -0.500000f, 1.000000f, 0.016000f,
        -0.500000f, 1.625000f, 0.016000f,
        -0.500000f, 1.625000f, 0.016000f,
        -0.500000f, 1.000000f, 0.016000f,
        0.500000f, 1.000000f, 0.016000f,
        -0.500000f, 1.625000f, 0.016000f,
        0.500000f, 1.000000f, 0.016000f,
        0.500000f, 1.625000f, 0.016000f,
        0.500000f, 1.625000f, 0.016000f,
        0.500000f, 1.000000f, 0.016000f,
        1.125000f, 1.000000f, 0.016000f,
        0.500000f, 1.625000f, 0.016000f,
        1.125000f, 1.000000f, 0.016000f,
        1.125000f, 1.625000f, 0.016000f,
        1.125000f, 1.625000f, 0.016000f,
        1.125000f, 1.000000f, 0.016000f,
        1.250000f, 1.000000f, 0.016000f,
        1.125000f, 1.625000f, 0.016000f,
        1.250000f, 1.000000f, 0.016000f,
        1.250000f, 1.625000f, 0.016000f,
        -1.250000f, 1.000000f, 0.016000f,
        -1.250000f, -1.000000f, 0.016000f,
        -1.125000f, -1.000000f, 0.016000f,
        -1.250000f, 1.000000f, 0.016000f,
        -1.125000f, -1.000000f, 0.016000f,
        -1.125000f, 1.000000f, 0.016000f,
        -1.125000f, 1.000000f, 0.016000f,
        -1.125000f, -1.000000f, 0.016000f,
        -0.500000f, -1.000000f, 0.016000f,
        -1.125000f, 1.000000f, 0.016000f,
        -0.500000f, -1.000000f, 0.016000f,
        -0.500000f, 1.000000f, 0.016000f,
        -0.500000f, 1.000000f, 0.016000f,
        -0.500000f, -1.000000f, 0.016000f,
        0.500000f, -1.000000f, 0.016000f,
        -0.500000f, 1.000000f, 0.016000f,
        0.500000f, -1.000000f, 0.016000f,
        0.500000f, 1.000000f, 0.016000f,
        0.500000f, 1.000000f, 0.016000f,
        0.500000f, -1.000000f, 0.016000f,
        1.125000f, -1.000000f, 0.016000f,
        0.500000f, 1.000000f, 0.016000f,
        1.125000f, -1.000000f, 0.016000f,
        1.125000f, 1.000000f, 0.016000f,
        1.125000f, 1.000000f, 0.016000f,
        1.125000f, -1.000000f, 0.016000f,
        1.250000f, -1.000000f, 0.016000f,
        1.125000f, 1.000000f, 0.016000f,
        1.250000f, -1.000000f, 0.016000f,
        1.250000f, 1.000000f, 0.016000f,
        -1.250000f, -1.000000f, 0.016000f,
        -1.250000f, -1.625000f, 0.016000f,
        -1.125000f, -1.625000f, 0.016000f,
        -1.250000f, -1.000000f, 0.016000f,
        -1.125000f, -1.625000f, 0.016000f,
        -1.125000f, -1.000000f, 0.016000f,
        -1.125000f, -1.000000f, 0.016000f,
        -1.125000f, -1.625000f, 0.016000f,
        -0.500000f, -1.625000f, 0.016000f,
        -1.125000f, -1.000000f, 0.016000f,
        -0.500000f, -1.625000f, 0.016000f,
        -0.500000f, -1.000000f, 0.016000f,
        -0.500000f, -1.000000f, 0.016000f,
        -0.500000f, -1.625000f, 0.016000f,
        0.500000f, -1.625000f, 0.016000f,
        -0.500000f, -1.000000f, 0.016000f,
        0.500000f, -1.625000f, 0.016000f,
        0.500000f, -1.000000f, 0.016000f,
        0.500000f, -1.000000f, 0.016000f,
        0.500000f, -1.625000f, 0.016000f,
        1.125000f, -1.625000f, 0.016000f,
        0.500000f, -1.000000f, 0.016000f,
        1.125000f, -1.625000f, 0.016000f,
        1.125000f, -1.000000f, 0.016000f,
        1.125000f, -1.000000f, 0.016000f,
        1.125000f, -1.625000f, 0.016000f,
        1.250000f, -1.625000f, 0.016000f,
        1.125000f, -1.000000f, 0.016000f,
        1.250000f, -1.625000f, 0.016000f,
        1.250000f, -1.000000f, 0.016000f,
        -1.125000f, -1.625000f, 0.016000f,
        -1.125000f, -1.750000f, 0.016000f,
        -0.500000f, -1.750000f, 0.016000f,
        -1.125000f, -1.625000f, 0.016000f,
        -0.500000f, -1.750000f, 0.016000f,
        -0.500000f, -1.625000f, 0.016000f,
        -0.500000f, -1.625000f, 0.016000f,
        -0.500000f, -1.750000f, 0.016000f,
        0.500000f, -1.750000f, 0.016000f,
        -0.500000f, -1.625000f, 0.016000f,
        0.500000f, -1.750000f, 0.016000f,
        0.500000f, -1.625000f, 0.016000f,
        0.500000f, -1.625000f, 0.016000f,
        0.500000f, -1.750000f, 0.016000f,
        1.125000f, -1.750000f, 0.016000f,
        0.500000f, -1.625000f, 0.016000f,
        1.125000f, -1.750000f, 0.016000f,
        1.125000f, -1.625000f, 0.016000f,
        -1.250000f, 1.625000f, 0.016000f,
        -1.125000f, 1.625000f, 0.016000f,
        -1.248101f, 1.646706f, 0.016000f,
        -1.125000f, 1.625000f, 0.016000f,
        -1.242462f, 1.667753f, 0.016000f,
        -1.248101f, 1.646706f, 0.016000f,
        -1.125000f, 1.625000f, 0.016000f,
        -1.233253f, 1.687500f, 0.016000f,
        -1.242462f, 1.667753f, 0.016000f,
        -1.125000f, 1.625000f, 0.016000f,
        -1.220755f, 1.705348f, 0.016000f,
        -1.233253f, 1.687500f, 0.016000f,
        -1.125000f, 1.625000f, 0.016000f,
        -1.205348f, 1.720756f, 0.016000f,
        -1.220755f, 1.705348f, 0.016000f,
        -1.125000f, 1.625000f, 0.016000f,
        -1.187500f, 1.733253f, 0.016000f,
        -1.205348f, 1.720756f, 0.016000f,
        -1.125000f, 1.625000f, 0.016000f,
        -1.167752f, 1.742462f, 0.016000f,
        -1.187500f, 1.733253f, 0.016000f,
        -1.125000f, 1.625000f, 0.016000f,
        -1.146706f, 1.748101f, 0.016000f,
        -1.167752f, 1.742462f, 0.016000f,
        -1.125000f, 1.625000f, 0.016000f,
        -1.125000f, 1.750000f, 0.016000f,
        -1.146706f, 1.748101f, 0.016000f,
        1.125000f, 1.750000f, 0.016000f,
        1.125000f, 1.625000f, 0.016000f,
        1.146706f, 1.748101f, 0.016000f,
        1.125000f, 1.625000f, 0.016000f,
        1.167753f, 1.742462f, 0.016000f,
        1.146706f, 1.748101f, 0.016000f,
        1.125000f, 1.625000f, 0.016000f,
        1.187500f, 1.733253f, 0.016000f,
        1.167753f, 1.742462f, 0.016000f,
        1.125000f, 1.625000f, 0.016000f,
        1.205348f, 1.720755f, 0.016000f,
        1.187500f, 1.733253f, 0.016000f,
        1.125000f, 1.625000f, 0.016000f,
        1.220756f, 1.705348f, 0.016000f,
        1.205348f, 1.720755f, 0.016000f,
        1.125000f, 1.625000f, 0.016000f,
        1.233253f, 1.687500f, 0.016000f,
        1.220756f, 1.705348f, 0.016000f,
        1.125000f, 1.625000f, 0.016000f,
        1.242462f, 1.667752f, 0.016000f,
        1.233253f, 1.687500f, 0.016000f,
        1.125000f, 1.625000f, 0.016000f,
        1.248101f, 1.646706f, 0.016000f,
        1.242462f, 1.667752f, 0.016000f,
        1.125000f, 1.625000f, 0.016000f,
        1.250000f, 1.625000f, 0.016000f,
        1.248101f, 1.646706f, 0.016000f,
        1.125000f, -1.625000f, 0.016000f,
        1.248101f, -1.646706f, 0.016000f,
        1.250000f, -1.625000f, 0.016000f,
        1.125000f, -1.625000f, 0.016000f,
        1.242462f, -1.667753f, 0.016000f,
        1.248101f, -1.646706f, 0.016000f,
        1.125000f, -1.625000f, 0.016000f,
        1.233253f, -1.687500f, 0.016000f,
        1.242462f, -1.667753f, 0.016000f,
        1.125000f, -1.625000f, 0.016000f,
        1.220755f, -1.705348f, 0.016000f,
        1.233253f, -1.687500f, 0.016000f,
        1.125000f, -1.625000f, 0.016000f,
        1.205348f, -1.720756f, 0.016000f,
        1.220755f, -1.705348f, 0.016000f,
        1.125000f, -1.625000f, 0.016000f,
        1.187500f, -1.733253f, 0.016000f,
        1.205348f, -1.720756f, 0.016000f,
        1.125000f, -1.625000f, 0.016000f,
        1.167752f, -1.742462f, 0.016000f,
        1.187500f, -1.733253f, 0.016000f,
        1.125000f, -1.625000f, 0.016000f,
        1.146706f, -1.748101f, 0.016000f,
        1.167752f, -1.742462f, 0.016000f,
        1.125000f, -1.625000f, 0.016000f,
        1.125000f, -1.750000f, 0.016000f,
        1.146706f, -1.748101f, 0.016000f,
        -1.125000f, -1.625000f, 0.016000f,
        -1.146706f, -1.748101f, 0.016000f,
        -1.125000f, -1.750000f, 0.016000f,
        -1.125000f, -1.625000f, 0.016000f,
        -1.167753f, -1.742462f, 0.016000f,
        -1.146706f, -1.748101f, 0.016000f,
        -1.125000f, -1.625000f, 0.016000f,
        -1.187500f, -1.733253f, 0.016000f,
        -1.167753f, -1.742462f, 0.016000f,
        -1.125000f, -1.625000f, 0.016000f,
        -1.205348f, -1.720755f, 0.016000f,
        -1.187500f, -1.733253f, 0.016000f,
        -1.125000f, -1.625000f, 0.016000f,
        -1.220756f, -1.705348f, 0.016000f,
        -1.205348f, -1.720755f, 0.016000f,
        -1.125000f, -1.625000f, 0.016000f,
        -1.233253f, -1.687500f, 0.016000f,
        -1.220756f, -1.705348f, 0.016000f,
        -1.125000f, -1.625000f, 0.016000f,
        -1.242462f, -1.667752f, 0.016000f,
        -1.233253f, -1.687500f, 0.016000f,
        -1.125000f, -1.625000f, 0.016000f,
        -1.248101f, -1.646706f, 0.016000f,
        -1.242462f, -1.667752f, 0.016000f,
        -1.125000f, -1.625000f, 0.016000f,
        -1.250000f, -1.625000f, 0.016000f,
        -1.248101f, -1.646706f, 0.016000f,
        -1.125000f, 1.750000f, 0.000000f,
        -0.500000f, 1.625000f, 0.000000f,
        -1.125000f, 1.625000f, 0.000000f,
        -0.500000f, 1.625000f, 0.000000f,
        -1.125000f, 1.750000f, 0.000000f,
        -0.500000f, 1.750000f, 0.000000f,
        0.500000f, 1.625000f, 0.000000f,
        -0.500000f, 1.625000f, 0.000000f,
        -0.500000f, 1.750000f, 0.000000f,
        0.500000f, 1.750000f, 0.000000f,
        0.500000f, 1.625000f, 0.000000f,
        -0.500000f, 1.750000f, 0.000000f,
        1.125000f, 1.625000f, 0.000000f,
        0.500000f, 1.625000f, 0.000000f,
        0.500000f, 1.750000f, 0.000000f,
        1.125000f, 1.750000f, 0.000000f,
        1.125000f, 1.625000f, 0.000000f,
        0.500000f, 1.750000f, 0.000000f,
        -1.125000f, 1.000000f, 0.000000f,
        -1.250000f, 1.000000f, 0.000000f,
        -1.250000f, 1.625000f, 0.000000f,
        -1.125000f, 1.625000f, 0.000000f,
        -1.125000f, 1.000000f, 0.000000f,
        -1.250000f, 1.625000f, 0.000000f,
        -0.500000f, 1.000000f, 0.000000f,
        -1.125000f, 1.000000f, 0.000000f,
        -1.125000f, 1.625000f, 0.000000f,
        -0.500000f, 1.625000f, 0.000000f,
        -0.500000f, 1.000000f, 0.000000f,
        -1.125000f, 1.625000f, 0.000000f,
        0.500000f, 1.000000f, 0.000000f,
        -0.500000f, 1.000000f, 0.000000f,
        -0.500000f, 1.625000f, 0.000000f,
        0.500000f, 1.625000f, 0.000000f,
        0.500000f, 1.000000f, 0.000000f,
        -0.500000f, 1.625000f, 0.000000f,
        1.125000f, 1.000000f, 0.000000f,
        0.500000f, 1.000000f, 0.000000f,
        0.500000f, 1.625000f, 0.000000f,
        1.125000f, 1.625000f, 0.000000f,
        1.125000f, 1.000000f, 0.000000f,
        0.500000f, 1.625000f, 0.000000f,
        1.250000f, 1.000000f, 0.000000f,
        1.125000f, 1.000000f, 0.000000f,
        1.125000f, 1.625000f, 0.000000f,
        1.250000f, 1.625000f, 0.000000f,
        1.250000f, 1.000000f, 0.000000f,
        1.125000f, 1.625000f, 0.000000f,
        -1.125000f, -1.000000f, 0.000000f,
        -1.250000f, -1.000000f, 0.000000f,
        -1.250000f, 1.000000f, 0.000000f,
        -1.125000f, 1.000000f, 0.000000f,
        -1.125000f, -1.000000f, 0.000000f,
        -1.250000f, 1.000000f, 0.000000f,
        -0.500000f, -1.000000f, 0.000000f,
        -1.125000f, -1.000000f, 0.000000f,
        -1.125000f, 1.000000f, 0.000000f,
        -0.500000f, 1.000000f, 0.000000f,
        -0.500000f, -1.000000f, 0.000000f,
        -1.125000f, 1.000000f, 0.000000f,
        0.500000f, -1.000000f, 0.000000f,
        -0.500000f, -1.000000f, 0.000000f,
        -0.500000f, 1.000000f, 0.000000f,
        0.500000f, 1.000000f, 0.000000f,
        0.500000f, -1.000000f, 0.000000f,
        -0.500000f, 1.000000f, 0.000000f,
        1.125000f, -1.000000f, 0.000000f,
        0.500000f, -1.000000f, 0.000000f,
        0.500000f, 1.000000f, 0.000000f,
        1.125000f, 1.000000f, 0.000000f,
        1.125000f, -1.000000f, 0.000000f,
        0.500000f, 1.000000f, 0.000000f,
        1.250000f, -1.000000f, 0.000000f,
        1.125000f, -1.000000f, 0.000000f,
        1.125000f, 1.000000f, 0.000000f,
        1.250000f, 1.000000f, 0.000000f,
        1.250000f, -1.000000f, 0.000000f,
        1.125000f, 1.000000f, 0.000000f,
        -1.125000f, -1.625000f, 0.000000f,
        -1.250000f, -1.625000f, 0.000000f,
        -1.250000f, -1.000000f, 0.000000f,
        -1.125000f, -1.000000f, 0.000000f,
        -1.125000f, -1.625000f, 0.000000f,
        -1.250000f, -1.000000f, 0.000000f,
        -0.500000f, -1.625000f, 0.000000f,
        -1.125000f, -1.625000f, 0.000000f,
        -1.125000f, -1.000000f, 0.000000f,
        -0.500000f, -1.000000f, 0.000000f,
        -0.500000f, -1.625000f, 0.000000f,
        -1.125000f, -1.000000f, 0.000000f,
        0.500000f, -1.625000f, 0.000000f,
        -0.500000f, -1.625000f, 0.000000f,
        -0.500000f, -1.000000f, 0.000000f,
        0.500000f, -1.000000f, 0.000000f,
        0.500000f, -1.625000f, 0.000000f,
        -0.500000f, -1.000000f, 0.000000f,
        1.125000f, -1.625000f, 0.000000f,
        0.500000f, -1.625000f, 0.000000f,
        0.500000f, -1.000000f, 0.000000f,
        1.125000f, -1.000000f, 0.000000f,
        1.125000f, -1.625000f, 0.000000f,
        0.500000f, -1.000000f, 0.000000f,
        1.250000f, -1.625000f, 0.000000f,
        1.125000f, -1.625000f, 0.000000f,
        1.125000f, -1.000000f, 0.000000f,
        1.250000f, -1.000000f, 0.000000f,
        1.250000f, -1.625000f, 0.000000f,
        1.125000f, -1.000000f, 0.000000f,
        -0.500000f, -1.750000f, 0.000000f,
        -1.125000f, -1.750000f, 0.000000f,
        -1.125000f, -1.625000f, 0.000000f,
        -0.500000f, -1.625000f, 0.000000f,
        -0.500000f, -1.750000f, 0.000000f,
        -1.125000f, -1.625000f, 0.000000f,
        0.500000f, -1.750000f, 0.000000f,
        -0.500000f, -1.750000f, 0.000000f,
        -0.500000f, -1.625000f, 0.000000f,
        0.500000f, -1.625000f, 0.000000f,
        0.500000f, -1.750000f, 0.000000f,
        -0.500000f, -1.625000f, 0.000000f,
        1.125000f, -1.750000f, 0.000000f,
        0.500000f, -1.750000f, 0.000000f,
        0.500000f, -1.625000f, 0.000000f,
        1.125000f, -1.625000f, 0.000000f,
        1.125000f, -1.750000f, 0.000000f,
        0.500000f, -1.625000f, 0.000000f,
        -1.248101f, 1.646706f, 0.000000f,
        -1.125000f, 1.625000f, 0.000000f,
        -1.250000f, 1.625000f, 0.000000f,
        -1.248101f, 1.646706f, 0.000000f,
        -1.242462f, 1.667753f, 0.000000f,
        -1.125000f, 1.625000f, 0.000000f,
        -1.242462f, 1.667753f, 0.000000f,
        -1.233253f, 1.687500f, 0.000000f,
        -1.125000f, 1.625000f, 0.000000f,
        -1.233253f, 1.687500f, 0.000000f,
        -1.220755f, 1.705348f, 0.000000f,
        -1.125000f, 1.625000f, 0.000000f,
        -1.220755f, 1.705348f, 0.000000f,
        -1.205348f, 1.720756f, 0.000000f,
        -1.125000f, 1.625000f, 0.000000f,
        -1.205348f, 1.720756f, 0.000000f,
        -1.187500f, 1.733253f, 0.000000f,
        -1.125000f, 1.625000f, 0.000000f,
        -1.187500f, 1.733253f, 0.000000f,
        -1.167752f, 1.742462f, 0.000000f,
        -1.125000f, 1.625000f, 0.000000f,
        -1.167752f, 1.742462f, 0.000000f,
        -1.146706f, 1.748101f, 0.000000f,
        -1.125000f, 1.625000f, 0.000000f,
        -1.146706f, 1.748101f, 0.000000f,
        -1.125000f, 1.750000f, 0.000000f,
        -1.125000f, 1.625000f, 0.000000f,
        1.146706f, 1.748101f, 0.000000f,
        1.125000f, 1.625000f, 0.000000f,
        1.125000f, 1.750000f, 0.000000f,
        1.146706f, 1.748101f, 0.000000f,
        1.167753f, 1.742462f, 0.000000f,
        1.125000f, 1.625000f, 0.000000f,
        1.167753f, 1.742462f, 0.000000f,
        1.187500f, 1.733253f, 0.000000f,
        1.125000f, 1.625000f, 0.000000f,
        1.187500f, 1.733253f, 0.000000f,
        1.205348f, 1.720755f, 0.000000f,
        1.125000f, 1.625000f, 0.000000f,
        1.205348f, 1.720755f, 0.000000f,
        1.220756f, 1.705348f, 0.000000f,
        1.125000f, 1.625000f, 0.000000f,
        1.220756f, 1.705348f, 0.000000f,
        1.233253f, 1.687500f, 0.000000f,
        1.125000f, 1.625000f, 0.000000f,
        1.233253f, 1.687500f, 0.000000f,
        1.242462f, 1.667752f, 0.000000f,
        1.125000f, 1.625000f, 0.000000f,
        1.242462f, 1.667752f, 0.000000f,
        1.248101f, 1.646706f, 0.000000f,
        1.125000f, 1.625000f, 0.000000f,
        1.248101f, 1.646706f, 0.000000f,
        1.250000f, 1.625000f, 0.000000f,
        1.125000f, 1.625000f, 0.000000f,
        1.250000f, -1.625000f, 0.000000f,
        1.248101f, -1.646706f, 0.000000f,
        1.125000f, -1.625000f, 0.000000f,
        1.248101f, -1.646706f, 0.000000f,
        1.242462f, -1.667753f, 0.000000f,
        1.125000f, -1.625000f, 0.000000f,
        1.242462f, -1.667753f, 0.000000f,
        1.233253f, -1.687500f, 0.000000f,
        1.125000f, -1.625000f, 0.000000f,
        1.233253f, -1.687500f, 0.000000f,
        1.220755f, -1.705348f, 0.000000f,
        1.125000f, -1.625000f, 0.000000f,
        1.220755f, -1.705348f, 0.000000f,
        1.205348f, -1.720756f, 0.000000f,
        1.125000f, -1.625000f, 0.000000f,
        1.205348f, -1.720756f, 0.000000f,
        1.187500f, -1.733253f, 0.000000f,
        1.125000f, -1.625000f, 0.000000f,
        1.187500f, -1.733253f, 0.000000f,
        1.167752f, -1.742462f, 0.000000f,
        1.125000f, -1.625000f, 0.000000f,
        1.167752f, -1.742462f, 0.000000f,
        1.146706f, -1.748101f, 0.000000f,
        1.125000f, -1.625000f, 0.000000f,
        1.146706f, -1.748101f, 0.000000f,
        1.125000f, -1.750000f, 0.000000f,
        1.125000f, -1.625000f, 0.000000f,
        -1.125000f, -1.750000f, 0.000000f,
        -1.146706f, -1.748101f, 0.000000f,
        -1.125000f, -1.625000f, 0.000000f,
        -1.146706f, -1.748101f, 0.000000f,
        -1.167753f, -1.742462f, 0.000000f,
        -1.125000f, -1.625000f, 0.000000f,
        -1.167753f, -1.742462f, 0.000000f,
        -1.187500f, -1.733253f, 0.000000f,
        -1.125000f, -1.625000f, 0.000000f,
        -1.187500f, -1.733253f, 0.000000f,
        -1.205348f, -1.720755f, 0.000000f,
        -1.125000f, -1.625000f, 0.000000f,
        -1.205348f, -1.720755f, 0.000000f,
        -1.220756f, -1.705348f, 0.000000f,
        -1.125000f, -1.625000f, 0.000000f,
        -1.220756f, -1.705348f, 0.000000f,
        -1.233253f, -1.687500f, 0.000000f,
        -1.125000f, -1.625000f, 0.000000f,
        -1.233253f, -1.687500f, 0.000000f,
        -1.242462f, -1.667752f, 0.000000f,
        -1.125000f, -1.625000f, 0.000000f,
        -1.242462f, -1.667752f, 0.000000f,
        -1.248101f, -1.646706f, 0.000000f,
        -1.125000f, -1.625000f, 0.000000f,
        -1.248101f, -1.646706f, 0.000000f,
        -1.250000f, -1.625000f, 0.000000f,
        -1.125000f, -1.625000f, 0.000000f,
        -1.125000f, 1.750000f, 0.016000f,
        -0.500000f, 1.750000f, 0.016000f,
        -1.125000f, 1.750000f, 0.000000f,
        -0.500000f, 1.750000f, 0.016000f,
        -0.500000f, 1.750000f, 0.000000f,
        -1.125000f, 1.750000f, 0.000000f,
        -0.500000f, 1.750000f, 0.016000f,
        0.500000f, 1.750000f, 0.016000f,
        -0.500000f, 1.750000f, 0.000000f,
        0.500000f, 1.750000f, 0.016000f,
        0.500000f, 1.750000f, 0.000000f,
        -0.500000f, 1.750000f, 0.000000f,
        0.500000f, 1.750000f, 0.016000f,
        1.125000f, 1.750000f, 0.016000f,
        0.500000f, 1.750000f, 0.000000f,
        1.125000f, 1.750000f, 0.016000f,
        1.125000f, 1.750000f, 0.000000f,
        0.500000f, 1.750000f, 0.000000f,
        1.125000f, 1.750000f, 0.016000f,
        1.146706f, 1.748101f, 0.016000f,
        1.125000f, 1.750000f, 0.000000f,
        1.146706f, 1.748101f, 0.016000f,
        1.146706f, 1.748101f, 0.000000f,
        1.125000f, 1.750000f, 0.000000f,
        1.146706f, 1.748101f, 0.016000f,
        1.167753f, 1.742462f, 0.016000f,
        1.146706f, 1.748101f, 0.000000f,
        1.167753f, 1.742462f, 0.016000f,
        1.167753f, 1.742462f, 0.000000f,
        1.146706f, 1.748101f, 0.000000f,
        1.167753f, 1.742462f, 0.016000f,
        1.187500f, 1.733253f, 0.016000f,
        1.167753f, 1.742462f, 0.000000f,
        1.187500f, 1.733253f, 0.016000f,
        1.187500f, 1.733253f, 0.000000f,
        1.167753f, 1.742462f, 0.000000f,
        1.187500f, 1.733253f, 0.016000f,
        1.205348f, 1.720755f, 0.016000f,
        1.187500f, 1.733253f, 0.000000f,
        1.205348f, 1.720755f, 0.016000f,
        1.205348f, 1.720755f, 0.000000f,
        1.187500f, 1.733253f, 0.000000f,
        1.205348f, 1.720755f, 0.016000f,
        1.220756f, 1.705348f, 0.016000f,
        1.205348f, 1.720755f, 0.000000f,
        1.220756f, 1.705348f, 0.016000f,
        1.220756f, 1.705348f, 0.000000f,
        1.205348f, 1.720755f, 0.000000f,
        1.220756f, 1.705348f, 0.016000f,
        1.233253f, 1.687500f, 0.016000f,
        1.220756f, 1.705348f, 0.000000f,
        1.233253f, 1.687500f, 0.016000f,
        1.233253f, 1.687500f, 0.000000f,
        1.220756f, 1.705348f, 0.000000f,
        1.233253f, 1.687500f, 0.016000f,
        1.242462f, 1.667752f, 0.016000f,
        1.233253f, 1.687500f, 0.000000f,
        1.242462f, 1.667752f, 0.016000f,
        1.242462f, 1.667752f, 0.000000f,
        1.233253f, 1.687500f, 0.000000f,
        1.242462f, 1.667752f, 0.016000f,
        1.248101f, 1.646706f, 0.016000f,
        1.242462f, 1.667752f, 0.000000f,
        1.248101f, 1.646706f, 0.016000f,
        1.248101f, 1.646706f, 0.000000f,
        1.242462f, 1.667752f, 0.000000f,
        1.250000f, 1.625000f, 0.016000f,
        1.248101f, 1.646706f, 0.000000f,
        1.248101f, 1.646706f, 0.016000f,
        1.250000f, 1.625000f, 0.016000f,
        1.250000f, 1.625000f, 0.000000f,
        1.248101f, 1.646706f, 0.000000f,
        1.250000f, 1.625000f, 0.016000f,
        1.250000f, 1.000000f, 0.016000f,
        1.250000f, 1.625000f, 0.000000f,
        1.250000f, 1.000000f, 0.016000f,
        1.250000f, 1.000000f, 0.000000f,
        1.250000f, 1.625000f, 0.000000f,
        1.250000f, 1.000000f, 0.016000f,
        1.250000f, -1.000000f, 0.016000f,
        1.250000f, 1.000000f, 0.000000f,
        1.250000f, -1.000000f, 0.016000f,
        1.250000f, -1.000000f, 0.000000f,
        1.250000f, 1.000000f, 0.000000f,
        1.250000f, -1.000000f, 0.016000f,
        1.250000f, -1.625000f, 0.016000f,
        1.250000f, -1.000000f, 0.000000f,
        1.250000f, -1.625000f, 0.016000f,
        1.250000f, -1.625000f, 0.000000f,
        1.250000f, -1.000000f, 0.000000f,
        1.250000f, -1.625000f, 0.016000f,
        1.248101f, -1.646706f, 0.016000f,
        1.250000f, -1.625000f, 0.000000f,
        1.248101f, -1.646706f, 0.016000f,
        1.248101f, -1.646706f, 0.000000f,
        1.250000f, -1.625000f, 0.000000f,
        1.248101f, -1.646706f, 0.016000f,
        1.242462f, -1.667753f, 0.016000f,
        1.248101f, -1.646706f, 0.000000f,
        1.242462f, -1.667753f, 0.016000f,
        1.242462f, -1.667753f, 0.000000f,
        1.248101f, -1.646706f, 0.000000f,
        1.242462f, -1.667753f, 0.016000f,
        1.233253f, -1.687500f, 0.016000f,
        1.242462f, -1.667753f, 0.000000f,
        1.233253f, -1.687500f, 0.016000f,
        1.233253f, -1.687500f, 0.000000f,
        1.242462f, -1.667753f, 0.000000f,
        1.233253f, -1.687500f, 0.016000f,
        1.220755f, -1.705348f, 0.016000f,
        1.233253f, -1.687500f, 0.000000f,
        1.220755f, -1.705348f, 0.016000f,
        1.220755f, -1.705348f, 0.000000f,
        1.233253f, -1.687500f, 0.000000f,
        1.220755f, -1.705348f, 0.016000f,
        1.205348f, -1.720756f, 0.016000f,
        1.220755f, -1.705348f, 0.000000f,
        1.205348f, -1.720756f, 0.016000f,
        1.205348f, -1.720756f, 0.000000f,
        1.220755f, -1.705348f, 0.000000f,
        1.205348f, -1.720756f, 0.016000f,
        1.187500f, -1.733253f, 0.016000f,
        1.205348f, -1.720756f, 0.000000f,
        1.187500f, -1.733253f, 0.016000f,
        1.187500f, -1.733253f, 0.000000f,
        1.205348f, -1.720756f, 0.000000f,
        1.187500f, -1.733253f, 0.016000f,
        1.167752f, -1.742462f, 0.016000f,
        1.187500f, -1.733253f, 0.000000f,
        1.167752f, -1.742462f, 0.016000f,
        1.167752f, -1.742462f, 0.000000f,
        1.187500f, -1.733253f, 0.000000f,
        1.167752f, -1.742462f, 0.016000f,
        1.146706f, -1.748101f, 0.016000f,
        1.167752f, -1.742462f, 0.000000f,
        1.146706f, -1.748101f, 0.016000f,
        1.146706f, -1.748101f, 0.000000f,
        1.167752f, -1.742462f, 0.000000f,
        1.125000f, -1.750000f, 0.016000f,
        1.146706f, -1.748101f, 0.000000f,
        1.146706f, -1.748101f, 0.016000f,
        1.125000f, -1.750000f, 0.016000f,
        1.125000f, -1.750000f, 0.000000f,
        1.146706f, -1.748101f, 0.000000f,
        0.500000f, -1.750000f, 0.016000f,
        1.125000f, -1.750000f, 0.000000f,
        1.125000f, -1.750000f, 0.016000f,
        0.500000f, -1.750000f, 0.016000f,
        0.500000f, -1.750000f, 0.000000f,
        1.125000f, -1.750000f, 0.000000f,
        -0.500000f, -1.750000f, 0.016000f,
        0.500000f, -1.750000f, 0.000000f,
        0.500000f, -1.750000f, 0.016000f,
        -0.500000f, -1.750000f, 0.016000f,
        -0.500000f, -1.750000f, 0.000000f,
        0.500000f, -1.750000f, 0.000000f,
        -1.125000f, -1.750000f, 0.016000f,
        -0.500000f, -1.750000f, 0.000000f,
        -0.500000f, -1.750000f, 0.016000f,
        -1.125000f, -1.750000f, 0.016000f,
        -1.125000f, -1.750000f, 0.000000f,
        -0.500000f, -1.750000f, 0.000000f,
        -1.125000f, -1.750000f, 0.016000f,
        -1.146706f, -1.748101f, 0.016000f,
        -1.125000f, -1.750000f, 0.000000f,
        -1.146706f, -1.748101f, 0.016000f,
        -1.146706f, -1.748101f, 0.000000f,
        -1.125000f, -1.750000f, 0.000000f,
        -1.146706f, -1.748101f, 0.016000f,
        -1.167753f, -1.742462f, 0.016000f,
        -1.146706f, -1.748101f, 0.000000f,
        -1.167753f, -1.742462f, 0.016000f,
        -1.167753f, -1.742462f, 0.000000f,
        -1.146706f, -1.748101f, 0.000000f,
        -1.167753f, -1.742462f, 0.016000f,
        -1.187500f, -1.733253f, 0.016000f,
        -1.167753f, -1.742462f, 0.000000f,
        -1.187500f, -1.733253f, 0.016000f,
        -1.187500f, -1.733253f, 0.000000f,
        -1.167753f, -1.742462f, 0.000000f,
        -1.187500f, -1.733253f, 0.016000f,
        -1.205348f, -1.720755f, 0.016000f,
        -1.187500f, -1.733253f, 0.000000f,
        -1.205348f, -1.720755f, 0.016000f,
        -1.205348f, -1.720755f, 0.000000f,
        -1.187500f, -1.733253f, 0.000000f,
        -1.205348f, -1.720755f, 0.016000f,
        -1.220756f, -1.705348f, 0.016000f,
        -1.205348f, -1.720755f, 0.000000f,
        -1.220756f, -1.705348f, 0.016000f,
        -1.220756f, -1.705348f, 0.000000f,
        -1.205348f, -1.720755f, 0.000000f,
        -1.220756f, -1.705348f, 0.016000f,
        -1.233253f, -1.687500f, 0.016000f,
        -1.220756f, -1.705348f, 0.000000f,
        -1.233253f, -1.687500f, 0.016000f,
        -1.233253f, -1.687500f, 0.000000f,
        -1.220756f, -1.705348f, 0.000000f,
        -1.233253f, -1.687500f, 0.016000f,
        -1.242462f, -1.667752f, 0.016000f,
        -1.233253f, -1.687500f, 0.000000f,
        -1.242462f, -1.667752f, 0.016000f,
        -1.242462f, -1.667752f, 0.000000f,
        -1.233253f, -1.687500f, 0.000000f,
        -1.242462f, -1.667752f, 0.016000f,
        -1.248101f, -1.646706f, 0.016000f,
        -1.242462f, -1.667752f, 0.000000f,
        -1.248101f, -1.646706f, 0.016000f,
        -1.248101f, -1.646706f, 0.000000f,
        -1.242462f, -1.667752f, 0.000000f,
        -1.250000f, -1.625000f, 0.016000f,
        -1.248101f, -1.646706f, 0.000000f,
        -1.248101f, -1.646706f, 0.016000f,
        -1.250000f, -1.625000f, 0.016000f,
        -1.250000f, -1.625000f, 0.000000f,
        -1.248101f, -1.646706f, 0.000000f,
        -1.250000f, -1.000000f, 0.016000f,
        -1.250000f, -1.625000f, 0.000000f,
        -1.250000f, -1.625000f, 0.016000f,
        -1.250000f, -1.000000f, 0.016000f,
        -1.250000f, -1.000000f, 0.000000f,
        -1.250000f, -1.625000f, 0.000000f,
        -1.250000f, 1.000000f, 0.016000f,
        -1.250000f, -1.000000f, 0.000000f,
        -1.250000f, -1.000000f, 0.016000f,
        -1.250000f, 1.000000f, 0.016000f,
        -1.250000f, 1.000000f, 0.000000f,
        -1.250000f, -1.000000f, 0.000000f,
        -1.250000f, 1.625000f, 0.016000f,
        -1.250000f, 1.000000f, 0.000000f,
        -1.250000f, 1.000000f, 0.016000f,
        -1.250000f, 1.625000f, 0.016000f,
        -1.250000f, 1.625000f, 0.000000f,
        -1.250000f, 1.000000f, 0.000000f,
        -1.250000f, 1.625000f, 0.016000f,
        -1.248101f, 1.646706f, 0.016000f,
        -1.250000f, 1.625000f, 0.000000f,
        -1.248101f, 1.646706f, 0.016000f,
        -1.248101f, 1.646706f, 0.000000f,
        -1.250000f, 1.625000f, 0.000000f,
        -1.248101f, 1.646706f, 0.016000f,
        -1.242462f, 1.667753f, 0.016000f,
        -1.248101f, 1.646706f, 0.000000f,
        -1.242462f, 1.667753f, 0.016000f,
        -1.242462f, 1.667753f, 0.000000f,
        -1.248101f, 1.646706f, 0.000000f,
        -1.242462f, 1.667753f, 0.016000f,
        -1.233253f, 1.687500f, 0.016000f,
        -1.242462f, 1.667753f, 0.000000f,
        -1.233253f, 1.687500f, 0.016000f,
        -1.233253f, 1.687500f, 0.000000f,
        -1.242462f, 1.667753f, 0.000000f,
        -1.233253f, 1.687500f, 0.016000f,
        -1.220755f, 1.705348f, 0.016000f,
        -1.233253f, 1.687500f, 0.000000f,
        -1.220755f, 1.705348f, 0.016000f,
        -1.220755f, 1.705348f, 0.000000f,
        -1.233253f, 1.687500f, 0.000000f,
        -1.220755f, 1.705348f, 0.016000f,
        -1.205348f, 1.720756f, 0.016000f,
        -1.220755f, 1.705348f, 0.000000f,
        -1.205348f, 1.720756f, 0.016000f,
        -1.205348f, 1.720756f, 0.000000f,
        -1.220755f, 1.705348f, 0.000000f,
        -1.205348f, 1.720756f, 0.016000f,
        -1.187500f, 1.733253f, 0.016000f,
        -1.205348f, 1.720756f, 0.000000f,
        -1.187500f, 1.733253f, 0.016000f,
        -1.187500f, 1.733253f, 0.000000f,
        -1.205348f, 1.720756f, 0.000000f,
        -1.187500f, 1.733253f, 0.016000f,
        -1.167752f, 1.742462f, 0.016000f,
        -1.187500f, 1.733253f, 0.000000f,
        -1.167752f, 1.742462f, 0.016000f,
        -1.167752f, 1.742462f, 0.000000f,
        -1.187500f, 1.733253f, 0.000000f,
        -1.167752f, 1.742462f, 0.016000f,
        -1.146706f, 1.748101f, 0.016000f,
        -1.167752f, 1.742462f, 0.000000f,
        -1.146706f, 1.748101f, 0.016000f,
        -1.146706f, 1.748101f, 0.000000f,
        -1.167752f, 1.742462f, 0.000000f,
        -1.125000f, 1.750000f, 0.016000f,
        -1.146706f, 1.748101f, 0.000000f,
        -1.146706f, 1.748101f, 0.016000f,
        -1.125000f, 1.750000f, 0.016000f,
        -1.125000f, 1.750000f, 0.000000f,
        -1.146706f, 1.748101f, 0.000000f,
    };
    private int mNumVertices = mVertices.length;

    //TEXTURE COORDINATES
    private float[] mTexCoordinatesA = {
        0.975000f, 0.975f,
        0.850000f, 0.975f,
        0.975000f, 1.0f,
        0.850000f, 1.0f,
        0.975000f, 1.0f,
        0.850000f, 0.975f,
        0.850000f, 1.0f,
        0.850000f, 0.975f,
        0.650000f, 0.975f,
        0.850000f, 1.0f,
        0.650000f, 0.975f,
        0.650000f, 1.0f,
        0.650000f, 1.0f,
        0.650000f, 0.975f,
        0.525000f, 0.975f,
        0.650000f, 1.0f,
        0.525000f, 0.975f,
        0.525000f, 1.0f,
        1.000000f, 0.975f,
        1.000000f, 0.85f,
        0.975000f, 0.85f,
        1.000000f, 0.975f,
        0.975000f, 0.85f,
        0.975000f, 0.975f,
        0.525000f, 0.325f,
        0.525000f, 0.45f,
        0.650000f, 0.45f,
        0.525000f, 0.325f,
        0.650000f, 0.45f,
        0.650000f, 0.325f,
        0.850000f, 0.975f,
        0.850000f, 0.85f,
        0.650000f, 0.85f,
        0.850000f, 0.975f,
        0.650000f, 0.85f,
        0.650000f, 0.975f,
        0.650000f, 0.975f,
        0.650000f, 0.85f,
        0.525000f, 0.85f,
        0.650000f, 0.975f,
        0.525000f, 0.85f,
        0.525000f, 0.975f,
        0.525000f, 0.975f,
        0.525000f, 0.85f,
        0.500000f, 0.85f,
        0.525000f, 0.975f,
        0.500000f, 0.85f,
        0.500000f, 0.975f,
        1.000000f, 0.85f,
        1.000000f, 0.45f,
        0.975000f, 0.45f,
        1.000000f, 0.85f,
        0.975000f, 0.45f,
        0.975000f, 0.85f,
        0.525000f, 0.45f,
        0.525000f, 0.85f,
        0.650000f, 0.85f,
        0.525000f, 0.45f,
        0.650000f, 0.85f,
        0.650000f, 0.45f,
        0.650000f, 0.45f,
        0.650000f, 0.85f,
        0.850000f, 0.85f,
        0.650000f, 0.45f,
        0.850000f, 0.85f,
        0.850000f, 0.45f,
        0.850000f, 0.45f,
        0.850000f, 0.85f,
        0.975000f, 0.85f,
        0.850000f, 0.45f,
        0.975000f, 0.85f,
        0.975000f, 0.45f,
        0.525000f, 0.85f,
        0.525000f, 0.45f,
        0.500000f, 0.45f,
        0.525000f, 0.85f,
        0.500000f, 0.45f,
        0.500000f, 0.85f,
        1.000000f, 0.45f,
        1.000000f, 0.325f,
        0.975000f, 0.325f,
        1.000000f, 0.45f,
        0.975000f, 0.325f,
        0.975000f, 0.45f,
        0.975000f, 0.45f,
        0.975000f, 0.325f,
        0.850000f, 0.325f,
        0.975000f, 0.45f,
        0.850000f, 0.325f,
        0.850000f, 0.45f,
        0.850000f, 0.45f,
        0.850000f, 0.325f,
        0.650000f, 0.325f,
        0.850000f, 0.45f,
        0.650000f, 0.325f,
        0.650000f, 0.45f,
        0.850000f, 0.85f,
        0.850000f, 0.975f,
        0.975000f, 0.975f,
        0.850000f, 0.85f,
        0.975000f, 0.975f,
        0.975000f, 0.85f,
        0.525000f, 0.45f,
        0.525000f, 0.325f,
        0.500000f, 0.325f,
        0.525000f, 0.45f,
        0.500000f, 0.325f,
        0.500000f, 0.45f,
        0.975000f, 0.325f,
        0.975000f, 0.3f,
        0.850000f, 0.3f,
        0.975000f, 0.325f,
        0.850000f, 0.3f,
        0.850000f, 0.325f,
        0.850000f, 0.325f,
        0.850000f, 0.3f,
        0.650000f, 0.3f,
        0.850000f, 0.325f,
        0.650000f, 0.3f,
        0.650000f, 0.325f,
        0.650000f, 0.325f,
        0.650000f, 0.3f,
        0.525000f, 0.3f,
        0.650000f, 0.325f,
        0.525000f, 0.3f,
        0.525000f, 0.325f,
        1.000000f, 0.975f,
        0.975000f, 0.975f,
        0.999997f, 0.991228f,
        0.975000f, 0.975f,
        0.999569f, 0.993029f,
        0.999997f, 0.991228f,
        0.975000f, 0.975f,
        0.998835f, 0.994726f,
        0.999569f, 0.993029f,
        0.975000f, 0.975f,
        0.997817f, 0.996267f,
        0.998835f, 0.994726f,
        0.975000f, 0.975f,
        0.996547f, 0.997605f,
        0.997817f, 0.996267f,
        0.975000f, 0.975f,
        0.995066f, 0.998701f,
        0.996547f, 0.997605f,
        0.975000f, 0.975f,
        0.993417f, 0.999522f,
        0.995066f, 0.998701f,
        0.975000f, 0.975f,
        0.991653f, 1.000041f,
        0.993417f, 0.999522f,
        0.975000f, 0.975f,
        0.975000f, 1.0f,
        0.991653f, 1.000041f,
        0.525000f, 1.0f,
        0.525000f, 0.975f,
        0.508350f, 0.999991f,
        0.525000f, 0.975f,
        0.506616f, 0.999525f,
        0.508350f, 0.999991f,
        0.525000f, 0.975f,
        0.504988f, 0.998762f,
        0.506616f, 0.999525f,
        0.525000f, 0.975f,
        0.503516f, 0.997728f,
        0.504988f, 0.998762f,
        0.525000f, 0.975f,
        0.502245f, 0.996451f,
        0.503516f, 0.997728f,
        0.525000f, 0.975f,
        0.501213f, 0.994972f,
        0.502245f, 0.996451f,
        0.525000f, 0.975f,
        0.500454f, 0.993333f,
        0.501213f, 0.994972f,
        0.525000f, 0.975f,
        0.499991f, 0.991586f,
        0.500454f, 0.993333f,
        0.525000f, 0.975f,
        0.500000f, 0.975f,
        0.499991f, 0.991586f,
        0.525000f, 0.325f,
        0.500020f, 0.308841f,
        0.500000f, 0.325f,
        0.525000f, 0.325f,
        0.500448f, 0.307041f,
        0.500020f, 0.308841f,
        0.525000f, 0.325f,
        0.501182f, 0.305344f,
        0.500448f, 0.307041f,
        0.525000f, 0.325f,
        0.502200f, 0.303803f,
        0.501182f, 0.305344f,
        0.525000f, 0.325f,
        0.503470f, 0.302464f,
        0.502200f, 0.303803f,
        0.525000f, 0.325f,
        0.504951f, 0.301368f,
        0.503470f, 0.302464f,
        0.525000f, 0.325f,
        0.506599f, 0.300548f,
        0.504951f, 0.301368f,
        0.525000f, 0.325f,
        0.508364f, 0.300028f,
        0.506599f, 0.300548f,
        0.525000f, 0.325f,
        0.525000f, 0.3f,
        0.508364f, 0.300028f,
        0.975000f, 0.325f,
        0.991661f, 0.300027f,
        0.975000f, 0.3f,
        0.975000f, 0.325f,
        0.993394f, 0.300494f,
        0.991661f, 0.300027f,
        0.975000f, 0.325f,
        0.995022f, 0.301256f,
        0.993394f, 0.300494f,
        0.975000f, 0.325f,
        0.996494f, 0.30229f,
        0.995022f, 0.301256f,
        0.975000f, 0.325f,
        0.997766f, 0.303567f,
        0.996494f, 0.30229f,
        0.975000f, 0.325f,
        0.998797f, 0.305047f,
        0.997766f, 0.303567f,
        0.975000f, 0.325f,
        0.999556f, 0.306685f,
        0.998797f, 0.305047f,
        0.975000f, 0.325f,
        1.000020f, 0.308433f,
        0.999556f, 0.306685f,
        0.975000f, 0.325f,
        1.000000f, 0.325f,
        1.000020f, 0.308433f,
        0.475208f, 0.3f,
        0.350000f, 0.325f,
        0.475000f, 0.325f,
        0.350000f, 0.325f,
        0.475208f, 0.3f,
        0.350000f, 0.3f,
        0.150000f, 0.325f,
        0.350000f, 0.325f,
        0.350000f, 0.3f,
        0.150000f, 0.3f,
        0.150000f, 0.325f,
        0.350000f, 0.3f,
        0.025000f, 0.325f,
        0.150000f, 0.325f,
        0.150000f, 0.3f,
        0.025000f, 0.3f,
        0.025000f, 0.325f,
        0.150000f, 0.3f,
        0.475000f, 0.45f,
        0.500000f, 0.45f,
        0.500000f, 0.325f,
        0.475000f, 0.325f,
        0.475000f, 0.45f,
        0.500000f, 0.325f,
        0.350000f, 0.45f,
        0.475000f, 0.45f,
        0.475000f, 0.325f,
        0.350000f, 0.325f,
        0.350000f, 0.45f,
        0.475000f, 0.325f,
        0.150000f, 0.45f,
        0.350000f, 0.45f,
        0.350000f, 0.325f,
        0.150000f, 0.325f,
        0.150000f, 0.45f,
        0.350000f, 0.325f,
        0.025000f, 0.45f,
        0.150000f, 0.45f,
        0.150000f, 0.325f,
        0.025000f, 0.325f,
        0.025000f, 0.45f,
        0.150000f, 0.325f,
        0.000000f, 0.45f,
        0.025000f, 0.45f,
        0.025000f, 0.325f,
        0.000000f, 0.325f,
        0.000000f, 0.45f,
        0.025000f, 0.325f,
        0.475000f, 0.85f,
        0.500000f, 0.85f,
        0.500000f, 0.45f,
        0.475000f, 0.45f,
        0.475000f, 0.85f,
        0.500000f, 0.45f,
        0.350000f, 0.85f,
        0.475000f, 0.85f,
        0.475000f, 0.45f,
        0.350000f, 0.45f,
        0.350000f, 0.85f,
        0.475000f, 0.45f,
        0.150000f, 0.85f,
        0.350000f, 0.85f,
        0.350000f, 0.45f,
        0.150000f, 0.45f,
        0.150000f, 0.85f,
        0.350000f, 0.45f,
        0.025000f, 0.85f,
        0.150000f, 0.85f,
        0.150000f, 0.45f,
        0.025000f, 0.45f,
        0.025000f, 0.85f,
        0.150000f, 0.45f,
        0.000000f, 0.85f,
        0.025000f, 0.85f,
        0.025000f, 0.45f,
        0.000000f, 0.45f,
        0.000000f, 0.85f,
        0.025000f, 0.45f,
        0.475000f, 0.975f,
        0.500000f, 0.975f,
        0.500000f, 0.85f,
        0.475000f, 0.85f,
        0.475000f, 0.975f,
        0.500000f, 0.85f,
        0.350000f, 0.975f,
        0.475000f, 0.975f,
        0.475000f, 0.85f,
        0.350000f, 0.85f,
        0.350000f, 0.975f,
        0.475000f, 0.85f,
        0.150000f, 0.975f,
        0.350000f, 0.975f,
        0.350000f, 0.85f,
        0.150000f, 0.85f,
        0.150000f, 0.975f,
        0.350000f, 0.85f,
        0.025000f, 0.975f,
        0.150000f, 0.975f,
        0.150000f, 0.85f,
        0.025000f, 0.85f,
        0.025000f, 0.975f,
        0.150000f, 0.85f,
        0.000000f, 0.975f,
        0.025000f, 0.975f,
        0.025000f, 0.85f,
        0.000000f, 0.85f,
        0.000000f, 0.975f,
        0.025000f, 0.85f,
        0.350000f, 1.0f,
        0.475000f, 1.0f,
        0.475000f, 0.975f,
        0.350000f, 0.975f,
        0.350000f, 1.0f,
        0.475000f, 0.975f,
        0.150000f, 1.0f,
        0.350000f, 1.0f,
        0.350000f, 0.975f,
        0.150000f, 0.975f,
        0.150000f, 1.0f,
        0.350000f, 0.975f,
        0.025000f, 1.0f,
        0.150000f, 1.0f,
        0.150000f, 0.975f,
        0.025000f, 0.975f,
        0.025000f, 1.0f,
        0.150000f, 0.975f,
        0.499998f, 0.308833f,
        0.475000f, 0.325f,
        0.500000f, 0.325f,
        0.499998f, 0.308833f,
        0.499548f, 0.307011f,
        0.475000f, 0.325f,
        0.499548f, 0.307011f,
        0.498789f, 0.305298f,
        0.475000f, 0.325f,
        0.498789f, 0.305298f,
        0.497745f, 0.303744f,
        0.475000f, 0.325f,
        0.497745f, 0.303744f,
        0.496446f, 0.302398f,
        0.475000f, 0.325f,
        0.496446f, 0.302398f,
        0.494935f, 0.3013f,
        0.475000f, 0.325f,
        0.494935f, 0.3013f,
        0.493257f, 0.300483f,
        0.475000f, 0.325f,
        0.493257f, 0.300483f,
        0.491464f, 0.299971f,
        0.475000f, 0.325f,
        0.491464f, 0.299971f,
        0.475208f, 0.3f,
        0.475000f, 0.325f,
        0.008421f, 0.299988f,
        0.025000f, 0.325f,
        0.025000f, 0.3f,
        0.008421f, 0.299988f,
        0.006649f, 0.300482f,
        0.025000f, 0.325f,
        0.006649f, 0.300482f,
        0.004988f, 0.301279f,
        0.025000f, 0.325f,
        0.004988f, 0.301279f,
        0.003489f, 0.302353f,
        0.025000f, 0.325f,
        0.003489f, 0.302353f,
        0.002198f, 0.303673f,
        0.025000f, 0.325f,
        0.002198f, 0.303673f,
        0.001155f, 0.305199f,
        0.025000f, 0.325f,
        0.001155f, 0.305199f,
        0.000393f, 0.306885f,
        0.025000f, 0.325f,
        0.000393f, 0.306885f,
       -0.000066f, 0.308681f,
        0.025000f, 0.325f,
       -0.000066f, 0.308681f,
        0.000000f, 0.325f,
        0.025000f, 0.325f,
        0.000000f, 0.975f,
        0.000028f, 0.991133f,
        0.025000f, 0.975f,
        0.000028f, 0.991133f,
        0.000478f, 0.992955f,
        0.025000f, 0.975f,
        0.000478f, 0.992955f,
        0.001237f, 0.994668f,
        0.025000f, 0.975f,
        0.001237f, 0.994668f,
        0.002281f, 0.996222f,
        0.025000f, 0.975f,
        0.002281f, 0.996222f,
        0.003580f, 0.997568f,
        0.025000f, 0.975f,
        0.003580f, 0.997568f,
        0.005091f, 0.998666f,
        0.025000f, 0.975f,
        0.005091f, 0.998666f,
        0.006769f, 0.999483f,
        0.025000f, 0.975f,
        0.006769f, 0.999483f,
        0.008562f, 0.999995f,
        0.025000f, 0.975f,
        0.008562f, 0.999995f,
        0.025000f, 1.0f,
        0.025000f, 0.975f,
        0.475000f, 1.0f,
        0.491506f, 1.000081f,
        0.475000f, 0.975f,
        0.491506f, 1.000081f,
        0.493278f, 0.999587f,
        0.475000f, 0.975f,
        0.493278f, 0.999587f,
        0.494939f, 0.99879f,
        0.475000f, 0.975f,
        0.494939f, 0.99879f,
        0.496438f, 0.997716f,
        0.475000f, 0.975f,
        0.496438f, 0.997716f,
        0.497729f, 0.996396f,
        0.475000f, 0.975f,
        0.497729f, 0.996396f,
        0.498772f, 0.99487f,
        0.475000f, 0.975f,
        0.498772f, 0.99487f,
        0.499535f, 0.993184f,
        0.475000f, 0.975f,
        0.499535f, 0.993184f,
        0.499994f, 0.991388f,
        0.475000f, 0.975f,
        0.499994f, 0.991388f,
        0.500000f, 0.975f,
        0.475000f, 0.975f,
        0.000000f, 0.277344f,
        0.064405f, 0.277344f,
        0.000354f, 0.257812f,
        0.064405f, 0.277344f,
        0.064759f, 0.257812f,
        0.000354f, 0.257812f,
        0.064405f, 0.277344f,
        0.128817f, 0.277344f,
        0.064759f, 0.257812f,
        0.128817f, 0.277344f,
        0.129170f, 0.257812f,
        0.064759f, 0.257812f,
        0.128817f, 0.277344f,
        0.193184f, 0.277344f,
        0.129170f, 0.257812f,
        0.193184f, 0.277344f,
        0.193537f, 0.257812f,
        0.129170f, 0.257812f,
        0.193184f, 0.277344f,
        0.204633f, 0.277344f,
        0.193537f, 0.257812f,
        0.204633f, 0.277344f,
        0.204983f, 0.257812f,
        0.193537f, 0.257812f,
        0.204633f, 0.277344f,
        0.216066f, 0.277344f,
        0.204983f, 0.257812f,
        0.216066f, 0.277344f,
        0.216416f, 0.257812f,
        0.204983f, 0.257812f,
        0.216066f, 0.277344f,
        0.227484f, 0.277344f,
        0.216416f, 0.257812f,
        0.227484f, 0.277344f,
        0.227832f, 0.257812f,
        0.216416f, 0.257812f,
        0.227484f, 0.277344f,
        0.238882f, 0.277344f,
        0.227832f, 0.257812f,
        0.238882f, 0.277344f,
        0.239229f, 0.257812f,
        0.227832f, 0.257812f,
        0.238882f, 0.277344f,
        0.250256f, 0.277344f,
        0.239229f, 0.257812f,
        0.250256f, 0.277344f,
        0.250602f, 0.257812f,
        0.239229f, 0.257812f,
        0.250256f, 0.277344f,
        0.261603f, 0.277344f,
        0.250602f, 0.257812f,
        0.261603f, 0.277344f,
        0.261947f, 0.257812f,
        0.250602f, 0.257812f,
        0.261603f, 0.277344f,
        0.272918f, 0.277344f,
        0.261947f, 0.257812f,
        0.272918f, 0.277344f,
        0.273261f, 0.257812f,
        0.261947f, 0.257812f,
        0.272918f, 0.277344f,
        0.284199f, 0.277344f,
        0.273261f, 0.257812f,
        0.284199f, 0.277344f,
        0.284541f, 0.257812f,
        0.273261f, 0.257812f,
        0.295441f, 0.277344f,
        0.284541f, 0.257812f,
        0.284199f, 0.277344f,
        0.295441f, 0.277344f,
        0.295782f, 0.257813f,
        0.284541f, 0.257812f,
        0.295441f, 0.277344f,
        0.358341f, 0.277344f,
        0.295782f, 0.257813f,
        0.358341f, 0.277344f,
        0.358680f, 0.257812f,
        0.295782f, 0.257813f,
        0.358341f, 0.277344f,
        0.420884f, 0.277344f,
        0.358680f, 0.257812f,
        0.420884f, 0.277344f,
        0.421220f, 0.257812f,
        0.358680f, 0.257812f,
        0.420884f, 0.277344f,
        0.482923f, 0.277344f,
        0.421220f, 0.257812f,
        0.482923f, 0.277344f,
        0.483255f, 0.257812f,
        0.421220f, 0.257812f,
        0.482923f, 0.277344f,
        0.493861f, 0.277344f,
        0.483255f, 0.257812f,
        0.493861f, 0.277344f,
        0.494190f, 0.257812f,
        0.483255f, 0.257812f,
        0.493861f, 0.277344f,
        0.504685f, 0.277344f,
        0.494190f, 0.257812f,
        0.504685f, 0.277344f,
        0.505011f, 0.257812f,
        0.494190f, 0.257812f,
        0.504685f, 0.277344f,
        0.515391f, 0.277344f,
        0.505011f, 0.257812f,
        0.515391f, 0.277344f,
        0.515712f, 0.257812f,
        0.505011f, 0.257812f,
        0.515391f, 0.277344f,
        0.525972f, 0.277344f,
        0.515712f, 0.257812f,
        0.525972f, 0.277344f,
        0.526290f, 0.257812f,
        0.515712f, 0.257812f,
        0.525972f, 0.277344f,
        0.536426f, 0.277334f,
        0.526290f, 0.257812f,
        0.536426f, 0.277334f,
        0.536740f, 0.257812f,
        0.526290f, 0.257812f,
        0.536426f, 0.277334f,
        0.546747f, 0.277344f,
        0.536740f, 0.257812f,
        0.546747f, 0.277344f,
        0.547057f, 0.257812f,
        0.536740f, 0.257812f,
        0.546747f, 0.277344f,
        0.556930f, 0.277344f,
        0.547057f, 0.257812f,
        0.556930f, 0.277344f,
        0.557237f, 0.257813f,
        0.547057f, 0.257812f,
        0.556930f, 0.277344f,
        0.566972f, 0.277344f,
        0.557237f, 0.257813f,
        0.566972f, 0.277344f,
        0.567275f, 0.257812f,
        0.557237f, 0.257813f,
        0.576866f, 0.277344f,
        0.567275f, 0.257812f,
        0.566972f, 0.277344f,
        0.576866f, 0.277344f,
        0.577167f, 0.257813f,
        0.567275f, 0.257812f,
        0.631581f, 0.277344f,
        0.577167f, 0.257813f,
        0.576866f, 0.277344f,
        0.631581f, 0.277344f,
        0.631880f, 0.257812f,
        0.577167f, 0.257813f,
        0.685327f, 0.277344f,
        0.631880f, 0.257812f,
        0.631581f, 0.277344f,
        0.685327f, 0.277344f,
        0.685625f, 0.257812f,
        0.631880f, 0.257812f,
        0.737961f, 0.277344f,
        0.685625f, 0.257812f,
        0.685327f, 0.277344f,
        0.737961f, 0.277344f,
        0.738260f, 0.257812f,
        0.685625f, 0.257812f,
        0.737961f, 0.277344f,
        0.747110f, 0.277344f,
        0.738260f, 0.257812f,
        0.747110f, 0.277344f,
        0.747407f, 0.257812f,
        0.738260f, 0.257812f,
        0.747110f, 0.277344f,
        0.756044f, 0.277344f,
        0.747407f, 0.257812f,
        0.756044f, 0.277344f,
        0.756339f, 0.257812f,
        0.747407f, 0.257812f,
        0.756044f, 0.277344f,
        0.764769f, 0.277344f,
        0.756339f, 0.257812f,
        0.764769f, 0.277344f,
        0.765062f, 0.257812f,
        0.756339f, 0.257812f,
        0.764769f, 0.277344f,
        0.773293f, 0.277344f,
        0.765062f, 0.257812f,
        0.773293f, 0.277344f,
        0.773584f, 0.257812f,
        0.765062f, 0.257812f,
        0.773293f, 0.277344f,
        0.781621f, 0.277344f,
        0.773584f, 0.257812f,
        0.781621f, 0.277344f,
        0.781909f, 0.257813f,
        0.773584f, 0.257812f,
        0.781621f, 0.277344f,
        0.789759f, 0.277344f,
        0.781909f, 0.257813f,
        0.789759f, 0.277344f,
        0.790046f, 0.257812f,
        0.781909f, 0.257813f,
        0.789759f, 0.277344f,
        0.797715f, 0.277344f,
        0.790046f, 0.257812f,
        0.797715f, 0.277344f,
        0.797999f, 0.257812f,
        0.790046f, 0.257812f,
        0.797715f, 0.277344f,
        0.805494f, 0.277344f,
        0.797999f, 0.257812f,
        0.805494f, 0.277344f,
        0.805775f, 0.257813f,
        0.797999f, 0.257812f,
        0.813103f, 0.277344f,
        0.805775f, 0.257813f,
        0.805494f, 0.277344f,
        0.813103f, 0.277344f,
        0.813381f, 0.257812f,
        0.805775f, 0.257813f,
        0.854933f, 0.277344f,
        0.813381f, 0.257812f,
        0.813103f, 0.277344f,
        0.854933f, 0.277344f,
        0.855209f, 0.257812f,
        0.813381f, 0.257812f,
        0.896044f, 0.277344f,
        0.855209f, 0.257812f,
        0.854933f, 0.277344f,
        0.896044f, 0.277344f,
        0.896318f, 0.257812f,
        0.855209f, 0.257812f,
        0.936621f, 0.277344f,
        0.896318f, 0.257812f,
        0.896044f, 0.277344f,
        0.936621f, 0.277344f,
        0.936894f, 0.257812f,
        0.896318f, 0.257812f,
        0.936621f, 0.277344f,
        0.943782f, 0.277344f,
        0.936894f, 0.257812f,
        0.943782f, 0.277344f,
        0.944055f, 0.257813f,
        0.936894f, 0.257812f,
        0.943782f, 0.277344f,
        0.950891f, 0.277344f,
        0.944055f, 0.257813f,
        0.950891f, 0.277344f,
        0.951163f, 0.257813f,
        0.944055f, 0.257813f,
        0.950891f, 0.277344f,
        0.957954f, 0.277344f,
        0.951163f, 0.257813f,
        0.957954f, 0.277344f,
        0.958225f, 0.257813f,
        0.951163f, 0.257813f,
        0.957954f, 0.277344f,
        0.964977f, 0.277344f,
        0.958225f, 0.257813f,
        0.964977f, 0.277344f,
        0.965247f, 0.257812f,
        0.958225f, 0.257813f,
        0.964977f, 0.277344f,
        0.971968f, 0.277344f,
        0.965247f, 0.257812f,
        0.971968f, 0.277344f,
        0.972237f, 0.257812f,
        0.965247f, 0.257812f,
        0.971968f, 0.277344f,
        0.978933f, 0.277344f,
        0.972237f, 0.257812f,
        0.978933f, 0.277344f,
        0.979201f, 0.257813f,
        0.972237f, 0.257812f,
        0.978933f, 0.277344f,
        0.985878f, 0.277344f,
        0.979201f, 0.257813f,
        0.985878f, 0.277344f,
        0.986145f, 0.257812f,
        0.979201f, 0.257813f,
        0.985878f, 0.277344f,
        0.992810f, 0.277344f,
        0.986145f, 0.257812f,
        0.992810f, 0.277344f,
        0.993076f, 0.257812f,
        0.986145f, 0.257812f,
        0.999736f, 0.277344f,
        0.993076f, 0.257812f,
        0.992810f, 0.277344f,
        0.999736f, 0.277344f,
        1.000000f, 0.257812f,
        0.993076f, 0.257812f,
    };
    private int mNumTexCoordinatesA = mTexCoordinatesA.length;

    /**
    END IMPORTED DATA
    **/

    private float[] mTexCoordinatesB = {
        0.972981f, 0.183022f,
        0.947981f, 0.183992f,
        0.972981f, 0.188022f,
        0.947981f, 0.188022f,
        0.972981f, 0.188022f,

        0.947981f, 0.183992f,
        0.947981f, 0.188022f,
        0.947981f, 0.183992f,
        0.907981f, 0.183938f,
        0.947981f, 0.188022f,

        0.907981f, 0.183938f,
        0.907981f, 0.188022f,
        0.907981f, 0.188022f,
        0.907981f, 0.183938f,
        0.882981f, 0.183022f,

        0.907981f, 0.188022f,
        0.882981f, 0.183022f,
        0.882981f, 0.188022f,
        0.977981f, 0.183022f,
        0.977981f, 0.158022f,

        0.972981f, 0.158022f,
        0.977981f, 0.183022f,
        0.972981f, 0.158022f,
        0.972981f, 0.183022f,

        0.000000f, 0.75f,
        0.000000f, 1.0f,
        0.250000f, 1.0f,
        0.000000f, 0.75f,
        0.250000f, 1.0f,
        0.250000f, 0.75f,

        0.947981f, 0.183992f,
        0.947981f, 0.158022f,
        0.907981f, 0.158022f,
        0.947981f, 0.183992f,
        0.907981f, 0.158022f,

        0.907981f, 0.183938f,
        0.907981f, 0.183938f,
        0.907981f, 0.158022f,
        0.882981f, 0.158022f,
        0.907981f, 0.183938f,

        0.882981f, 0.158022f,
        0.882981f, 0.183022f,
        0.882981f, 0.183022f,
        0.882981f, 0.158022f,
        0.877981f, 0.158022f,

        0.882981f, 0.183022f,
        0.877981f, 0.158022f,
        0.877981f, 0.183022f,
        0.977981f, 0.158022f,
        0.977981f, 0.078022f,

        0.972981f, 0.078022f,
        0.977981f, 0.158022f,
        0.972981f, 0.078022f,
        0.972981f, 0.158022f,
        0.882981f, 0.078022f,

        0.882981f, 0.158022f,
        0.907981f, 0.158022f,
        0.882981f, 0.078022f,
        0.907981f, 0.158022f,
        0.907981f, 0.078022f,

        0.907981f, 0.078022f,
        0.907981f, 0.158022f,
        0.947981f, 0.158022f,
        0.907981f, 0.078022f,
        0.947981f, 0.158022f,

        0.947981f, 0.078022f,
        0.947981f, 0.078022f,
        0.947981f, 0.158022f,
        0.972981f, 0.158022f,
        0.947981f, 0.078022f,

        0.972981f, 0.158022f,
        0.972981f, 0.078022f,
        0.882981f, 0.158022f,
        0.882981f, 0.078022f,
        0.877981f, 0.078022f,

        0.882981f, 0.158022f,
        0.877981f, 0.078022f,
        0.877981f, 0.158022f,
        0.977981f, 0.078022f,
        0.977981f, 0.053022f,

        0.972981f, 0.053022f,
        0.977981f, 0.078022f,
        0.972981f, 0.053022f,
        0.972981f, 0.078022f,
        0.972981f, 0.078022f,

        0.972981f, 0.053022f,
        0.947981f, 0.053022f,
        0.972981f, 0.078022f,
        0.947981f, 0.053022f,
        0.947981f, 0.078022f,

        0.947981f, 0.078022f,
        0.947981f, 0.053022f,
        0.907981f, 0.053022f,
        0.947981f, 0.078022f,
        0.907981f, 0.053022f,

        0.907981f, 0.078022f,
        0.250000f, 1.0f,
        0.250000f, 0.75f,
        0.000000f, 0.75f,
        0.250000f, 1.0f,
        0.000000f, 0.75f,
        0.000000f, 1.0f,
        0.882981f, 0.078022f,
        0.882981f, 0.053022f,
        0.877981f, 0.053022f,
        0.882981f, 0.078022f,
        0.877981f, 0.053022f,
        0.877981f, 0.078022f,
        0.972981f, 0.053022f,
        0.972981f, 0.048022f,
        0.947981f, 0.048022f,
        0.972981f, 0.053022f,
        0.947981f, 0.048022f,
        0.947981f, 0.053022f,
        0.947981f, 0.053022f,
        0.947981f, 0.048022f,
        0.907981f, 0.048022f,
        0.947981f, 0.053022f,
        0.907981f, 0.048022f,
        0.907981f, 0.053022f,
        0.907981f, 0.053022f,
        0.907981f, 0.048022f,
        0.882981f, 0.048022f,
        0.907981f, 0.053022f,
        0.882981f, 0.048022f,
        0.882981f, 0.053022f,
        0.977981f, 0.183022f,
        0.972981f, 0.183022f,
        0.977980f, 0.186267f,
        0.972981f, 0.183022f,
        0.977895f, 0.186628f,
        0.977980f, 0.186267f,
        0.972981f, 0.183022f,
        0.977748f, 0.186967f,
        0.977895f, 0.186628f,
        0.972981f, 0.183022f,
        0.977544f, 0.187275f,
        0.977748f, 0.186967f,
        0.972981f, 0.183022f,
        0.977290f, 0.187543f,
        0.977544f, 0.187275f,
        0.972981f, 0.183022f,
        0.976994f, 0.187762f,
        0.977290f, 0.187543f,
        0.972981f, 0.183022f,
        0.976664f, 0.187926f,
        0.976994f, 0.187762f,
        0.972981f, 0.183022f,
        0.976312f, 0.18803f,
        0.976664f, 0.187926f,
        0.972981f, 0.183022f,
        0.972981f, 0.188022f,
        0.976312f, 0.18803f,
        0.882981f, 0.188022f,
        0.882981f, 0.183022f,
        0.879651f, 0.18802f,
        0.882981f, 0.183022f,
        0.879304f, 0.187927f,
        0.879651f, 0.18802f,
        0.882981f, 0.183022f,
        0.878979f, 0.187774f,
        0.879304f, 0.187927f,
        0.882981f, 0.183022f,
        0.878684f, 0.187567f,
        0.878979f, 0.187774f,
        0.882981f, 0.183022f,
        0.878430f, 0.187312f,
        0.878684f, 0.187567f,
        0.882981f, 0.183022f,
        0.878224f, 0.187016f,
        0.878430f, 0.187312f,
        0.882981f, 0.183022f,
        0.878072f, 0.186688f,
        0.878224f, 0.187016f,
        0.882981f, 0.183022f,
        0.877979f, 0.186339f,
        0.878072f, 0.186688f,
        0.882981f, 0.183022f,
        0.877981f, 0.183022f,
        0.877979f, 0.186339f,
        0.882981f, 0.053022f,
        0.877985f, 0.04979f,
        0.877981f, 0.053022f,
        0.882981f, 0.053022f,
        0.878071f, 0.04943f,
        0.877985f, 0.04979f,
        0.882981f, 0.053022f,
        0.878217f, 0.049091f,
        0.878071f, 0.04943f,
        0.882981f, 0.053022f,
        0.878421f, 0.048782f,
        0.878217f, 0.049091f,
        0.882981f, 0.053022f,
        0.878675f, 0.048515f,
        0.878421f, 0.048782f,
        0.882981f, 0.053022f,
        0.878971f, 0.048295f,
        0.878675f, 0.048515f,
        0.882981f, 0.053022f,
        0.879301f, 0.048131f,
        0.878971f, 0.048295f,
        0.882981f, 0.053022f,
        0.879654f, 0.048027f,
        0.879301f, 0.048131f,
        0.882981f, 0.053022f,
        0.882981f, 0.048022f,
        0.879654f, 0.048027f,
        0.972981f, 0.053022f,
        0.976313f, 0.048027f,
        0.972981f, 0.048022f,
        0.972981f, 0.053022f,
        0.976660f, 0.04812f,
        0.976313f, 0.048027f,
        0.972981f, 0.053022f,
        0.976985f, 0.048273f,
        0.976660f, 0.04812f,
        0.972981f, 0.053022f,
        0.977280f, 0.04848f,
        0.976985f, 0.048273f,
        0.972981f, 0.053022f,
        0.977534f, 0.048735f,
        0.977280f, 0.04848f,
        0.972981f, 0.053022f,
        0.977740f, 0.049031f,
        0.977534f, 0.048735f,
        0.972981f, 0.053022f,
        0.977892f, 0.049359f,
        0.977740f, 0.049031f,
        0.972981f, 0.053022f,
        0.977985f, 0.049708f,
        0.977892f, 0.049359f,
        0.972981f, 0.053022f,
        0.977981f, 0.053022f,
        0.977985f, 0.049708f,
        0.873023f, 0.048022f,
        0.847981f, 0.053022f,
        0.872981f, 0.053022f,
        0.847981f, 0.053022f,
        0.873023f, 0.048022f,
        0.847981f, 0.048022f,
        0.807981f, 0.053022f,
        0.847981f, 0.053022f,
        0.847981f, 0.048022f,
        0.807981f, 0.048022f,
        0.807981f, 0.053022f,
        0.847981f, 0.048022f,
        0.782981f, 0.053022f,
        0.807981f, 0.053022f,
        0.807981f, 0.048022f,
        0.782981f, 0.048022f,
        0.782981f, 0.053022f,
        0.807981f, 0.048022f,
        0.872981f, 0.078022f,
        0.877981f, 0.078022f,
        0.877981f, 0.053022f,
        0.872981f, 0.053022f,
        0.872981f, 0.078022f,
        0.877981f, 0.053022f,
        0.847981f, 0.078022f,
        0.872981f, 0.078022f,
        0.872981f, 0.053022f,
        0.847981f, 0.053022f,
        0.847981f, 0.078022f,
        0.872981f, 0.053022f,
        0.807981f, 0.078022f,
        0.847981f, 0.078022f,
        0.847981f, 0.053022f,
        0.807981f, 0.053022f,
        0.807981f, 0.078022f,
        0.847981f, 0.053022f,
        0.782981f, 0.078022f,
        0.807981f, 0.078022f,
        0.807981f, 0.053022f,
        0.782981f, 0.053022f,
        0.782981f, 0.078022f,
        0.807981f, 0.053022f,
        0.777981f, 0.078022f,
        0.782981f, 0.078022f,
        0.782981f, 0.053022f,
        0.777981f, 0.053022f,
        0.777981f, 0.078022f,
        0.782981f, 0.053022f,
        0.872981f, 0.158022f,
        0.877981f, 0.158022f,
        0.877981f, 0.078022f,
        0.872981f, 0.078022f,
        0.872981f, 0.158022f,
        0.877981f, 0.078022f,
        0.847981f, 0.158022f,
        0.872981f, 0.158022f,
        0.872981f, 0.078022f,
        0.847981f, 0.078022f,
        0.847981f, 0.158022f,
        0.872981f, 0.078022f,
        0.807981f, 0.158022f,
        0.847981f, 0.158022f,
        0.847981f, 0.078022f,
        0.807981f, 0.078022f,
        0.807981f, 0.158022f,
        0.847981f, 0.078022f,
        0.782981f, 0.158022f,
        0.807981f, 0.158022f,
        0.807981f, 0.078022f,
        0.782981f, 0.078022f,
        0.782981f, 0.158022f,
        0.807981f, 0.078022f,
        0.777981f, 0.158022f,
        0.782981f, 0.158022f,
        0.782981f, 0.078022f,
        0.777981f, 0.078022f,
        0.777981f, 0.158022f,
        0.782981f, 0.078022f,
        0.872981f, 0.183022f,
        0.877981f, 0.183022f,
        0.877981f, 0.158022f,
        0.872981f, 0.158022f,
        0.872981f, 0.183022f,
        0.877981f, 0.158022f,
        0.847981f, 0.183022f,
        0.872981f, 0.183022f,
        0.872981f, 0.158022f,
        0.847981f, 0.158022f,
        0.847981f, 0.183022f,
        0.872981f, 0.158022f,
        0.807981f, 0.183022f,
        0.847981f, 0.183022f,
        0.847981f, 0.158022f,
        0.807981f, 0.158022f,
        0.807981f, 0.183022f,
        0.847981f, 0.158022f,
        0.782981f, 0.183022f,
        0.807981f, 0.183022f,
        0.807981f, 0.158022f,
        0.782981f, 0.158022f,
        0.782981f, 0.183022f,
        0.807981f, 0.158022f,
        0.777981f, 0.183022f,
        0.782981f, 0.183022f,
        0.782981f, 0.158022f,
        0.777981f, 0.158022f,
        0.777981f, 0.183022f,
        0.782981f, 0.158022f,
        0.847981f, 0.188022f,
        0.872981f, 0.188022f,
        0.872981f, 0.183022f,
        0.847981f, 0.183022f,
        0.847981f, 0.188022f,
        0.872981f, 0.183022f,
        0.807981f, 0.188022f,
        0.847981f, 0.188022f,
        0.847981f, 0.183022f,
        0.807981f, 0.183022f,
        0.807981f, 0.188022f,
        0.847981f, 0.183022f,
        0.782981f, 0.188022f,
        0.807981f, 0.188022f,
        0.807981f, 0.183022f,
        0.782981f, 0.183022f,
        0.782981f, 0.188022f,
        0.807981f, 0.183022f,
        0.877980f, 0.049788f,
        0.872981f, 0.053022f,
        0.877981f, 0.053022f,
        0.877980f, 0.049788f,
        0.877891f, 0.049424f,
        0.872981f, 0.053022f,
        0.877891f, 0.049424f,
        0.877739f, 0.049081f,
        0.872981f, 0.053022f,
        0.877739f, 0.049081f,
        0.877530f, 0.048771f,
        0.872981f, 0.053022f,
        0.877530f, 0.048771f,
        0.877270f, 0.048501f,
        0.872981f, 0.053022f,
        0.877270f, 0.048501f,
        0.876968f, 0.048282f,
        0.872981f, 0.053022f,
        0.876968f, 0.048282f,
        0.876632f, 0.048118f,
        0.872981f, 0.053022f,
        0.876632f, 0.048118f,
        0.876274f, 0.048016f,
        0.872981f, 0.053022f,
        0.876274f, 0.048016f,
        0.873023f, 0.048022f,
        0.872981f, 0.053022f,
        0.779665f, 0.048019f,
        0.782981f, 0.053022f,
        0.782981f, 0.048022f,
        0.779665f, 0.048019f,
        0.779311f, 0.048118f,
        0.782981f, 0.053022f,
        0.779311f, 0.048118f,
        0.778979f, 0.048277f,
        0.782981f, 0.053022f,
        0.778979f, 0.048277f,
        0.778679f, 0.048492f,
        0.782981f, 0.053022f,
        0.778679f, 0.048492f,
        0.778421f, 0.048756f,
        0.782981f, 0.053022f,
        0.778421f, 0.048756f,
        0.778212f, 0.049062f,
        0.782981f, 0.053022f,
        0.778212f, 0.049062f,
        0.778059f, 0.049399f,
        0.782981f, 0.053022f,
        0.778059f, 0.049399f,
        0.777968f, 0.049758f,
        0.782981f, 0.053022f,
        0.777968f, 0.049758f,
        0.777981f, 0.053022f,
        0.782981f, 0.053022f,
        0.777981f, 0.183022f,
        0.777987f, 0.186248f,
        0.782981f, 0.183022f,
        0.777987f, 0.186248f,
        0.778077f, 0.186613f,
        0.782981f, 0.183022f,
        0.778077f, 0.186613f,
        0.778228f, 0.186955f,
        0.782981f, 0.183022f,
        0.778228f, 0.186955f,
        0.778437f, 0.187266f,
        0.782981f, 0.183022f,
        0.778437f, 0.187266f,
        0.778697f, 0.187535f,
        0.782981f, 0.183022f,
        0.778697f, 0.187535f,
        0.778999f, 0.187755f,
        0.782981f, 0.183022f,
        0.778999f, 0.187755f,
        0.779335f, 0.187918f,
        0.782981f, 0.183022f,
        0.779335f, 0.187918f,
        0.779693f, 0.188021f,
        0.782981f, 0.183022f,
        0.779693f, 0.188021f,
        0.782981f, 0.188022f,
        0.782981f, 0.183022f,
        0.872981f, 0.188022f,
        0.876282f, 0.188038f,
        0.872981f, 0.183022f,
        0.876282f, 0.188038f,
        0.876637f, 0.187939f,
        0.872981f, 0.183022f,
        0.876637f, 0.187939f,
        0.876969f, 0.18778f,
        0.872981f, 0.183022f,
        0.876969f, 0.18778f,
        0.877269f, 0.187565f,
        0.872981f, 0.183022f,
        0.877269f, 0.187565f,
        0.877527f, 0.187301f,
        0.872981f, 0.183022f,
        0.877527f, 0.187301f,
        0.877735f, 0.186996f,
        0.872981f, 0.183022f,
        0.877735f, 0.186996f,
        0.877888f, 0.186658f,
        0.872981f, 0.183022f,
        0.877888f, 0.186658f,
        0.877980f, 0.186299f,
        0.872981f, 0.183022f,
        0.877980f, 0.186299f,
        0.877981f, 0.183022f,
        0.872981f, 0.183022f,
        0.776000f, 0.026293f,
        0.788881f, 0.024943f,
        0.776070f, 0.017872f,
        0.788881f, 0.024943f,
        0.788951f, 0.016523f,
        0.776070f, 0.017872f,
        0.788881f, 0.024943f,
        0.801763f, 0.023494f,
        0.788951f, 0.016523f,
        0.801763f, 0.023494f,
        0.801834f, 0.015076f,
        0.788951f, 0.016523f,
        0.801763f, 0.023494f,
        0.814636f, 0.022136f,
        0.801834f, 0.015076f,
        0.814636f, 0.022136f,
        0.814707f, 0.013725f,
        0.801834f, 0.015076f,
        0.814636f, 0.022136f,
        0.816926f, 0.022609f,
        0.814707f, 0.013725f,
        0.816926f, 0.022609f,
        0.816996f, 0.014209f,
        0.814707f, 0.013725f,
        0.816926f, 0.022609f,
        0.819213f, 0.02308f,
        0.816996f, 0.014209f,
        0.819213f, 0.02308f,
        0.819283f, 0.014691f,
        0.816996f, 0.014209f,
        0.819213f, 0.02308f,
        0.821496f, 0.023549f,
        0.819283f, 0.014691f,
        0.821496f, 0.023549f,
        0.821566f, 0.015173f,
        0.819283f, 0.014691f,
        0.821496f, 0.023549f,
        0.823776f, 0.024014f,
        0.821566f, 0.015173f,
        0.823776f, 0.024014f,
        0.823845f, 0.015654f,
        0.821566f, 0.015173f,
        0.823776f, 0.024014f,
        0.826051f, 0.024476f,
        0.823845f, 0.015654f,
        0.826051f, 0.024476f,
        0.826120f, 0.016135f,
        0.823845f, 0.015654f,
        0.826051f, 0.024476f,
        0.828320f, 0.024935f,
        0.826120f, 0.016135f,
        0.828320f, 0.024935f,
        0.828389f, 0.016615f,
        0.826120f, 0.016135f,
        0.828320f, 0.024935f,
        0.830583f, 0.025391f,
        0.828389f, 0.016615f,
        0.830583f, 0.025391f,
        0.830652f, 0.017096f,
        0.828389f, 0.016615f,
        0.830583f, 0.025391f,
        0.832839f, 0.025845f,
        0.830652f, 0.017096f,
        0.832839f, 0.025845f,
        0.832908f, 0.017576f,
        0.830652f, 0.017096f,
        0.835088f, 0.026295f,
        0.832908f, 0.017576f,
        0.832839f, 0.025845f,
        0.835088f, 0.026295f,
        0.835156f, 0.018056f,
        0.832908f, 0.017576f,
        0.835088f, 0.026295f,
        0.847668f, 0.024915f,
        0.835156f, 0.018056f,
        0.847668f, 0.024915f,
        0.847736f, 0.016715f,
        0.835156f, 0.018056f,
        0.847668f, 0.024915f,
        0.860176f, 0.023391f,
        0.847736f, 0.016715f,
        0.860176f, 0.023391f,
        0.860244f, 0.015248f,
        0.847736f, 0.016715f,
        0.860176f, 0.023391f,
        0.872584f, 0.022008f,
        0.860244f, 0.015248f,
        0.872584f, 0.022008f,
        0.872651f, 0.013937f,
        0.860244f, 0.015248f,
        0.872584f, 0.022008f,
        0.874772f, 0.022417f,
        0.872651f, 0.013937f,
        0.874772f, 0.022417f,
        0.874838f, 0.014429f,
        0.872651f, 0.013937f,
        0.874772f, 0.022417f,
        0.876937f, 0.02282f,
        0.874838f, 0.014429f,
        0.876937f, 0.02282f,
        0.877002f, 0.014917f,
        0.874838f, 0.014429f,
        0.876937f, 0.02282f,
        0.879078f, 0.023216f,
        0.877002f, 0.014917f,
        0.879078f, 0.023216f,
        0.879142f, 0.015402f,
        0.877002f, 0.014917f,
        0.879078f, 0.023216f,
        0.881194f, 0.023605f,
        0.879142f, 0.015402f,
        0.881194f, 0.023605f,
        0.881258f, 0.015884f,
        0.879142f, 0.015402f,
        0.881194f, 0.023605f,
        0.883285f, 0.023987f,
        0.881258f, 0.015884f,
        0.883285f, 0.023987f,
        0.883348f, 0.016362f,
        0.881258f, 0.015884f,
        0.883285f, 0.023987f,
        0.885349f, 0.024363f,
        0.883348f, 0.016362f,
        0.885349f, 0.024363f,
        0.885411f, 0.016836f,
        0.883348f, 0.016362f,
        0.885349f, 0.024363f,
        0.887386f, 0.024732f,
        0.885411f, 0.016836f,
        0.887386f, 0.024732f,
        0.887447f, 0.017307f,
        0.885411f, 0.016836f,
        0.887386f, 0.024732f,
        0.889394f, 0.025094f,
        0.887447f, 0.017307f,
        0.889394f, 0.025094f,
        0.889455f, 0.017776f,
        0.887447f, 0.017307f,
        0.891373f, 0.02545f,
        0.889455f, 0.017776f,
        0.889394f, 0.025094f,
        0.891373f, 0.02545f,
        0.891433f, 0.018242f,
        0.889455f, 0.017776f,
        0.902316f, 0.024234f,
        0.891433f, 0.018242f,
        0.891373f, 0.02545f,
        0.902316f, 0.024234f,
        0.902376f, 0.017144f,
        0.891433f, 0.018242f,
        0.913065f, 0.022978f,
        0.902376f, 0.017144f,
        0.902316f, 0.024234f,
        0.913065f, 0.022978f,
        0.913125f, 0.016025f,
        0.902376f, 0.017144f,
        0.923592f, 0.021866f,
        0.913125f, 0.016025f,
        0.913065f, 0.022978f,
        0.923592f, 0.021866f,
        0.923652f, 0.015067f,
        0.913125f, 0.016025f,
        0.923592f, 0.021866f,
        0.925422f, 0.022192f,
        0.923652f, 0.015067f,
        0.925422f, 0.022192f,
        0.925481f, 0.015553f,
        0.923652f, 0.015067f,
        0.925422f, 0.022192f,
        0.927208f, 0.022518f,
        0.925481f, 0.015553f,
        0.927208f, 0.022518f,
        0.927267f, 0.016034f,
        0.925481f, 0.015553f,
        0.927208f, 0.022518f,
        0.928953f, 0.022843f,
        0.927267f, 0.016034f,
        0.928953f, 0.022843f,
        0.929012f, 0.01651f,
        0.927267f, 0.016034f,
        0.928953f, 0.022843f,
        0.930658f, 0.023168f,
        0.929012f, 0.01651f,
        0.930658f, 0.023168f,
        0.930716f, 0.016981f,
        0.929012f, 0.01651f,
        0.930658f, 0.023168f,
        0.932324f, 0.023493f,
        0.930716f, 0.016981f,
        0.932324f, 0.023493f,
        0.932381f, 0.017447f,
        0.930716f, 0.016981f,
        0.932324f, 0.023493f,
        0.933951f, 0.023816f,
        0.932381f, 0.017447f,
        0.933951f, 0.023816f,
        0.934009f, 0.017907f,
        0.932381f, 0.017447f,
        0.933951f, 0.023816f,
        0.935543f, 0.024138f,
        0.934009f, 0.017907f,
        0.935543f, 0.024138f,
        0.935599f, 0.018362f,
        0.934009f, 0.017907f,
        0.935543f, 0.024138f,
        0.937098f, 0.024459f,
        0.935599f, 0.018362f,
        0.937098f, 0.024459f,
        0.937155f, 0.01881f,
        0.935599f, 0.018362f,
        0.938620f, 0.024778f,
        0.937155f, 0.01881f,
        0.937098f, 0.024459f,
        0.938620f, 0.024778f,
        0.938676f, 0.019252f,
        0.937155f, 0.01881f,
        0.946986f, 0.024206f,
        0.938676f, 0.019252f,
        0.938620f, 0.024778f,
        0.946986f, 0.024206f,
        0.947041f, 0.018788f,
        0.938676f, 0.019252f,
        0.955208f, 0.023583f,
        0.947041f, 0.018788f,
        0.946986f, 0.024206f,
        0.955208f, 0.023583f,
        0.955263f, 0.018247f,
        0.947041f, 0.018788f,
        0.963324f, 0.02309f,
        0.955263f, 0.018247f,
        0.955208f, 0.023583f,
        0.963324f, 0.02309f,
        0.963378f, 0.01781f,
        0.955263f, 0.018247f,
        0.963324f, 0.02309f,
        0.964756f, 0.023441f,
        0.963378f, 0.01781f,
        0.964756f, 0.023441f,
        0.964810f, 0.018202f,
        0.963378f, 0.01781f,
        0.964756f, 0.023441f,
        0.966178f, 0.023794f,
        0.964810f, 0.018202f,
        0.966178f, 0.023794f,
        0.966232f, 0.01859f,
        0.964810f, 0.018202f,
        0.966178f, 0.023794f,
        0.967590f, 0.024148f,
        0.966232f, 0.01859f,
        0.967590f, 0.024148f,
        0.967645f, 0.018975f,
        0.966232f, 0.01859f,
        0.967590f, 0.024148f,
        0.968995f, 0.024503f,
        0.967645f, 0.018975f,
        0.968995f, 0.024503f,
        0.969049f, 0.019357f,
        0.967645f, 0.018975f,
        0.968995f, 0.024503f,
        0.970393f, 0.024859f,
        0.969049f, 0.019357f,
        0.970393f, 0.024859f,
        0.970447f, 0.019735f,
        0.969049f, 0.019357f,
        0.970393f, 0.024859f,
        0.971786f, 0.025217f,
        0.970447f, 0.019735f,
        0.971786f, 0.025217f,
        0.971840f, 0.020109f,
        0.970447f, 0.019735f,
        0.971786f, 0.025217f,
        0.973175f, 0.025575f,
        0.971840f, 0.020109f,
        0.973175f, 0.025575f,
        0.973229f, 0.02048f,
        0.971840f, 0.020109f,
        0.973175f, 0.025575f,
        0.974562f, 0.025934f,
        0.973229f, 0.02048f,
        0.974562f, 0.025934f,
        0.974615f, 0.020846f,
        0.973229f, 0.02048f,
        0.975947f, 0.026293f,
        0.974615f, 0.020846f,
        0.974562f, 0.025934f,
        0.975947f, 0.026293f,
        0.976000f, 0.021207f,
        0.974615f, 0.020846f,
    };
    private int mNumTexCoordinatesB = mTexCoordinatesB.length;
}