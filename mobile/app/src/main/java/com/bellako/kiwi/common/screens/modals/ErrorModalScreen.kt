package com.bellako.kiwi.common.screens.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Button
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@Composable
fun ErrorModalScreen(
    modifier: Modifier = Modifier,
    message: String =
        "Wild Error Appeared!",
    subMessage: String =
        "Uh-oh! It seems a careless scribe forgot to write this part of the story.\n\n" +
            "Let's get back on track!",
    buttonMessage: String = "RETRY",
    onButtonClick: (() -> Unit)? = null,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        ErrorModalLayout(
            modifier,
            message,
            subMessage,
            buttonMessage,
            onButtonClick,
        )
    }
}

@Composable
private fun ErrorModalLayout(
    modifier: Modifier = Modifier,
    message: String,
    subMessage: String,
    buttonMessage: String,
    onButtonClick: (() -> Unit)? = null,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(getResponsiveSizeHeight(Spacing.medium)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = "Error icon",
            tint = MaterialTheme.colorScheme.error,
            modifier =
                Modifier
                    .size(getResponsiveSizeHeight(50.dp)),
        )

        Kiwi_Spacer(Spacing.xLarge)

        Kiwi_H2(
            KiwiTextArguments(
                message,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                bold = true,
            ),
        )

        Kiwi_Spacer(Spacing.xLarge)

        Kiwi_P2(
            KiwiTextArguments(
                subMessage,
                TextAlign.Center,
                color = MaterialTheme.colorScheme.outline,
                modifier =
                    Modifier
                        .testTag(CommonTestTags.ERROR_MODAL),
            ),
        )

        Kiwi_Spacer(Spacing.xLarge)

        if (onButtonClick != null) {
            Kiwi_Button(
                textArguments =
                    KiwiTextArguments(
                        buttonMessage,
                        color = MaterialTheme.colorScheme.secondary,
                    ),
                onClick = onButtonClick,
                modifier =
                    Modifier
                        .padding(getResponsiveSizeHeight(Spacing.large)),
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun ErrorModal_Preview() {
    Kiwi_Theme {
        ErrorModalScreen {}
    }
}
