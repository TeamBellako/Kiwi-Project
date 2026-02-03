package com.bellako.kiwi.features.skills.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Label3
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.tests.SkillsTestFactory
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth

const val ICON_GOAL_WEIGHT = 0.25f

@Composable
fun SkillNotification(
    type: SkillNotificationType,
    skill: SkillDomain,
    onClick: () -> Unit = {},
) {
    val header =
        if (type == SkillNotificationType.NEW) {
            "New Skill"
        } else {
            "Skill Ready"
        }
    val body = skill.name

    val kiwiColor = LocalKiwiColors.current

    Column {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .clickable { onClick() },
        ) {
            Image(
                painter = painterResource(id = R.drawable.generic_notification),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = getResponsiveSizeHeight(Spacing.medium))
                        .padding(vertical = getResponsiveSizeHeight(Spacing.large)),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                    Kiwi_Image(
                        painter = painterResource(id = skillIcon(skill.icon)),
                        alt = "Goal icon",
                        colorFilter = ColorFilter.tint(kiwiColor.colorF),
                        modifier =
                            Modifier
                                .size(getResponsiveSizeWidth(30.dp)),
                        alignment = Alignment.Center,
                    )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier =
                        Modifier
                            .weight(1f - ICON_GOAL_WEIGHT)
                            .padding(start = getResponsiveSizeHeight(Spacing.medium)),
                ) {
                    Kiwi_H2(
                        KiwiTextArguments(
                            header,
                            color = kiwiColor.colorF,
                        ),
                    )
                    Kiwi_Spacer(getResponsiveSizeHeight(Spacing.xSmall))
                    Kiwi_Label3(
                        KiwiTextArguments(
                            body,
                            color = kiwiColor.color6,
                        ),
                    )
                }
            }
        }
    }
}

enum class SkillNotificationType {
    NEW,
    READY,
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Suppress("MagicNumber")
@Composable
fun GoalsNotification_Card_Preview() {
    Kiwi_Theme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(LocalKiwiColors.current.color0),
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(horizontal = getResponsiveSizeHeight(Spacing.large)),
            ) {
                SkillNotification(
                    type = SkillNotificationType.NEW,
                    skill = SkillsTestFactory.skill1(),
                )
                SkillNotification(
                    type = SkillNotificationType.READY,
                    skill = SkillsTestFactory.skill2(),
                )
            }
        }
    }
}
