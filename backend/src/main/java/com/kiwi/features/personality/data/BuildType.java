package com.kiwi.features.personality.data;

import java.util.List;
import java.util.Optional;

public enum BuildType {

    // Element IDs: 1=Adaptability, 2=Control, 3=Empathy, 4=Focus, 5=Motivation, 6=Resilience
    BERSERKER(
            new BuildStats(140, 18, 6, 15, 8, 10, 6, 8),
            List.of(1000L, 1001L, 1002L, 1003L /*, 3000L, 1070L*/), // TODO: Uncomment once all skills are place in the DB
            List.of(
                    new ElementMultiplierConfig(1, 1.0f),  // Adaptability
                    new ElementMultiplierConfig(2, 1.0f),  // Control
                    new ElementMultiplierConfig(3, 1.0f),  // Empathy
                    new ElementMultiplierConfig(4, 1.5f),  // Focus
                    new ElementMultiplierConfig(5, 1.0f),  // Motivation
                    new ElementMultiplierConfig(6, 0.5f)   // Resilience
            ),
            List.of(/* new StatusResistanceConfig(stateId, resistance) */)
    ),
    SHAMAN(
            new BuildStats(95, 7, 18, 7, 14, 17, 15, 16),
            List.of(1000L, 1001L, 1002L, 1003L /*, 5001L, 2000L, 5000L, 2080L*/), // TODO: Uncomment once all skills are place in the DB
            List.of(
                    new ElementMultiplierConfig(1, 0.5f),  // Adaptability
                    new ElementMultiplierConfig(2, 1.5f),  // Control
                    new ElementMultiplierConfig(3, 1.0f),  // Empathy
                    new ElementMultiplierConfig(4, 1.0f),  // Focus
                    new ElementMultiplierConfig(5, 1.0f),  // Motivation
                    new ElementMultiplierConfig(6, 1.0f)   // Resilience
            ),
            List.of(/* new StatusResistanceConfig(stateId, resistance) */)
    ),
    MONK(
            new BuildStats(115, 8, 15, 12, 18, 13, 10, 14),
            List.of(1000L, 1001L, 1002L, 1003L /*, 4001L, 4000L, 6000L, 4030L*/), // TODO: Uncomment once all skills are place in the DB
            List.of(
                    new ElementMultiplierConfig(1, 1.5f),  // Adaptability
                    new ElementMultiplierConfig(2, 0.5f),  // Control
                    new ElementMultiplierConfig(3, 1.0f),  // Empathy
                    new ElementMultiplierConfig(4, 1.0f),  // Focus
                    new ElementMultiplierConfig(5, 1.0f),  // Motivation
                    new ElementMultiplierConfig(6, 1.0f)   // Resilience
            ),
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
