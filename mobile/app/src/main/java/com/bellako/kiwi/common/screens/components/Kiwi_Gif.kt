package com.bellako.kiwi.common.screens.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest

@Composable
fun Kiwi_Gif (
    painterResourceId: Int,
    alt: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val isPreview = LocalInspectionMode.current
    if (isPreview) {
        // Gifs not shown in preview, show as static image instead
        Kiwi_Image(
            painterResourceId = painterResourceId,
            alt = alt,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        // Actual gif in runtime
        val imageRequest = ImageRequest.Builder(LocalContext.current)
            .data(painterResourceId)
            .decoderFactory(GifDecoder.Factory())
            .build()

        AsyncImage(
            model = imageRequest,
            contentDescription = alt,
            modifier = modifier,
            contentScale = contentScale,
        )
    }
}