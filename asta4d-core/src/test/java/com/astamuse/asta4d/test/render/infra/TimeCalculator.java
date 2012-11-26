package com.astamuse.asta4d.test.render.infra;

public class TimeCalculator {

    public final static void shouldRunInTime(Runnable run, long time) {
        long begin = System.currentTimeMillis();
        run.run();
        long end = System.currentTimeMillis();
        long period = end - begin;
        assert period < time : "Execution is expected less than " + time + "ms but it takes " + period + "ms";
    }

}
