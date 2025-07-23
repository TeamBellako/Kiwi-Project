package com.bellako.kiwi.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.bellako.kiwi.ui.theme.Spacing
import com.bellako.kiwi.ui.theme.getResponsiveRelativeSize

@Composable
fun Kiwi_Spacer(
    height: Dp = getResponsiveRelativeSize(Spacing.medium)
) {
    Spacer(Modifier.height(getResponsiveRelativeSize(height)))
}
