package com.bellako.kiwi.ui.modals

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person3
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.bellako.kiwi.services.common.CommonTestTags
import com.bellako.kiwi.ui.screens.ScreenRoutes
import com.bellako.kiwi.ui.theme.KiwiTheme

data class NavigationItem(
    val icon: ImageVector,
    val route: String
)

val navigationItems = listOf(
    NavigationItem(
        icon = Icons.Default.Home,
        route = ScreenRoutes.HOME
    ),
    NavigationItem(
        icon = Icons.Filled.Brightness5,
        route = ScreenRoutes.HOME
    ),
    NavigationItem(
        icon = Icons.Filled.Preview,
        route = ScreenRoutes.HOME
    ),
    NavigationItem(
        icon = Icons.Filled.Adjust,
        route = ScreenRoutes.HOME
    ),
    NavigationItem(
        icon = Icons.Filled.Person3,
        route = ScreenRoutes.SETTINGS
    )
)

@Composable
fun AppBarModal(
    navController: NavController
) {
    val selectedNavigationIndex = rememberSaveable { mutableIntStateOf(0) }

    NavigationBar(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp))
            .testTag(CommonTestTags.BOTTOM_APPBAR),
        contentColor = MaterialTheme.colorScheme.inversePrimary,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedNavigationIndex.intValue == index,
                onClick = {
                    selectedNavigationIndex.intValue = index
                    navController.navigate(item.route)
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.inversePrimary
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Preview
@Composable
fun AppBarModalPreview() {
    val navController = rememberNavController()
    KiwiTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                bottomBar = {
                    AppBarModal(
                        navController = navController
                    )
                },
            ) { paddingValues ->
                val graph = navController.createGraph(startDestination = "") {}

                NavHost(
                    navController = navController,
                    graph = graph,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}