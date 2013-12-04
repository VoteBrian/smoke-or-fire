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
 *  along with SmokeOrFire.  If not, see <http://www.gnu.org/licenses/>.
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

    public static final int AD_FREE_THRESHOLD = 26;

    public float[] HIGHLIGHT = {0.2f, 0.7f, 0.9f, 0.5f};
    public float[] BRIGHT = {0.2f, 0.7f, 0.9f, 1.0f};
    public float[] RED_FULL   = {0.4f, 0.0f, 0.0f, 0.7f};
    public float[] RED_LITE   = {0.4f, 0.0f, 0.0f, 0.2f};
    public float[] BLACK_FULL = {0.2f, 0.2f, 0.2f, 0.7f};
    public float[] BLACK_LITE = {0.2f, 0.2f, 0.2f, 0.2f};
    public float[] WHITE_FULL = {0.8f, 0.8f, 0.8f, 0.7f};
    public float[] WHITE_LITE = {0.8f, 0.8f, 0.8f, 0.2f};

    private static final String mPub = "";

    public static final String SKU_NO_ADS = "sof_no_ads";

    private static boolean mReady = false;

    public Global() {
        // do nothing
    }

    public static String getPubKey() {
        return  mPub;
    }

    public static void setReady() {
        mReady = true;
    }

    public static boolean getReady() {
        return mReady;
    }
}