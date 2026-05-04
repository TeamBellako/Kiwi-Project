package com.kiwi.combat;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

/**
 * Pulls scripted values for {@link Random#nextInt(int)} and {@link Random#nextFloat()}
 * from FIFO queues. Lets engine tests assert deterministic outcomes for hit/crit/variance rolls.
 */
public class ScriptedRandom extends Random {

    private final Deque<Integer> ints = new ArrayDeque<>();
    private final Deque<Float> floats = new ArrayDeque<>();

    public ScriptedRandom queueInts(int... values) {
        for (int v : values) ints.add(v);
        return this;
    }

    public ScriptedRandom queueFloats(float... values) {
        for (float v : values) floats.add(v);
        return this;
    }

    @Override
    public int nextInt(int bound) {
        if (ints.isEmpty()) {
            throw new IllegalStateException("ScriptedRandom: no more queued ints (bound=" + bound + ")");
        }
        return ints.poll();
    }

    @Override
    public int nextInt() {
        return nextInt(Integer.MAX_VALUE);
    }

    @Override
    public float nextFloat() {
        if (floats.isEmpty()) {
            throw new IllegalStateException("ScriptedRandom: no more queued floats");
        }
        return floats.poll();
    }
}
