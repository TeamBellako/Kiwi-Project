package com.bellako.kiwi.features.personality.data

data class PersonalityDTO(
    val realName: String,
    val knightName: String,
    val build: String,
    val goodApps: List<String>,
    val badApps: List<String>,
    val neutralApps: List<String>,
)
