package com.bellako.kiwi.features.tips.screen

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.AndroidRuntimeException
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_H1
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_P1
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.utils.Logger.warn
import com.bellako.kiwi.features.tips.data.TipState
import com.bellako.kiwi.features.tips.model.FakeTipsAPI
import com.bellako.kiwi.features.tips.model.ITipsViewModel
import com.bellako.kiwi.features.tips.model.TipsRepository
import com.bellako.kiwi.features.tips.model.TipsViewModel
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@Composable
fun TipScreen(viewModel: ITipsViewModel) {
    val tipState by viewModel.state.collectAsState()

    val kiwiColor = LocalKiwiColors.current
    val context = LocalContext.current

    Box(
        modifier =
            Modifier
                .padding(horizontal = Spacing.large)
                .fillMaxSize()
                .clickable { viewModel.closeTip() },
        contentAlignment = Alignment.Center,
    ) {
        Kiwi_Image(
            R.drawable.dialogue_small_bg,
            "Dialogue frame",
            contentScale = ContentScale.FillWidth,
        )

        Column(
            modifier =
                Modifier
                    .padding(horizontal = Spacing.large)
                    .fillMaxWidth()
                    .padding(getResponsiveSizeHeight(Spacing.large)),
            verticalArrangement = Arrangement.Center,
        ) {
            Kiwi_H1(
                KiwiTextArguments(
                    tipState?.title ?: "",
                    TextAlign.Left,
                    color = kiwiColor.color6,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                ),
            )

            Kiwi_P1(
                KiwiTextArguments(
                    tipState?.text ?: "",
                    color = kiwiColor.color6,
                ),
            )

            Kiwi_Spacer()

            if (
                tipState?.readMoreURL != null &&
                tipState?.readMoreURL?.isEmpty() == false &&
                !tipState?.readMoreURL.equals("")
            ) {
                Kiwi_FixedSizeButton(
                    textArguments =
                        KiwiTextArguments(
                            "Read more",
                            color = kiwiColor.color6,
                        ),
                    color = kiwiColor.color5A,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { openReadMoreURL(context, tipState?.readMoreURL ?: "") },
                )
            }
        }
    }
}

private fun openReadMoreURL(
    context: Context,
    url: String,
) {
    val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())

    try {
        context.startActivity(browserIntent)
    } catch (e: ActivityNotFoundException) {
        warn("No browser app found: ${e.message}")
    } catch (e: AndroidRuntimeException) {
        warn("Runtime error: ${e.message}")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Suppress("MagicNumber")
@Composable
fun TipScreen_Preview() {
    Kiwi_Theme {
        val fakeViewModel = TipsViewModel(TipsRepository(FakeTipsAPI()))
        TipScreen(fakeViewModel)
    }
}
