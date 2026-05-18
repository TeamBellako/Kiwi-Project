package com.bellako.kiwi.features.appbar.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.common.data.ScreenRoutes
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.features.appbar.model.AppBarViewModel
import com.bellako.kiwi.features.appbar.model.IAppBarViewModel
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

private const val NAV_ICON_BG_FADE_MS = 250
private const val NAV_ICON_POP_SCALE = 1.25f
private const val NAV_ICON_POP_UP_MS = 110
private const val NAV_ICON_PRESS_SCALE = 0.85f
private const val NAV_ICON_PRESS_MS = 90

// -------------------------------------------------------------------------------------------------

@Composable
fun AppBarScreen(
    navController: NavController,
    appBarViewModel: AppBarViewModel = hiltViewModel(),
) {
    Box(
        modifier =
            Modifier
                .wrapContentSize()
                .background(LocalKiwiColors.current.color2),
    ) {
        AppBarModalLayout(navController, appBarViewModel)
    }
}

// -------------------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarModalLayout(
    navController: NavController,
    appBarViewModel: IAppBarViewModel,
) {
    val kiwiColors = LocalKiwiColors.current
    val context = LocalContext.current

    val state by appBarViewModel.state.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = normalize(navBackStackEntry?.destination?.route)

    LaunchedEffect(currentRoute) {
        appBarViewModel.onRouteChanged(currentRoute)

        currentRoute?.let {
            appBarViewModel.onNewContentVisited(currentRoute)
        }
    }

    CompositionLocalProvider(LocalRippleConfiguration provides null) {
        NavigationBar(
            modifier =
                Modifier
                    .clip(
                        RoundedCornerShape(
                            getResponsiveSizeHeight(30.dp),
                            getResponsiveSizeHeight(30.dp),
                            0.dp,
                            0.dp,
                        ),
                    ).fillMaxWidth()
                    .navigationBarsPadding()
                    .height(getResponsiveSizeHeight(90.dp))
                    .testTag(CommonTestTags.BOTTOM_APPBAR),
            containerColor = kiwiColors.color1,
        ) {
            Spacer(modifier = Modifier.width(getResponsiveSizeHeight(Spacing.large)))

            state.items.forEach { item ->
                val isSelected = currentRoute == item.route
                val interactionSource = remember { MutableInteractionSource() }
                val pressed by interactionSource.collectIsPressedAsState()

                NavigationBarItem(
                    selected = isSelected,
                    enabled = true,
                    interactionSource = interactionSource,
                    onClick = {
                        if (item.route != currentRoute) {
                            AudioManager.playSFX(
                                context,
                                R.raw.snd_ui_navigationtransition,
                            )
                            navController.navigate(item.route)
                        }
                    },
                    icon = {
                        AppBarIcon(
                            icon = item.icon,
                            selected = isSelected,
                            pressed = pressed,
                            showBadge = item.hasNewContent,
                        )
                    },
                    colors =
                        NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                        ),
                )
            }

            Spacer(modifier = Modifier.width(getResponsiveSizeHeight(Spacing.large)))
        }
    }
}

fun normalize(route: String?): String? =
    when {
        route?.startsWith(ScreenRoutes.SKILLS) == true -> ScreenRoutes.SKILLS
        route?.startsWith(ScreenRoutes.OBJECTIVES) == true -> ScreenRoutes.OBJECTIVES
        else -> route
    }

// -------------------------------------------------------------------------------------------------

@Composable
private fun AppBarIcon(
    icon: Int,
    selected: Boolean,
    pressed: Boolean,
    showBadge: Boolean,
) {
    val kiwiColors = LocalKiwiColors.current

    val pressScale by animateFloatAsState(
        targetValue = if (pressed) NAV_ICON_PRESS_SCALE else 1f,
        animationSpec = tween(NAV_ICON_PRESS_MS, easing = EaseOut),
        label = "navIconPress",
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (selected) kiwiColors.color5A else kiwiColors.color5A.copy(alpha = 0f),
        animationSpec = tween(NAV_ICON_BG_FADE_MS, easing = EaseInOut),
        label = "navIconBackground",
    )

    val iconScale = remember { Animatable(1f) }
    LaunchedEffect(selected) {
        if (selected) {
            iconScale.snapTo(1f)
            iconScale.animateTo(NAV_ICON_POP_SCALE, tween(NAV_ICON_POP_UP_MS, easing = EaseOut))
            iconScale.animateTo(
                1f,
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow,
                ),
            )
        }
    }

    Box(
        modifier =
            Modifier
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(getResponsiveSizeHeight(10.dp)),
                ).padding(getResponsiveSizeHeight(Spacing.xSmall)),
    ) {
        Box {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = kiwiColors.colorF,
                modifier =
                    Modifier
                        .size(getResponsiveSizeHeight(50.dp))
                        .graphicsLayer {
                            scaleX = iconScale.value * pressScale
                            scaleY = iconScale.value * pressScale
                        },
            )

            if (showBadge) {
                Box(
                    modifier =
                        Modifier
                            .size(getResponsiveSizeHeight(8.dp))
                            .background(kiwiColors.color8A, CircleShape)
                            .align(Alignment.TopEnd),
                )
            }
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
