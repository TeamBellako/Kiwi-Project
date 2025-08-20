package com.bellako.kiwi.common.screens.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun Kiwi_Image(
    painterResourceId: Int,
    alt: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    Image(
        painter = painterResource(id = painterResourceId),
        contentDescription = alt,
        modifier = modifier,
        contentScale = contentScale,
    )
}
