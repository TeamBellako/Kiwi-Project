package com.bellako.kiwi.features.personality.data

data class PersonalityDomain(
    val realName: UserName,
    val knightName: UserName,
    val build: String,
    val goodApps: List<String>,
    val badApps: List<String>,
)
