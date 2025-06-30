package com.bellako.kiwi.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bellako.kiwi.R

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_lightitalic, FontWeight.Light)
)

val LeagueSpartan = FontFamily(
    Font(R.font.leaguespartan_regular, FontWeight.Normal)
)

val kiwiTypography = Typography(
    // Section Titles
    headlineLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 22.sp
    ),
    // Dialogue Text / Default
    bodyLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Medium,
        fontSize = 19.sp,
        lineHeight = (19 * 1.4).sp
    ),
    // Subtitles
    bodyMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp
    ),
    // Subtexts
    bodySmall = TextStyle(
        fontFamily = LeagueSpartan,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    // Big Buttons / Options / Skills
    labelLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 22.sp
    ),
    // Cooldowns / Descriptions
    labelMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Light,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    // Timers
    displayMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 22.sp
    ),
    // Stat Numbers
    displayLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Medium,
        fontSize = 30.sp
    )
)
