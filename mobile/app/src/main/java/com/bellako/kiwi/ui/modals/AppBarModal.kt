package com.bellako.kiwi.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.bellako.kiwi.ui.theme.Spacing

data class NavigationItem(
    val icon: ImageVector,
    val route: String,
    val enabled: Boolean = true
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
        route = ScreenRoutes.HOME,
        enabled = false
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
    Box(
        modifier = Modifier
            .wrapContentSize()
    ) {
        AppBarModalLayout(
            navController
        )
    }
}


@Composable
fun AppBarModalLayout(
    navController: NavController
) {
    val selectedNavigationIndex = rememberSaveable { mutableIntStateOf(0) }

    NavigationBar(
        modifier = Modifier
            .clip(RoundedCornerShape(40.dp, 40.dp, 0.dp, 0.dp))
            .testTag(CommonTestTags.BOTTOM_APPBAR),
        contentColor = MaterialTheme.colorScheme.secondary,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        navigationItems.forEachIndexed { index, item ->
            val tint = MaterialTheme.colorScheme.secondary.copy(
                alpha = if (item.enabled) 1f else 0.4f
            )
            NavigationBarItem(
                enabled = item.enabled,
                selected = selectedNavigationIndex.intValue == index,
                onClick = {
                    selectedNavigationIndex.intValue = index
                    navController.navigate(item.route)
                },
                icon = {
                    Box(
                        modifier = Modifier
                            .background(
                                color =
                                    if (selectedNavigationIndex.intValue == index)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(Spacing.small)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = "",
                            tint = tint
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent // Override default container color behavior
                )
            )
        }
    }
}

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
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