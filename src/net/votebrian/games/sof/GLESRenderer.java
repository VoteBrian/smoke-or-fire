package net.votebrian.games.sof;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;

import android.util.Log;

class GLESRenderer implements GLSurfaceView.Renderer {
    Global gbl;
    Context mCtx;

    private Model mBtnHigher;
    private Model mBtnLower;
    private Model mBtnSmoke;
    private Model mBtnFire;

    private ByteBuffer mLbb;
    private FloatBuffer mLinesBuffer;
    private int mNumLines = 0;
    private int POINTS_PER_LINE = 3;
    private int BYTES_PER_POINT = 4;
    private float[] mLineVert = new float[24];

    private int mViewW = 0;
    private int mViewH = 0;
    private float mViewAngle = 10f;

    private float mNearH = 0f;
    private float mNearW = 0f;
    private float mNearZ = 5f;
    private float mFarZ  = 15f;

    private FloatBuffer mPositionBuffer;
    private FloatBuffer mDiffuseBuffer;

    private float mXAngle = 0;
    private float mYAngle = 0;

    private float mBtnBaseZ = 8f;
    private float[] mBtn;

    public final int SS_SUNLIGHT = GL10.GL_LIGHT0;

    public GLESRenderer(Context context) {
        mCtx = context;
        gbl = (Global) context.getApplicationContext();

        mBtnHigher = new Model();
        mBtnHigher.setColor(1.0f, 1.0f, 1.0f, 0.1f);

        mBtnLower = new Model();
        mBtnLower.setColor(1.0f, 1.0f, 1.0f, 0.1f);

        mBtnSmoke = new Model();
        mBtnSmoke.setColor(1.0f, 1.0f, 1.0f, 0.1f);

        mBtnFire = new Model();
        mBtnFire.setColor(1.0f, 1.0f, 1.0f, 0.1f);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        setDisplayProperties(gl);
        initLighting(gl);
        gbl.createDeck(gl);
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glLoadIdentity();

        // rotate card table
        gl.glRotatef(-20f, 1f, 0f, 0f);
        gl.glRotatef(5, 0f, 1f, 0f);
        gl.glRotatef( -10f, 0f, 0f, 1f);
        gl.glTranslatef(1f, 3f, 0f);

        gbl.draw(gl);


        // undo rotate for buttons/overlays
        gl.glTranslatef(-1f, -3f, 0f);
        gl.glRotatef(10f, 0f, 0f, 1f);
        gl.glRotatef(-5f, 0f, 1f, 0f);
        gl.glRotatef(20f, 1f, 0f, 0f);

        mBtnHigher.draw(gl);
        mBtnLower.draw(gl);
        mBtnSmoke.draw(gl);
        mBtnFire.draw(gl);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mViewW = width;
        mViewH = height;

        setDisplayProperties(gl);
        setProjection(gl);

        setBtnVertices();
    }

    private void setProjection(GL10 gl) {
        float ratio = (float) mViewW / (float) mViewH;

        // determine the "half-width" and "half-height" of our view at the near cutoff Z value stuff
        // stuff stuff
        mNearH = (float) (mNearZ * (Math.tan(Math.toRadians(mViewAngle))));
        mNearW = mNearH * ratio;

        // Define orthographic projection
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glFrustumf(-mNearW, mNearW, -mNearH, mNearH, mNearZ, mFarZ);
        gl.glViewport(0, 0, mViewW, mViewH);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
    }

    private void setDisplayProperties(GL10 gl) {
        // Set background color
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // Set to remove CW triangles
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glFrontFace(GL10.GL_CCW);
        gl.glCullFace(GL10.GL_BACK);

        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);

        // set blend parameter
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        gl.glDisable(GL10.GL_COLOR_MATERIAL);
        // gl.glEnable(GL10.GL_COLOR_MATERIAL);
    }

    private void initLighting(GL10 gl) {
        float[] diffuse = { 1.0f, 1.0f, 1.0f, 1.0f };
        ByteBuffer mDbb = ByteBuffer.allocateDirect( diffuse.length * 4 );
        mDbb.order(ByteOrder.nativeOrder());
        mDiffuseBuffer = mDbb.asFloatBuffer();
        mDiffuseBuffer.put(diffuse);
        mDiffuseBuffer.position(0);

        float[] pos = { 0.0f, 0.0f, 0.0f, 1.0f };
        ByteBuffer mPbb = ByteBuffer.allocateDirect( pos.length * 4 );
        mPbb.order(ByteOrder.nativeOrder());
        mPositionBuffer = mPbb.asFloatBuffer();
        mPositionBuffer.put(pos);
        mPositionBuffer.position(0);

        gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, mPositionBuffer);
        gl.glLightfv(SS_SUNLIGHT, GL10.GL_DIFFUSE, mDiffuseBuffer);
        gl.glShadeModel(GL10.GL_SMOOTH);
        // gl.glEnable(GL10.GL_LIGHTING);
        gl.glDisable(GL10.GL_LIGHTING);
        gl.glEnable(SS_SUNLIGHT);
    }

    private void getRotation() {
        mXAngle = gbl.getXAngle();
        mYAngle = gbl.getYAngle();
    }

    private void setBtnVertices() {
        float ratio = (float) mViewW / (float) mViewH;

        float h = (float) (mBtnBaseZ * (Math.tan(Math.toRadians(mViewAngle))));
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