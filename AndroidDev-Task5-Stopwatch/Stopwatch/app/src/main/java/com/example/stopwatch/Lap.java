package com.example.stopwatch;

/** Simple immutable model for one recorded lap. */
public class Lap {
    public final int number;    // 1-based lap index
    public final long splitMs;  // time since the previous lap (or since start)
    public final long totalMs;  // total elapsed time when the lap was recorded

    public Lap(int number, long splitMs, long totalMs) {
        this.number = number;
        this.splitMs = splitMs;
        this.totalMs = totalMs;
    }
}