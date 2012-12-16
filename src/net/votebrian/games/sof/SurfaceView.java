package net.votebrian.games.sof;

import android.util.AttributeSet;
import android.content.Context;
import android.view.MotionEvent;
import android.opengl.GLSurfaceView;

public class SurfaceView extends GLSurfaceView {

    int EVENT_DOWN = 0;
    int EVENT_MOVE = 1;
    int EVENT_UP   = 2;

    private GLESRenderer renderer;

    float mStartX = 0;
    float mStartY = 0;

    public SurfaceView(Context context, AttributeSet atr) {
        super(context, atr);
        renderer = new GLESRenderer(context);

        setRenderer(renderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                renderer.buttonEvent(x, y, EVENT_DOWN);
                break;

            case MotionEvent.ACTION_UP:
                renderer.buttonEvent(x, y, EVENT_UP);
                break;

            case MotionEvent.ACTION_MOVE:
                renderer.buttonEvent(x,y, EVENT_MOVE);
                break;
        }

        return true;
    }
}