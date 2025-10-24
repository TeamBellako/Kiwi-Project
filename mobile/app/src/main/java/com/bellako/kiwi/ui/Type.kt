package com.bellako.kiwi.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bellako.kiwi.R

val Poppins =
    FontFamily(
        Font(R.font.poppins_regular, FontWeight.Normal),
        Font(R.font.poppins_medium, FontWeight.Medium),
        Font(R.font.poppins_semibold, FontWeight.SemiBold),
        Font(R.font.poppins_bold, FontWeight.Bold),
        Font(R.font.poppins_lightitalic, FontWeight.Light),
    )

val LeagueSpartan =
    FontFamily(
        Font(R.font.leaguespartan_regular, FontWeight.Normal),
    )

val kiwiTypography =
    Typography(
        // Section Titles
        headlineLarge =
            TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                lineHeight = (24 * 1.4).sp,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                lineHeight = (20 * 1.4).sp,
            ),
        headlineSmall =
            TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = (16 * 1.4).sp,
            ),
        // Dialogue Text / Default
        bodyLarge =
            TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                lineHeight = (18 * 1.4).sp,
            ),
        // Subtitles
        bodyMedium =
            TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = (14 * 1.4).sp,
            ),
        // Subtexts
        bodySmall =
            TextStyle(
                fontFamily = LeagueSpartan,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
                lineHeight = (10 * 1.4).sp,
            ),
        // Big Buttons / Options / Skills
        labelLarge =
            TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = (16 * 1.4).sp,
            ),
        // Cooldowns / Descriptions
        labelMedium =
            TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.Light,
                fontSize = 13.sp,
                lineHeight = (13 * 1.4).sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.Light,
                fontSize = 10.sp,
                lineHeight = (10 * 1.4).sp,
            ),
        // Timers
        displayLarge =
            TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                lineHeight = 22.sp,
            ),
        // Stat Numbers
        displayMedium =
            TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 30.sp,
            ),
    )
