package com.kiwi.features.goals.data;

/**
 * Computes per-user, per-difficulty targets for AppUsage goals.
 *
 * Good apps: goal is to spend AT LEAST this many ms on good apps.
 *   Level 1 = max(baseline, 15 min)
 *   Each subsequent level adds 15 min, capped at 1 h.
 *
 * Bad apps: goal is to spend AT MOST this many ms on bad apps.
 *   Level 1 = min(baseline, 1 h)
 *   Each subsequent level subtracts 15 min, floored at 5 min.
 */
public final class AppUsageGoalTargetCalculator {

    public static final long STEP_MS     = 15L * 60 * 1_000;  // 15 min in ms
    public static final long GOOD_MIN_MS = 15L * 60 * 1_000;  // 15 min
    public static final long GOOD_MAX_MS = 60L * 60 * 1_000;  // 1 h
    public static final long BAD_MAX_MS  = 60L * 60 * 1_000;  // 1 h
    public static final long BAD_MIN_MS  =  5L * 60 * 1_000;  // 5 min
    public static final int  MAX_DIFFICULTY = 5;

    private AppUsageGoalTargetCalculator() {}

    /**
     * @param baselineMs average daily ms the user spent on good apps (last 7 days)
     * @param difficulty 1..MAX_DIFFICULTY
     * @return target ms clamped to [GOOD_MIN_MS, GOOD_MAX_MS]
     */
    public static long computeGoodAppTarget(long baselineMs, int difficulty) {
        long level1 = Math.max(baselineMs, GOOD_MIN_MS);
        long target = level1 + (long) (difficulty - 1) * STEP_MS;
        return Math.min(target, GOOD_MAX_MS);
    }

    /**
     * @param baselineMs average daily ms the user spent on bad apps (last 7 days)
     * @param difficulty 1..MAX_DIFFICULTY
     * @return target ms clamped to [BAD_MIN_MS, BAD_MAX_MS]
     */
    public static long computeBadAppTarget(long baselineMs, int difficulty) {
        long level1 = Math.min(baselineMs, BAD_MAX_MS);
        long target = level1 - (long) (difficulty - 1) * STEP_MS;
        return Math.max(target, BAD_MIN_MS);
    }
}
