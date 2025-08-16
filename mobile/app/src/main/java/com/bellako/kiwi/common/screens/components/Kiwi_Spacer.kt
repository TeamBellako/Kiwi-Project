package com.bellako.kiwi.common.screens.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@Composable
fun Kiwi_Spacer(
    height: Dp = Spacing.medium
) {
    Spacer(Modifier.height(getResponsiveSizeHeight(height)))
}
