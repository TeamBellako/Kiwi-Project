package com.bellako.kiwi.common.services

import com.bellako.kiwi.features.personality.model.IPersonalityRepository

interface ScriptVariableResolver {
    suspend fun getValue(name: String): String
}

class PersonalityScriptVariableResolver(
    private val personalityRepository: IPersonalityRepository,
) : ScriptVariableResolver {
    override suspend fun getValue(name: String): String {
        val personality = personalityRepository.getPersonality().getOrNull() ?: return ""

        return when (name.uppercase()) {
            "NAME" -> personality.realName
            else -> name
        }
    }
}
