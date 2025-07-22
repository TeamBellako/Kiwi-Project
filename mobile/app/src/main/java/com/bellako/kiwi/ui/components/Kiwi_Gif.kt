package com.bellako.kiwi.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest

@Composable
fun Kiwi_Gif (
    painterResource: String,
    alt: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val context = LocalContext.current

    val imageRequest = ImageRequest.Builder(context)
        .data("android.resource://${context.packageName}/drawable/${painterResource}") // no extension
        .decoderFactory(GifDecoder.Factory())
        .build()

    AsyncImage(
        model = imageRequest,
        contentDescription = alt,
        modifier = modifier,
        contentScale = contentScale,
    )

}