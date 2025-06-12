package com.bellako.kiwi.common

import com.bellako.kiwi.R
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.ui.ScreenRoutes
import com.bellako.kiwi.ui.components.Kiwi_Button
import com.bellako.kiwi.ui.components.Kiwi_Image
import com.bellako.kiwi.ui.components.Kiwi_Spacer
import com.bellako.kiwi.ui.components.Kiwi_TextArguments
import com.bellako.kiwi.ui.theme.KiwiTheme

@Composable
fun HomeScreen(
    navController: NavController,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Kiwi_Image(
            R.drawable.meme,
            ""
        )

        Kiwi_Spacer()

        Kiwi_Button(
            Kiwi_TextArguments(
                "Settings",
                color = Color.White
            ),
            { navController.navigate(ScreenRoutes.SETTINGS) }
        )

        Kiwi_Spacer()

        Kiwi_Button(
            Kiwi_TextArguments(
                "Help",
                color = Color.White
            ),
            { navController.navigate(ScreenRoutes.HELP) }
        )

        Kiwi_Spacer()

        Kiwi_Button(
            Kiwi_TextArguments(
                "Logout",
                color = Color.White
            ),
            onLogout
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    KiwiTheme {
        HomeScreen(
            rememberNavController(),
            {},
        )
    }
}