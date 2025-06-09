package com.bellako.kiwi.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.ui.components.Kiwi_Button
import com.bellako.kiwi.ui.components.Kiwi_Spacer
import com.bellako.kiwi.ui.theme.KiwiTheme

@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Kiwi_Button(
            "Settings",
            onNavigateToSettings
        )

        Kiwi_Spacer()

        Kiwi_Button(
            "Help",
            onNavigateToHelp
        )

        Kiwi_Spacer()

        Kiwi_Button(
            "Logout",
            onLogout
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    KiwiTheme {
        HomeScreen(
            {},
            {},
            {}
        )
    }
}