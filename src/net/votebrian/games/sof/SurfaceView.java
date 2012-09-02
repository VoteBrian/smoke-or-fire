package net.votebrian.games.sof;

import android.util.AttributeSet;
import android.content.Context;
import android.view.MotionEvent;
import android.opengl.GLSurfaceView;

public class SurfaceView extends GLSurfaceView {

    Global gbl;

    float mStartX = 0;
    float mStartY = 0;

    public SurfaceView(Context context, AttributeSet atr) {
        super(context, atr);

        setRenderer(new GLESRenderer(context));

        gbl = (Global) context.getApplicationContext();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                // mStartX = x;
                // mStartY = y;
                break;
            case MotionEvent.ACTION_UP:
                /*
                int curr = gbl.getSomething();

                if(curr == 0) {
                    gbl.setSomething(1);
                } else if(curr == 1) {
                    gbl.setSomething(2);
                } else if(curr == 2) {
                    gbl.setSomething(0);
                }

                mStartX = 0;
                mStartY = 0;
                */
                gbl.deal();
                break;
            case MotionEvent.ACTION_MOVE:
                /*
                gbl.setXAngle(x-mStartX);
                gbl.setYAngle(mStartY-y);

                mStartX = x;
                mStartY = y;
                */
                break;
        }

        return true;
    }
}