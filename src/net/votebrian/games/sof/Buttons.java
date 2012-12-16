package net.votebrian.games.sof;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.util.Log;

public class Buttons {
    private Context mCtx;
    private Global  gbl;

    private Model mBtnHigher;
    private Model mBtnLower;
    private Model mBtnSmoke;
    private Model mBtnFire;

    private float mBtnBaseZ = 5.2f;
    private float[] mBtn;

    public Buttons(Context context, GL10 gl) {
        mCtx = context;
        gbl = (Global) mCtx.getApplicationContext();

        mBtnHigher = new Model(mCtx, gl);
        mBtnLower = new Model(mCtx, gl);
        mBtnSmoke = new Model(mCtx, gl);
        mBtnFire = new Model(mCtx, gl);

        mBtnHigher.setModelColor(gbl.SETTLE);
        mBtnLower.setModelColor(gbl.SETTLE);
        mBtnSmoke.setModelColor(gbl.SETTLE);
        mBtnFire.setModelColor(gbl.SETTLE);

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

        mBtnHigher.setModelColor(gbl.SETTLE);
        mBtnLower.setModelColor(gbl.SETTLE);
        mBtnSmoke.setModelColor(gbl.SETTLE);
        mBtnFire.setModelColor(gbl.SETTLE);

        switch (btn) {
            case Global.SMOKE:
                mBtnSmoke.setModelColor(gbl.HIGHLIGHT);
                break;
            case Global.FIRE:
                mBtnFire.setModelColor(gbl.HIGHLIGHT);
                break;
            case Global.HIGHER:
                mBtnHigher.setModelColor(gbl.HIGHLIGHT);
                break;
            case Global.LOWER:
                mBtnLower.setModelColor(gbl.HIGHLIGHT);
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
        mBtnHigher.setModelColor(gbl.SETTLE);
        mBtnHigher.setOutlineColor(gbl.BLUE);

        mBtnLower.enableModel();
        mBtnLower.enableOutline();
        mBtnLower.setModelColor(gbl.SETTLE);
        mBtnLower.setOutlineColor(gbl.BLUE);

        mBtnSmoke.enableModel();
        mBtnSmoke.enableOutline();
        mBtnSmoke.setModelColor(gbl.SETTLE);
        mBtnSmoke.setOutlineColor(gbl.BLUE);

        mBtnFire.enableModel();
        mBtnFire.enableOutline();
        mBtnFire.setModelColor(gbl.SETTLE);
        mBtnFire.setOutlineColor(gbl.BLUE);
    }

    public void enableRelative() {
        mBtnHigher.enableModel();
        mBtnHigher.enableOutline();
        mBtnHigher.setModelColor(gbl.SETTLE);
        mBtnHigher.setOutlineColor(gbl.BLUE);

        mBtnLower.enableModel();
        mBtnLower.enableOutline();
        mBtnLower.setModelColor(gbl.SETTLE);
        mBtnLower.setOutlineColor(gbl.BLUE);
    }

    public void enableAbsolute() {
        mBtnSmoke.enableModel();
        mBtnSmoke.enableOutline();
        mBtnSmoke.setModelColor(gbl.SETTLE);
        mBtnSmoke.setOutlineColor(gbl.BLUE);

        mBtnFire.enableModel();
        mBtnFire.enableOutline();
        mBtnFire.setModelColor(gbl.SETTLE);
        mBtnFire.setOutlineColor(gbl.BLUE);
    }


    public void settle() {

        mBtnHigher.setModelColor(gbl.SETTLE);
        mBtnFire.setModelColor(gbl.SETTLE);
        mBtnLower.setModelColor(gbl.SETTLE);
        mBtnSmoke.setModelColor(gbl.SETTLE);
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