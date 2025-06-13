package com.bellako.kiwi.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person3
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.home.HomeScreen
import com.bellako.kiwi.login.LoginScreen
import com.bellako.kiwi.login.LoginViewModel
import com.bellako.kiwi.settings.SettingsScreen
import com.bellako.kiwi.settings.SettingsViewModel

object ScreenRoutes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val SETTINGS = "settings"
    const val HELP = "help"
}

@Composable
fun MainScreen(loginViewModel : LoginViewModel = hiltViewModel()) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            if (navController.currentDestination != null && navController.currentDestination?.route != ScreenRoutes.LOGIN) {
                BottomAppBar(navController = navController)
            }
        },
        content = { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = ScreenRoutes.LOGIN,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(ScreenRoutes.LOGIN) {
                    LoginScreen(
                        viewModel = loginViewModel,
                        navController = navController
                    )
                }

                composable(ScreenRoutes.HOME) {
                    HomeScreen(
                        navController = navController,
                        onLogout = {
                            loginViewModel.logout()
                            navController.navigate(ScreenRoutes.LOGIN) {
                                popUpTo(ScreenRoutes.LOGIN) { inclusive = true }
                            }
                        }
                    )
                }

                composable(ScreenRoutes.HELP) {
                    HelpScreen(navController = navController)
                }

                composable(ScreenRoutes.SETTINGS) {
                    val settingsViewModel : SettingsViewModel = hiltViewModel()
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        navController = navController
                    )
                }
            }
        }
    )
}

@Composable
fun BottomAppBar(
    navController: NavController
) {
    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp))
            .testTag(CommonTestTags.BOTTOM_APPBAR),
        contentColor = Color.White,
        containerColor = MaterialTheme.colorScheme.primary,
        contentPadding = PaddingValues(16.dp),
        tonalElevation = 8.dp
    ) {
        IconButton(onClick = { navController.navigate(ScreenRoutes.HOME) }) {
            Icon(Icons.Filled.Image, contentDescription = "Home")
        }

        IconButton(onClick = {  }) {
            Icon(Icons.Filled.Brightness5, contentDescription = "Skills")
        }

        IconButton(onClick = {  }) {
            Icon(Icons.Filled.Preview, contentDescription = "Quests")
        }

        IconButton(onClick = {  }) {
            Icon(Icons.Filled.Adjust, contentDescription = "Battle")
        }

        IconButton(onClick = { navController.navigate(ScreenRoutes.SETTINGS) }) {
            Icon(Icons.Filled.Person3, contentDescription = "Settings")
        }
    }

}
