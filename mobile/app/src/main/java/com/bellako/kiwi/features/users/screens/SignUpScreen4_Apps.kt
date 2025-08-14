package com.bellako.kiwi.features.users.screens

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.tooling.preview.Preview
import com.bellako.kiwi.ui.KiwiTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.ScreenRoutes
import com.bellako.kiwi.common.screens.components.Kiwi_Button
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Label2
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.Kiwi_TextArguments
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.google.accompanist.drawablepainter.rememberDrawablePainter


data class AppInfo(val packageName: String, val name: String, val icon: Drawable)

@Composable
fun SignUpScreen4_Apps(
    navController: NavController
) {
    SignUpScreen() {
        AppClassification(navController)
    }
}

@Composable
fun AppClassification(
    navController: NavController
) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val myPackageName = context.packageName

    // Get all installed apps
    val realApps = try {
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            // filter not system apps
            .filter {
                val isSystemApp = (it.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                val isUpdatedSystemApp = (it.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
                !isSystemApp || isUpdatedSystemApp
            }
            .map {
                val name = packageManager.getApplicationLabel(it).toString()
                val icon = packageManager.getApplicationIcon(it)
                AppInfo(it.packageName, name, icon)
            }
    } catch (_: Exception) {
        emptyList()
    }

    // If empty (preview) mock this app
    val apps = realApps.ifEmpty {
        listOf(
            AppInfo(
                packageName = myPackageName,
                name = "GrowTale",
                icon = ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round)!!
            )
        )
    }

    val myApp = apps.find { it.packageName == myPackageName }
    val goodApps = remember {
        mutableStateListOf<AppInfo>().apply {
            myApp?.let { add(it) }
        }
    }
    val badApps = remember {
        mutableStateListOf<AppInfo>().apply {
            addAll(apps.filter { it.packageName != myPackageName })
        }
    }


    // Show apps

    Column(
        modifier = Modifier
            .padding(getResponsiveSizeHeight(Spacing.medium))
    ) {
        Kiwi_P2(Kiwi_TextArguments(
            text = "Categorize your apps.\nTouch to switch between lists.",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary
        ))
        Kiwi_Spacer(Spacing.large)

        Row(modifier = Modifier
            .fillMaxSize().weight(1f)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Kiwi_H2(Kiwi_TextArguments(
                    text = "Good apps",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                ))
                Kiwi_Spacer(Spacing.small)
                LazyColumn {
                    items(goodApps) { app ->
                        AppItem(app) {
                            goodApps.remove(app)
                            badApps.add(app)
                            badApps.sortBy { it.name.lowercase() }
                        }
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Kiwi_H2(Kiwi_TextArguments(
                    text = "Evil apps",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                ))
                Kiwi_Spacer(Spacing.small)
                LazyColumn {
                    items(badApps) { app ->
                        AppItem(app) {
                            badApps.remove(app)
                            goodApps.add(app)
                            goodApps.sortBy { it.name.lowercase() }
                        }
                    }
                }
            }
        }
        Kiwi_Spacer(Spacing.large)

        Kiwi_Button(
            Kiwi_TextArguments(
                "CONTINUE",
                textAlign = TextAlign.Center
            ),
            rowModifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.navigate(ScreenRoutes.HOME)
            }
        )
    }

}

@Composable
fun AppItem(app: AppInfo, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(getResponsiveSizeHeight(Spacing.xSmall))
    ) {
        Kiwi_Image(
            painter = rememberDrawablePainter(app.icon),
            alt = "app icon",
            modifier = Modifier
                .size(getResponsiveSizeHeight(50.dp))
                .padding(end = getResponsiveSizeHeight(10.dp))
        )
        Kiwi_Label2(Kiwi_TextArguments(app.name))
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
