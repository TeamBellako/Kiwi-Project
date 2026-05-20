package com.kiwi.goals;

import com.kiwi.features.goals.data.AppUsageGoalTargetCalculator;
import org.junit.Test;

import static com.kiwi.features.goals.data.AppUsageGoalTargetCalculator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies the boundary rules of {@link AppUsageGoalTargetCalculator}.
 *
 * Good apps – target is the minimum daily time the user must spend:
 *   Level 1 = max(baseline, 15 min)
 *   Each extra level adds 15 min, capped at 1 h.
 *
 * Bad apps – target is the maximum daily time the user may spend:
 *   Level 1 = min(baseline, 1 h)
 *   Each extra level subtracts 15 min, floored at 5 min.
 */
public class AppUsageGoalTargetCalculatorTests {

    // ─────────────────────────────────────────────────────────────────────────
    // GOOD APPS
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    public void goodApps_baseline_below_min_gives_min_at_level1() {
        // baseline 5 min < 15 min floor → level 1 should be 15 min
        long result = computeGoodAppTarget(5 * 60_000L, 1);
        assertEquals(GOOD_MIN_MS, result);
    }

    @Test
    public void goodApps_baseline_above_min_gives_baseline_at_level1() {
        // baseline 30 min > 15 min floor → level 1 should be 30 min
        long baseline = 30 * 60_000L;
        long result = computeGoodAppTarget(baseline, 1);
        assertEquals(baseline, result);
    }

    @Test
    public void goodApps_each_level_adds_15_min() {
        // baseline = 15 min (minimum) so level1 = 15 min
        long baseline = GOOD_MIN_MS;
        long level1 = computeGoodAppTarget(baseline, 1);
        long level2 = computeGoodAppTarget(baseline, 2);
        assertEquals(level1 + STEP_MS, level2);
    }

    @Test
    public void goodApps_upper_bound_capped_at_1h() {
        // With a high baseline (50 min) + difficulty 5 the uncapped value
        // would exceed 1 h — must be clamped to 1 h.
        long baseline = 50 * 60_000L;
        long result = computeGoodAppTarget(baseline, 5);
        assertEquals(GOOD_MAX_MS, result);
    }

    @Test
    public void goodApps_level5_with_zero_baseline_stays_within_bounds() {
        // zero baseline → level 1 = 15 min, level 5 = 15 + 4×15 = 75 min → capped at 60 min
        long result = computeGoodAppTarget(0L, 5);
        assertEquals(GOOD_MAX_MS, result);
    }

    @Test
    public void goodApps_result_never_below_good_min() {
        for (int diff = 1; diff <= MAX_DIFFICULTY; diff++) {
            long result = computeGoodAppTarget(0L, diff);
            assert result >= GOOD_MIN_MS
                    : "Good target below minimum at difficulty " + diff;
        }
    }

    @Test
    public void goodApps_result_never_above_good_max() {
        long hugeBaseline = 10 * 60 * 60_000L; // 10 h
        for (int diff = 1; diff <= MAX_DIFFICULTY; diff++) {
            long result = computeGoodAppTarget(hugeBaseline, diff);
            assert result <= GOOD_MAX_MS
                    : "Good target above maximum at difficulty " + diff;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BAD APPS
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    public void badApps_baseline_above_max_gives_max_at_level1() {
        // baseline 3 h > 1 h ceiling → level 1 should be 1 h
        long result = computeBadAppTarget(3 * 60 * 60_000L, 1);
        assertEquals(BAD_MAX_MS, result);
    }

    @Test
    public void badApps_baseline_below_max_gives_baseline_at_level1() {
        // baseline 30 min < 1 h ceiling → level 1 should be 30 min
        long baseline = 30 * 60_000L;
        long result = computeBadAppTarget(baseline, 1);
        assertEquals(baseline, result);
    }

    @Test
    public void badApps_each_level_subtracts_15_min() {
        // baseline = 1 h (ceiling) so level1 = 60 min
        long baseline = BAD_MAX_MS;
        long level1 = computeBadAppTarget(baseline, 1);
        long level2 = computeBadAppTarget(baseline, 2);
        assertEquals(level1 - STEP_MS, level2);
    }

    @Test
    public void badApps_lower_bound_floored_at_5_min() {
        // With baseline = 5 min (already at floor), difficulty 5 must not go below 5 min.
        long result = computeBadAppTarget(BAD_MIN_MS, 5);
        assertEquals(BAD_MIN_MS, result);
    }

    @Test
    public void badApps_level5_with_high_baseline_stays_within_bounds() {
        // baseline = 1 h, level 5: 60 - 4×15 = 0 min → clamped to 5 min
        long result = computeBadAppTarget(BAD_MAX_MS, 5);
        assertEquals(BAD_MIN_MS, result);
    }

    @Test
    public void badApps_result_never_above_bad_max() {
        for (int diff = 1; diff <= MAX_DIFFICULTY; diff++) {
            long result = computeBadAppTarget(10 * 60 * 60_000L, diff);
            assert result <= BAD_MAX_MS
                    : "Bad target above maximum at difficulty " + diff;
        }
    }

    @Test
    public void badApps_result_never_below_bad_min() {
        for (int diff = 1; diff <= MAX_DIFFICULTY; diff++) {
            long result = computeBadAppTarget(0L, diff);
            assert result >= BAD_MIN_MS
                    : "Bad target below minimum at difficulty " + diff;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DIFFICULTY PROGRESSION – full ladder
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    public void goodApps_full_ladder_with_zero_baseline() {
        // baseline 0 → level1 = 15 min; each step +15 min until cap
        assertEquals(15 * 60_000L, computeGoodAppTarget(0L, 1));
        assertEquals(30 * 60_000L, computeGoodAppTarget(0L, 2));
        assertEquals(45 * 60_000L, computeGoodAppTarget(0L, 3));
        assertEquals(60 * 60_000L, computeGoodAppTarget(0L, 4)); // cap
        assertEquals(60 * 60_000L, computeGoodAppTarget(0L, 5)); // still capped
    }

    @Test
    public void badApps_full_ladder_with_1h_baseline() {
        // baseline 1 h (ceiling) → level1 = 60 min; each step -15 min until floor
        assertEquals(60 * 60_000L, computeBadAppTarget(BAD_MAX_MS, 1));
        assertEquals(45 * 60_000L, computeBadAppTarget(BAD_MAX_MS, 2));
        assertEquals(30 * 60_000L, computeBadAppTarget(BAD_MAX_MS, 3));
        assertEquals(15 * 60_000L, computeBadAppTarget(BAD_MAX_MS, 4));
        assertEquals(5  * 60_000L, computeBadAppTarget(BAD_MAX_MS, 5)); // floor
    }
}
