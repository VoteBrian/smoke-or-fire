package net.votebrian.games.sof;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;

import android.os.Handler;

import android.util.Log;

class GLESRenderer implements GLSurfaceView.Renderer {
    Global gbl;
    Context mCtx;

    private Handler handler;

    private Buttons mOverlayBtns;
    private Btn btn;

    private int EVENT_DOWN = 0;
    private int EVENT_MOVE = 1;
    private int EVENT_UP   = 2;

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

    public final int SS_SUNLIGHT = GL10.GL_LIGHT0;

    private boolean relBtnsEnabled = false;

    private int mSelectionFail = 0;
    // 0:  Ready for selection
    // 1:  Selection Failed. Deal disabled until cards cleared.
    // 2:  Cards cleared.  Reset counter

    public GLESRenderer(Context context) {
        mCtx = context;
        gbl = (Global) context.getApplicationContext();
        handler = new Handler();

        mOverlayBtns = new Buttons();
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

        mOverlayBtns.draw(gl);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mViewW = width;
        mViewH = height;

        setDisplayProperties(gl);
        setProjection(gl);

        mOverlayBtns.setVertices(mViewW, mViewH, mViewAngle);
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
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glDisable(GL10.GL_LIGHTING);
        gl.glEnable(SS_SUNLIGHT);
    }

    /*
    private void getRotation() {
        mXAngle = gbl.getXAngle();
        mYAngle = gbl.getYAngle();
    }
    */

    public void buttonEvent(float x, float y, int event) {
        // determine region of button event
        int region = regionCalc(x, y);

        if(mSelectionFail == 1) {
            Log.v("RENDERER", "mSelectionFail: " + mSelectionFail);

            // getting rid of Clearable, but burnTable won't work without it right now
            gbl.setClearable(1);
            gbl.burnTable();
            gbl.setClearable(0);

            mSelectionFail = 2;

            handler.postDelayed( new Runnable() {
                    public void run() {
                        Log.v("RENDERER", "Runnable");
                        mSelectionFail = 0;
                    }
                }, 500);

        } else if(mSelectionFail == 0) {
            if( event == EVENT_DOWN ) {
                // highlight button pressed
                if( (region < 2)  || relBtnsEnabled ) {
                    mOverlayBtns.highlightBtn(region);
                }
            } else if( event == EVENT_UP ) {
                // remove highlight
                if( (region < 2)  || relBtnsEnabled ) {
                    // remove highlight after a slight delay
                    handler.postDelayed( new Runnable() {
                        public void run() {
                            mOverlayBtns.settle();
                        }
                    }, 50);

                    // deal card and determine outcome
                    int result = gbl.deal(region);
                    Log.v("RENDERER", "result: " + result);

                    switch(result) {
                        case Global.BAD:
                            // expand drink counter
                            // show fail splash image

                            // show cards on table

                            // disable all selections until cleared
                            mSelectionFail = 1;

                            // disable higher/lower selections
                            relBtnsEnabled = false;
                            break;
                        case Global.GOOD:
                            // highlight drink counter increment

                            // enable higher/lower selections
                            relBtnsEnabled = true;
                            break;
                        case Global.SOCIAL:
                            // highlight drink counter increment
                            // show social splash image
                            // show matching cards
                            //stuff
                            break;
                    }
                }
            }
        }
    }

    private int regionCalc(float x, float y) {
        float slope = (float) mViewH / (float) mViewW;
        float upslope = -1 * slope * (float) x + mViewH;
        float downslope = slope * x;

        if( y > upslope ) {
            if( y > downslope ) {
                return Global.LOWER;
            } else {
                return Global.FIRE;
            }
        } else {
            if( y > downslope ) {
                return Global.SMOKE;
            } else {
                return Global.HIGHER;
            }
        }
    }
}