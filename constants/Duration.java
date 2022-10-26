package com.ltl.mpmp_lab3.constants;

public enum Duration {
//    public final static Long ANIMATION_DURATION_MILLIS = 1000L;
//    public final static Long GAME_DURATION_MILLIS = 60000L;

    ANIMATION_MILLIS(1000L),
    GAME_MILLIS(60000L);

    private final long duration;
    Duration(long duration){
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }
}
