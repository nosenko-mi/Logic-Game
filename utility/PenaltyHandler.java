package com.ltl.mpmp_lab3.utility;

import android.view.MenuItem;

import com.ltl.mpmp_lab3.R;

public class PenaltyHandler {

    public static final int PENALTY_EASY = 0;
    public static final int PENALTY_NORMAL = 1;
    public static final int PENALTY_HARD = 5;

    private PenaltyHandler(){}

    public static int getPenalty(MenuItem item){
        switch (item.getItemId()){
            case R.id.game_easy_settings:
//                penalty = 0;
                return PENALTY_EASY;

            case R.id.game_normal_settings:
//                penalty = 1;
                return PENALTY_NORMAL;

            case R.id.game_hard_settings:
                return PENALTY_HARD;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return PENALTY_NORMAL;
        }
    }
}
