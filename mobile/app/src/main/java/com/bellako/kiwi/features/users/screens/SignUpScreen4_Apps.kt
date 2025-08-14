package com.bellako.kiwi.features.users.screens

import androidx.compose.ui.tooling.preview.Preview
import com.bellako.kiwi.ui.KiwiTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.common.screens.ScreenRoutes
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_TextArguments
import kotlinx.coroutines.delay


@Composable
fun SignUpScreen4_Apps(
    navController: NavController
) {
    SignUpScreen() {

        Kiwi_P2(Kiwi_TextArguments("Select apps"))

        LaunchedEffect(Unit) {
            delay(2000)
            navController.navigate(ScreenRoutes.HOME)
        }

    }
}

// -------------------------------------------------------------------------------------------------

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun SignUpScreen4_AppsPreview() {
    KiwiTheme {
        SignUpScreen4_Apps(
            navController = rememberNavController()
        )
    }
}
