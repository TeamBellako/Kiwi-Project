package com.kiwi.features.personality.data;

import java.util.List;
import java.util.Optional;

public enum BuildType {

    BERSERKER(
            new BuildStats(100, 10, 5, 5, 3, 8, 6, 4),
            List.of(/* skill IDs */),
            List.of(/* new ElementMultiplierConfig(elementId, multiplier) */),
            List.of(/* new StatusResistanceConfig(stateId, resistance) */)
    ),
    SHAMAN(
            new BuildStats(100, 5, 10, 3, 5, 6, 6, 8),
            List.of(/* skill IDs */),
            List.of(/* new ElementMultiplierConfig(elementId, multiplier) */),
            List.of(/* new StatusResistanceConfig(stateId, resistance) */)
    ),
    MONK(
            new BuildStats(100, 7, 7, 7, 7, 7, 7, 4),
            List.of(/* skill IDs */),
            List.of(/* new ElementMultiplierConfig(elementId, multiplier) */),
            List.of(/* new StatusResistanceConfig(stateId, resistance) */)
    );

    private final BuildStats stats;
    private final List<Long> skillIds;
    private final List<ElementMultiplierConfig> elementMultipliers;
    private final List<StatusResistanceConfig> statusResistances;

    BuildType(
            BuildStats stats,
            List<Long> skillIds,
            List<ElementMultiplierConfig> elementMultipliers,
            List<StatusResistanceConfig> statusResistances
    ) {
        this.stats = stats;
        this.skillIds = skillIds;
        this.elementMultipliers = elementMultipliers;
        this.statusResistances = statusResistances;
    }

    public BuildStats getStats() { return stats; }
    public List<Long> getSkillIds() { return skillIds; }
    public List<ElementMultiplierConfig> getElementMultipliers() { return elementMultipliers; }
    public List<StatusResistanceConfig> getStatusResistances() { return statusResistances; }

    public static Optional<BuildType> fromString(String value) {
        if (value == null) return Optional.empty();
        try {
            return Optional.of(BuildType.valueOf(value.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    // -------------------------------------------------------------------------

    public record BuildStats(
            int maxHp,
            int patk, int matk,
            int pdef, int mdef,
            int acc, int eva,
            int lck
    ) {}

    public record ElementMultiplierConfig(long elementId, float multiplier) {}

    public record StatusResistanceConfig(long stateId, float resistance) {}
}
