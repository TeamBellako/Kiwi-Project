package com.bellako.kiwi.features.tips.data

data class TipDTO(
    val id: Long,
    val title: String,
    val text: String,
    val readMoreURL: String?,
)
