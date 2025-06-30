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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.bellako.kiwi.ui.components.Kiwi_Spacer
import com.bellako.kiwi.ui.components.Kiwi_TextArguments
import com.bellako.kiwi.ui.theme.KiwiTheme

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
        PermissionRequestBox(
            context,
            launcher,
            hasUsageAccess,
            hasStepPermission
        )
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
private fun PermissionRequestBox(
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
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
                .fillMaxWidth(0.85f)
        ) {
            Kiwi_P1(
                Kiwi_TextArguments(
                    "GrowTale requires permissions to access metrics such as steps and screen time. " +
                            "Please click below to activate them before proceeding.",
                    TextAlign.Center,
                    color = MaterialTheme.colorScheme.inversePrimary
                )
            )

            Kiwi_Spacer()

            Kiwi_Button(
                Kiwi_TextArguments(
                    "Enable Usage Access",
                    color = MaterialTheme.colorScheme.inversePrimary,
                    bold = true
                ),
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                },
                enabled = !hasUsageAccess.value
            )

            Kiwi_Button(
                Kiwi_TextArguments(
                    "Enable Activity Recognition",
                    color = MaterialTheme.colorScheme.inversePrimary,
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
@Preview(device = "spec:width=411dp,height=891dp,dpi=420")
fun PermissionsRequestModalPreview() {
    KiwiTheme {
        PermissionsRequestModal() {}
    }
}