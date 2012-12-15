package net.votebrian.games.sof;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.util.Log;

public class Buttons {
    private Context mCtx;

    private Model mBtnHigher;
    private Model mBtnLower;
    private Model mBtnSmoke;
    private Model mBtnFire;

    private float mBtnBaseZ = 5.1f;
    private float[] mBtn;

    private float[] mSettle = {1.0f, 1.0f, 1.0f, 0.05f};
    private float[] mHighlight = {0.2f, 0.7f, 0.9f, 0.3f};
    private float[] mOff = {0.0f, 0.0f, 0.0f, 0.0f};
    private float[] mBlue = {0.2f, 0.7f, 0.9f, 0.5f};

    public Buttons(Context context, GL10 gl) {
        mCtx = context;


        mBtnHigher = new Model(mCtx, gl);
        mBtnHigher.setModelColor(mSettle);

        mBtnLower = new Model(mCtx, gl);
        mBtnLower.setModelColor(mSettle);

        mBtnSmoke = new Model(mCtx, gl);
        mBtnSmoke.setModelColor(mSettle);

        mBtnFire = new Model(mCtx, gl);
        mBtnFire.setModelColor(mSettle);

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

        mBtnHigher.setModelColor(mSettle);
        mBtnLower.setModelColor(mSettle);
        mBtnSmoke.setModelColor(mSettle);
        mBtnFire.setModelColor(mSettle);

        switch (btn) {
            case Global.SMOKE:
                mBtnSmoke.setModelColor(mHighlight);
                break;
            case Global.FIRE:
                mBtnFire.setModelColor(mHighlight);
                break;
            case Global.HIGHER:
                mBtnHigher.setModelColor(mHighlight);
                break;
            case Global.LOWER:
                mBtnLower.setModelColor(mHighlight);
                break;
            default:
                break;
        }
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
        mBtnHigher.setModelColor(mSettle);
        mBtnHigher.setOutlineColor(mBlue);

        mBtnLower.enableModel();
        mBtnLower.enableOutline();
        mBtnLower.setModelColor(mSettle);
        mBtnLower.setOutlineColor(mBlue);

        mBtnSmoke.enableModel();
        mBtnSmoke.enableOutline();
        mBtnSmoke.setModelColor(mSettle);
        mBtnSmoke.setOutlineColor(mBlue);

        mBtnFire.enableModel();
        mBtnFire.enableOutline();
        mBtnFire.setModelColor(mSettle);
        mBtnFire.setOutlineColor(mBlue);
    }

    public void enableRelative() {
        mBtnHigher.enableModel();
        mBtnHigher.enableOutline();
        mBtnHigher.setModelColor(mSettle);
        mBtnHigher.setOutlineColor(mBlue);

        mBtnLower.enableModel();
        mBtnLower.enableOutline();
        mBtnLower.setModelColor(mSettle);
        mBtnLower.setOutlineColor(mBlue);
    }

    public void enableAbsolute() {
        mBtnSmoke.enableModel();
        mBtnSmoke.enableOutline();
        mBtnSmoke.setModelColor(mSettle);
        mBtnSmoke.setOutlineColor(mBlue);

        mBtnFire.enableModel();
        mBtnFire.enableOutline();
        mBtnFire.setModelColor(mSettle);
        mBtnFire.setOutlineColor(mBlue);
    }


    public void settle() {

        mBtnHigher.setModelColor(mSettle);
        mBtnFire.setModelColor(mSettle);
        mBtnLower.setModelColor(mSettle);
        mBtnSmoke.setModelColor(mSettle);
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



        mBtnHigher.setOutlineIndices(new int[] {0, 1, 1, 2});
        mBtnLower.setOutlineIndices(new int[] {0, 2, 1, 2});
        mBtnSmoke.setOutlineIndices(new int[] {0, 2, 1, 2});
        mBtnFire.setOutlineIndices(new int[] {0, 1, 2, 1});
    }
}