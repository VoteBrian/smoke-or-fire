package net.votebrian.games.sof;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

public class Buttons {
    private Model mBtnHigher;
    private Model mBtnLower;
    private Model mBtnSmoke;
    private Model mBtnFire;

    private float mBtnBaseZ = 8f;
    private float[] mBtn;

    public Buttons() {
        mBtnHigher = new Model();
        mBtnLower = new Model();
        mBtnSmoke = new Model();
        mBtnFire = new Model();

        mBtnHigher.setColor(1.0f, 1.0f, 1.0f, 0.1f);
        mBtnLower.setColor(1.0f, 1.0f, 1.0f, 0.1f);
        mBtnSmoke.setColor(1.0f, 1.0f, 1.0f, 0.1f);
        mBtnFire.setColor(1.0f, 1.0f, 1.0f, 0.1f);
    }

    public void draw(GL10 gl) {
        mBtnHigher.draw(gl);
        mBtnLower.draw(gl);
        mBtnSmoke.draw(gl);
        mBtnFire.draw(gl);
    }

    public void setVertices(float viewW, float viewH, float viewAngle) {
        float ratio = viewW / viewH;

        float h = (float) (mBtnBaseZ * (Math.tan(Math.toRadians(viewAngle))));
        float w = h * ratio;

        mBtn = new float[9];

        // HIGHER
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
    }
}