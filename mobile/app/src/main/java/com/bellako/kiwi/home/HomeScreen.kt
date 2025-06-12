package com.bellako.kiwi.home

import com.bellako.kiwi.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person3
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.common.CommonTestTags
import com.bellako.kiwi.common.ScreenRoutes
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
            .padding(16.dp)
            .background(Color.White)
            .testTag(CommonTestTags.HOME_SCREEN),
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
            rememberNavController()
        ) {}
    }
}