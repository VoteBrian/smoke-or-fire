/*
 *  Copyright 2012 Brian Flores
 *
 *  This file is part of SmokeOrFire.
 *
 *  SmokeOrFire is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SmokeOrFire is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

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