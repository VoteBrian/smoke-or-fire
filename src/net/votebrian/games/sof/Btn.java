package net.votebrian.games.sof;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;

public class Btn extends Model {
    private Global gbl;
    private float mRotOffset = 0;
    private float mXOffset;
    private float mYOffset;
    private float mZOffset;
    private float[] mRotAxes = {0f, 0f, 0f};

    public Btn(Context context, GL10 gl) {
        super(context, gl);

        gbl = (Global) context.getApplicationContext();
    }

    public void draw(GL10 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(mXOffset, mYOffset, mZOffset);
        gl.glRotatef(mRotOffset, mRotAxes[0], mRotAxes[1], mRotAxes[2]);
        gl.glTranslatef(-mXOffset, -mYOffset, -mZOffset);
        super.draw(gl);
        gl.glPopMatrix();
    }

    public void setOffsets(float x, float y, float z) {
        mXOffset = x;
        mYOffset = y;
        mZOffset = z;
    }

    public void setRotAxes(float[] axes){
        mRotAxes = axes;
    }

    public void setRotOffset(float offset){
        mRotOffset = offset;
    }
}