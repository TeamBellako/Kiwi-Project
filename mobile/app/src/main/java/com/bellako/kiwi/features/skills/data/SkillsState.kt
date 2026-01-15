package com.bellako.kiwi.features.skills.data

data class SkillsState(
    val skills: List<SkillDomain> = emptyList(),
) {
    val deckSkills: List<SkillDomain>
        get() =
            skills
                .filter { it.deckSlot > 0 }
                .sortedBy { it.deckSlot }

    fun skillById(id: Long): SkillDomain? = skills.firstOrNull { it.id == id }
}
