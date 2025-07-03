package com.bellako.kiwi.ui.modals

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.bellako.kiwi.services.common.CommonTestTags
import com.bellako.kiwi.ui.components.Kiwi_Button
import com.bellako.kiwi.ui.components.Kiwi_H1
import com.bellako.kiwi.ui.components.Kiwi_P1
import com.bellako.kiwi.ui.components.Kiwi_P2
import com.bellako.kiwi.ui.components.Kiwi_Spacer
import com.bellako.kiwi.ui.components.Kiwi_TextArguments
import com.bellako.kiwi.ui.theme.KiwiTheme
import com.bellako.kiwi.ui.theme.Spacing

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun PermissionsRequestModal(
    onPermissionsGranted: @Composable () -> Unit
) {
    val context = LocalContext.current

    val hasUsageAccess = remember { mutableStateOf(hasUsageStatsPermission(context)) }
    val hasStepPermission = remember { mutableStateOf(hasActivityRecognitionPermission(context)) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasStepPermission.value = granted
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasUsageAccess.value = hasUsageStatsPermission(context)
                hasStepPermission.value = hasActivityRecognitionPermission(context)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (hasUsageAccess.value && hasStepPermission.value) {
        onPermissionsGranted()
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            PermissionRequestLayout(
                context,
                launcher,
                hasUsageAccess,
                hasStepPermission
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
private fun PermissionRequestLayout(
    context: Context,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    hasUsageAccess: MutableState<Boolean>,
    hasStepPermission: MutableState<Boolean>
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .testTag(CommonTestTags.PERMISSIONS_REQUEST_MODAL),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(Spacing.medium),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = "Error icon",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .size(Spacing.xLarge)
            )

            Kiwi_Spacer()

            Kiwi_H1(Kiwi_TextArguments(
                "Permissions\n Required",
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                bold = true
            ))

            Kiwi_P2(
                Kiwi_TextArguments(
                "GrowTale requires permissions to access metrics such as steps and screen time.",
                    TextAlign.Center,
                    color = MaterialTheme.colorScheme.outline
                )
            )

            Kiwi_Spacer(Spacing.small)

            Kiwi_P2(
                Kiwi_TextArguments(
                    "Please click below to activate them before proceeding.",
                    TextAlign.Center,
                    color = MaterialTheme.colorScheme.outline
                )
            )

            Kiwi_Spacer(Spacing.large)

            Kiwi_Button(Kiwi_TextArguments(
                    "ENABLE USAGE ACCESS",
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    bold = true
                ),
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                },
                enabled = !hasUsageAccess.value
            )

            Kiwi_Spacer(Spacing.small)

            Kiwi_Button(Kiwi_TextArguments(
                    "ENABLE ACTIVITY RECOGNITION",
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    bold = true
                ),
                onClick = {
                    launcher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                },
                enabled = !hasStepPermission.value
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.Q)
private fun hasUsageStatsPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.unsafeCheckOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        android.os.Process.myUid(),
        context.packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}

private fun hasActivityRecognitionPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    } else true
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
fun PermissionsRequestModalPreview() {
    KiwiTheme {
        PermissionsRequestModal() {}
    }
}