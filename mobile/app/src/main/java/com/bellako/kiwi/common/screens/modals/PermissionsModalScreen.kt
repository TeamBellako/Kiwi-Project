package com.bellako.kiwi.common.screens.modals

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.analytics.FirebaseEventNames
import com.bellako.kiwi.analytics.firebaseLogEvent
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Button
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.common.utils.hasUsageStatsPermission
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun PermissionsModalScreen(
    navController: NavHostController,
    exclusionRoutes: List<String> = listOf(),
    withPermissions: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isExclusion =
        navBackStackEntry == null ||
            navBackStackEntry?.destination == null ||
            exclusionRoutes.contains(navBackStackEntry?.destination?.route)

    var hadPermissionsOnStop = false
    val hasPermissions = remember { mutableStateOf(hasUsageStatsPermission(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_STOP) {
                    hadPermissionsOnStop = hasPermissions.value
                }
                if (event == Lifecycle.Event.ON_START) {
                    hasPermissions.value = hasUsageStatsPermission(context)
                    if (hasPermissions.value && !hadPermissionsOnStop) {
                        firebaseLogEvent(FirebaseEventNames.PERMISSION_GRANTED)
                    }
                }
            }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (isPreview || (!hasPermissions.value && !isExclusion)) {
        AudioManager.playSFX(context, R.raw.snd_ui_modalopen)
        PermissionRequestLayout(context)
    } else {
        withPermissions()
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
private fun PermissionRequestLayout(context: Context) {
    val kiwiColors = LocalKiwiColors.current

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(kiwiColors.color2)
                .testTag(CommonTestTags.PERMISSIONS_REQUEST_MODAL),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.matchParentSize(),
            color = Color.Black.copy(alpha = 0.25f),
        ) {}

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(getResponsiveSizeHeight(Spacing.medium)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = "Error icon",
                tint = kiwiColors.color8A,
                modifier =
                    Modifier
                        .size(getResponsiveSizeHeight(50.dp)),
            )

            Kiwi_Spacer(Spacing.xLarge)

            Kiwi_H2(
                KiwiTextArguments(
                    "Permissions Required",
                    color = kiwiColors.colorF,
                    textAlign = TextAlign.Center,
                    bold = true,
                ),
            )

            Kiwi_Spacer(Spacing.xLarge)

            Kiwi_P2(
                KiwiTextArguments(
                    "GrowTale requires permissions to access metrics such as apps usage time.",
                    TextAlign.Center,
                    color = kiwiColors.colorF1,
                ),
            )

            Kiwi_Spacer(Spacing.small)

            Kiwi_P2(
                KiwiTextArguments(
                    "Please click below to activate them before proceeding.",
                    TextAlign.Center,
                    color = kiwiColors.colorF1,
                ),
            )

            Kiwi_Spacer(Spacing.xLarge)

            Kiwi_Button(
                textArguments =
                    KiwiTextArguments(
                        "ENABLE APP USAGE ACCESS",
                        color = kiwiColors.color7,
                        textAlign = TextAlign.Center,
                        bold = true,
                    ),
                color = kiwiColors.color5A,
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                },
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
fun PermissionsRequestModal_Preview() {
    Kiwi_Theme {
        PermissionsModalScreen(
            navController = rememberNavController(),
        ) {}
    }
}
