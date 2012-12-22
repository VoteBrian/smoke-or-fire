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

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.app.Application;

public class Global extends Application {

    public static final int SMOKE  = 0;
    public static final int FIRE   = 1;
    public static final int HIGHER = 2;
    public static final int LOWER  = 3;
    public static final int PASS   = 4;

    public static final int HEARTS   = 0;
    public static final int DIAMONDS = 1;
    public static final int CLUBS    = 2;
    public static final int SPADES   = 3;

    public static final int BAD    = 0;
    public static final int GOOD   = 1;
    public static final int SOCIAL = 2;

    public static final int IN_DECK  = 0;
    public static final int ON_TABLE = 1;
    public static final int BURNT    = 3;

    public float[] SETTLE = {0.2f, 0.7f, 0.9f, 0.05f};
    public float[] HIGHLIGHT = {0.2f, 0.7f, 0.9f, 0.5f};
    public float[] WHITE = {1.0f, 1.0f, 1.0f, 0.5f};
    public float[] OFF = {0.0f, 0.0f, 0.0f, 0.0f};
    public float[] BRIGHT = {0.2f, 0.7f, 0.9f, 1.0f};
    public float[] BLUE = {0.2f, 0.7f, 0.9f, 0.5f};
    public float[] RED = {0.9f, 0.2f, 0.2f, 0.5f};
    public float[] BLACK = {0.0f, 0.0f, 0.1f, 0.5f};

    public Global() {
        //mDeck = new Deck(this);
    }
}