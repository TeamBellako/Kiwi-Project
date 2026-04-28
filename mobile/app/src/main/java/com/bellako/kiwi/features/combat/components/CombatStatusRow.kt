package com.bellako.kiwi.features.combat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Label2
import com.bellako.kiwi.common.screens.components.Kiwi_Label3
import com.bellako.kiwi.common.screens.components.Kiwi_P3
import com.bellako.kiwi.features.combat.data.CombatActiveStatusDomain
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth

private val STATUS_ICON_SIZE = 28.dp
private val STATUS_POPUP_RADIUS = 12.dp
private val STATUS_POPUP_MAX_WIDTH = 220.dp

@Composable
fun CombatStatusRow(
    statuses: List<CombatActiveStatusDomain>,
    selectedStatusId: Long?,
    onStatusClick: (CombatActiveStatusDomain) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalKiwiColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(
                getResponsiveSizeWidth(Spacing.small),
                Alignment.CenterHorizontally,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        statuses.forEach { status ->
            val isSelected = status.stateId == selectedStatusId
            Box(
                modifier =
                    Modifier
                        .size(getResponsiveSizeHeight(STATUS_ICON_SIZE))
                        .clickable { onStatusClick(status) },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = statusIcon(status),
                    contentDescription = status.name,
                    tint = if (isSelected) colors.color8A else colors.color7A,
                    modifier = Modifier.size(getResponsiveSizeHeight(STATUS_ICON_SIZE)),
                )
            }
        }
    }
}

@Composable
fun CombatStatusPopup(
    status: CombatActiveStatusDomain,
    modifier: Modifier = Modifier,
) {
    val colors = LocalKiwiColors.current
    val maxWidth = getResponsiveSizeWidth(STATUS_POPUP_MAX_WIDTH)
    Box(
        modifier =
            modifier
                .widthIn(max = maxWidth)
                .background(
                    color = colors.color3A,
                    shape = RoundedCornerShape(getResponsiveSizeHeight(STATUS_POPUP_RADIUS)),
                ).border(
                    width = getResponsiveSizeHeight(1.dp),
                    color = colors.color5C,
                    shape = RoundedCornerShape(getResponsiveSizeHeight(STATUS_POPUP_RADIUS)),
                ).padding(
                    horizontal = getResponsiveSizeWidth(Spacing.medium),
                    vertical = getResponsiveSizeHeight(Spacing.small),
                ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.xSmall)),
        ) {
            Kiwi_Label2(
                KiwiTextArguments(
                    text = status.name,
                    color = colors.colorF,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
            )
            Kiwi_P3(
                KiwiTextArguments(
                    text = status.description,
                    color = colors.color7A,
                    textAlign = TextAlign.Center,
                ),
            )
            Kiwi_Label3(
                KiwiTextArguments(
                    text = remainingTurnsLabel(status.remainingTurns),
                    color = colors.color8A,
                    italic = true,
                    textAlign = TextAlign.Center,
                ),
            )
        }
    }
}

private fun remainingTurnsLabel(remainingTurns: Int): String = if (remainingTurns == 1) "1 Turn Left" else "$remainingTurns Turns Left"

private fun statusIcon(status: CombatActiveStatusDomain): ImageVector {
    val key = status.name.lowercase()
    return when {
        "poison" in key || "venom" in key || "tox" in key -> Icons.Filled.BugReport
        "burn" in key || "fire" in key -> Icons.Filled.LocalFireDepartment
        "freeze" in key || "frost" in key || "cold" in key -> Icons.Filled.AcUnit
        "rage" in key || "fury" in key -> Icons.Filled.Whatshot
        "sleep" in key || "insomn" in key -> Icons.Filled.NightsStay
        "heal" in key || "regen" in key -> Icons.Filled.Healing
        "shield" in key || "defen" in key || "guard" in key -> Icons.Filled.Shield
        "blind" in key || "vision" in key -> Icons.Filled.Visibility
        "love" in key || "charm" in key -> Icons.Filled.Favorite
        else -> Icons.Filled.AutoAwesome
    }
}

@Suppress("MagicNumber")
@Preview(name = "Status row", widthDp = 392, heightDp = 220)
@Composable
fun CombatStatusRow_Preview() {
    Kiwi_Theme {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(LocalKiwiColors.current.color2)
                    .padding(Spacing.medium),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            ) {
                CombatStatusPopup(
                    status =
                        CombatActiveStatusDomain(
                            stateId = 1L,
                            name = "Poisoned",
                            description = "Receives Damage After Each Turn",
                            remainingTurns = 1,
                        ),
                )
                CombatStatusRow(
                    statuses =
                        listOf(
                            CombatActiveStatusDomain(1L, "Poisoned", null, "Receives damage after each turn", 1),
                            CombatActiveStatusDomain(2L, "Burning", null, "Burning for 3 turns", 3),
                            CombatActiveStatusDomain(3L, "Frozen", null, "Cannot move", 2),
                        ),
                    selectedStatusId = 1L,
                    onStatusClick = {},
                )
            }
        }
    }
}
