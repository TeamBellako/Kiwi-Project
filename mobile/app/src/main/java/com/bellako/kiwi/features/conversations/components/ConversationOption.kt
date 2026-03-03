package com.bellako.kiwi.features.conversations.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.features.conversations.data.ConversationOptionDomain
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth

@Composable
fun ConversationOption(
    option: ConversationOptionDomain,
    icon: Int? = null,
    onClick: () -> Unit = {},
) {
    val kiwiColor = LocalKiwiColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier =
            Modifier
                .wrapContentHeight()
                .background(
                    color = kiwiColor.color3,
                    shape = RoundedCornerShape(getResponsiveSizeHeight(10.dp)),
                ).padding(
                    horizontal = getResponsiveSizeWidth(12.dp),
                    vertical = getResponsiveSizeHeight(12.dp),
                ),
    ) {
        if (icon != null) {
            Kiwi_Image(
                painterResourceId = icon,
                alt = "",
                modifier = Modifier.size(Spacing.medium),
            )
        }
        Box(modifier = Modifier.weight(1f).clickable { onClick() }, contentAlignment = Alignment.Center) {
            Kiwi_P2(KiwiTextArguments(option.text))
        }
    }
}

@Composable
@Suppress("MagicNumber")
fun CharacterName(
    name: String,
    dark: Boolean,
    small: Boolean,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .height(getResponsiveSizeHeight(50.dp))
                .offset(y = getResponsiveSizeHeight((-10).dp)),
    ) {
        Kiwi_Image(
            if (small) {
                R.drawable.dialogue_small_name
            } else if (dark) {
                R.drawable.dialogue_name_dark
            } else {
                R.drawable.dialogue_name_light
            },
            "Characters name",
            modifier = Modifier.width(getResponsiveSizeWidth(75.dp)),
        )
        Kiwi_P2(
            KiwiTextArguments(
                if (dark) "???" else name,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(Spacing.medium, Spacing.small),
                color = if (dark || small) LocalKiwiColors.current.color6 else LocalKiwiColors.current.color3,
            ),
        )
    }
}

// region Preview
@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Suppress("MagicNumber")
@Composable
fun GoalsModal_Preview() {
    Kiwi_Theme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ConversationOption(
                ConversationOptionDomain(
                    1L,
                    "Esta es la opcion 1",
                    "Esta es la opcion 1",
                    "Esta es la opcion 1",
                    1,
                    0,
                ),
                icon = R.drawable.ic_daily_challenge_mental,
            )
            ConversationOption(
                ConversationOptionDomain(
                    2L,
                    "Esta seria otra opcion pero mejor que no la elijas. Las frases largas no suelen ser buenas",
                    "Esta seria otra opcion pero mejor que no la elijas. Las frases largas no suelen ser buenas",
                    "Esta seria otra opcion pero mejor que no la elijas. Las frases largas no suelen ser buenas",
                    1,
                    0,
                ),
            )
        }
    }
}
// endregion
