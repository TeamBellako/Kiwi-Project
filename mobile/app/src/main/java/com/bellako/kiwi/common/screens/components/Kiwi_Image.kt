package com.bellako.kiwi.common.screens.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun Kiwi_Image(
    painter: Painter,
    alt: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
) {
    Image(
        painter = painter,
        contentDescription = alt,
        modifier = modifier,
        contentScale = contentScale,
        alignment = alignment,
    )
}

@Composable
fun Kiwi_Image(
    painterResourceId: Int,
    alt: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
) {
    Kiwi_Image(
        painter = painterResource(id = painterResourceId),
        alt = alt,
        modifier = modifier,
        contentScale = contentScale,
        alignment = alignment,
    )
}
