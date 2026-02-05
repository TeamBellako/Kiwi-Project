package com.bellako.kiwi.features.skills.data

data class SkillsState(
    val skills: Map<Long, SkillDomain> = emptyMap<Long, SkillDomain>(),
) {
    val allSkills: List<SkillDomain>
        get() =
            skills.values.toList()
    val deckSkills: List<SkillDomain>
        get() =
            skills.values
                .filter { it.deckSlot > 0 }
                .sortedBy { it.deckSlot }
}
