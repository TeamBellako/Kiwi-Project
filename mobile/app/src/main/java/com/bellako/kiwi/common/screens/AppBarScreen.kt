package com.bellako.kiwi.common.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.common.data.ScreenRoutes
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

data class NavigationItem(
    val icon: Int,
    val route: String,
    val enabled: Boolean = true,
)

val navigationItems =
    listOf(
        NavigationItem(
            icon = R.drawable.ic_map,
            route = ScreenRoutes.HOME,
        ),
        NavigationItem(
            icon = R.drawable.ic_fire,
            route = ScreenRoutes.WIP,
        ),
        NavigationItem(
            icon = R.drawable.ic_target,
            route = ScreenRoutes.WIP,
        ),
        NavigationItem(
            icon = R.drawable.ic_swords,
            route = ScreenRoutes.WIP,
        ),
        NavigationItem(
            icon = R.drawable.ic_person,
            route = ScreenRoutes.SETTINGS,
        ),
    )

@Composable
fun AppBarScreen(navController: NavController) {
    Box(
        modifier =
            Modifier
                .wrapContentSize(),
    ) {
        AppBarModalLayout(
            navController,
        )
    }
}

@Composable
fun AppBarModalLayout(navController: NavController) {
    val selectedNavigationIndex = rememberSaveable { mutableIntStateOf(0) }

    NavigationBar(
        modifier =
            Modifier
                .clip(RoundedCornerShape(getResponsiveSizeHeight(20.dp), getResponsiveSizeHeight(40.dp), 0.dp, 0.dp))
                .fillMaxWidth()
                .height(getResponsiveSizeHeight(100.dp))
                .testTag(CommonTestTags.BOTTOM_APPBAR),
        contentColor = MaterialTheme.colorScheme.secondary,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        navigationItems.forEachIndexed { index, item ->
            val tint =
                MaterialTheme.colorScheme.secondary.copy(
                    alpha = if (item.enabled) 1f else 0.4f,
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
                        modifier =
                            Modifier
                                .background(
                                    color =
                                        if (selectedNavigationIndex.intValue == index) {
                                            MaterialTheme.colorScheme.primaryContainer
                                        } else {
                                            Color.Transparent
                                        },
                                    shape = RoundedCornerShape(getResponsiveSizeHeight(10.dp)),
                                ).padding(getResponsiveSizeHeight(Spacing.small)),
                    ) {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = "",
                            tint = tint,
                            modifier = Modifier.size(getResponsiveSizeHeight(30.dp)),
                        )
                    }
                },
                colors =
                    NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent, // Override default container color behavior
                    ),
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun AppBarModal_Preview() {
    Kiwi_Theme {
        Scaffold(
            bottomBar = {
                AppBarScreen(navController = rememberNavController())
            },
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues))
            },
        )
    }
}
