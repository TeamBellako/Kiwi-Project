package com.bellako.kiwi.features.users.screens

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.analytics.FirebaseEventNames
import com.bellako.kiwi.analytics.firebaseLogEvent
import com.bellako.kiwi.common.data.ScreenRoutes
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Button
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Label2
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.LoadingModal
import com.bellako.kiwi.common.screens.modals.ErrorModalScreen
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.users.tests.UsersTestTags
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val APP_ITEM_DISABLED_ALPHA = 0.3f

@Composable
fun SignUpScreen4_Apps(
    personalityViewModel: IPersonalityViewModel,
    navController: NavController,
) {
    SignUpScreen {
        AppClassification(personalityViewModel, navController)
    }
}

data class AppInfo(
    val packageName: String,
    val name: String,
    val icon: Drawable,
)

@Composable
fun AppClassification(
    personalityViewModel: IPersonalityViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val myPackageName = context.packageName
    val isPreview = LocalInspectionMode.current

    val personalityUiState by personalityViewModel.uiState.collectAsState()
    val personalityIsLoading by personalityViewModel.isLoading.collectAsState()

    var localLoading by remember { mutableStateOf(false) }

    val isLoading by remember { derivedStateOf { localLoading || personalityIsLoading } }

    // Get all installed apps
    val realApps =
        try {
            packageManager
                .getInstalledApplications(PackageManager.GET_META_DATA)
                // filter not system apps
                .filter {
                    val isSystemApp = (it.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    val isUpdatedSystemApp = (it.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
                    !isSystemApp || isUpdatedSystemApp
                }.map {
                    val name = packageManager.getApplicationLabel(it).toString()
                    val icon = packageManager.getApplicationIcon(it)
                    AppInfo(it.packageName, name, icon)
                }
        } catch (_: Exception) {
            emptyList()
        }

    // If empty (preview) mock this app
    val apps =
        realApps.ifEmpty {
            listOf(
                AppInfo(
                    packageName = myPackageName,
                    name = "GrowTale",
                    icon = ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round)!!,
                ),
            )
        }

    val myApp = apps.find { it.packageName == myPackageName }
    val goodApps =
        remember {
            mutableStateListOf<AppInfo>().apply {
                myApp?.let { add(it) }
            }
        }
    val badApps =
        remember {
            mutableStateListOf<AppInfo>().apply {
                addAll(apps.filter { it.packageName != myPackageName })
            }
        }
    updateApps(goodApps, badApps, personalityViewModel)

    if (personalityUiState == UIState.GeneralError) {
        ErrorModalScreen(onButtonClick = {
            personalityViewModel.resetUiState()
        })
    } else {
        AppClassificationColumns(
            isLoading = isLoading,
            personalityViewModel = personalityViewModel,
            goodApps = goodApps,
            badApps = badApps,
            navController = navController,
            onUpdateSuccess = {
                localLoading = true
            },
        )

        if (isLoading || isPreview) {
            LoadingModal()
        }
    }
}

@Composable
fun AppClassificationColumns(
    isLoading: Boolean,
    personalityViewModel: IPersonalityViewModel,
    goodApps: SnapshotStateList<AppInfo>,
    badApps: SnapshotStateList<AppInfo>,
    navController: NavController,
    onUpdateSuccess: (() -> Unit),
) {
    Column(
        modifier =
            Modifier.padding(getResponsiveSizeHeight(Spacing.medium)),
    ) {
        Kiwi_P2(
            KiwiTextArguments(
                text = "Categorize your apps.\nTap to switch between lists.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary,
            ),
        )
        Kiwi_Spacer(Spacing.large)

        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .weight(1f),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Kiwi_H2(
                    KiwiTextArguments(
                        text = "Good apps",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    ),
                )
                Kiwi_Spacer(Spacing.small)
                LazyColumn {
                    items(goodApps) { app ->
                        AppItem(
                            app = app,
                            onClick = {
                                goodApps.remove(app)
                                badApps.add(app)
                                updateApps(goodApps, badApps, personalityViewModel)
                            },
                            enabled = !isLoading,
                        )
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Kiwi_H2(
                    KiwiTextArguments(
                        text = "Evil apps",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    ),
                )
                Kiwi_Spacer(Spacing.small)
                LazyColumn {
                    items(badApps) { app ->
                        AppItem(
                            app = app,
                            onClick = {
                                badApps.remove(app)
                                goodApps.add(app)
                                updateApps(goodApps, badApps, personalityViewModel)
                            },
                            enabled = !isLoading,
                        )
                    }
                }
            }
        }
        Kiwi_Spacer(Spacing.large)

        Kiwi_Button(
            textArguments =
                KiwiTextArguments(
                    "CONTINUE",
                    textAlign = TextAlign.Center,
                ),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    if (personalityViewModel.updateApps().isSuccess) {
                        firebaseLogEvent(FirebaseEventNames.SIGNUP_4_APPS_COMPLETED)

                        navController.navigate(ScreenRoutes.HOME)
                        onUpdateSuccess()
                    }
                }
            },
            enabled = !isLoading,
            testTag = UsersTestTags.SIGNUP_BUTTON,
        )
    }
}

private fun updateApps(
    goodApps: SnapshotStateList<AppInfo>,
    badApps: SnapshotStateList<AppInfo>,
    personalityViewModel: IPersonalityViewModel,
) {
    goodApps.sortBy { it.name.lowercase() }
    badApps.sortBy { it.name.lowercase() }
    personalityViewModel.onAppsChanged(
        goodApps.map { it.packageName },
        badApps.map { it.packageName },
    )
}

@Composable
fun AppItem(
    app: AppInfo,
    onClick: () -> Unit,
    enabled: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .clickable(
                    onClick =
                        if (enabled) {
                            onClick
                        } else {
                            {}
                        },
                ).padding(getResponsiveSizeHeight(Spacing.xSmall)),
    ) {
        Kiwi_Image(
            painter = rememberDrawablePainter(app.icon),
            alt = "app icon",
            modifier =
                Modifier
                    .size(getResponsiveSizeHeight(50.dp))
                    .padding(end = getResponsiveSizeHeight(10.dp))
                    .graphicsLayer { alpha = if (enabled) 1f else APP_ITEM_DISABLED_ALPHA },
        )
        Kiwi_Label2(KiwiTextArguments(app.name))
    }
}

// -------------------------------------------------------------------------------------------------

@SuppressLint("ViewModelConstructorInComposable")
@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun SignUpScreen4_Apps_Preview() {
    Kiwi_Theme {
        SignUpScreen4_Apps(
            personalityViewModel =
                PersonalityFakeViewModel(
                    PersonalityState(
                        validPersonalityDTO().realName,
                        validPersonalityDTO().knightName,
                        validPersonalityDTO().build,
                        validPersonalityDTO().goodApps,
                        validPersonalityDTO().badApps,
                    ),
                ),
            navController = rememberNavController(),
        )
    }
}
