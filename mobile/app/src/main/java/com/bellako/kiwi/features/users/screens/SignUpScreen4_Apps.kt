package com.bellako.kiwi.features.users.screens

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
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
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
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
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val APP_ITEM_DISABLED_ALPHA = 0.3f
private val APP_ICON_SIZE_DP = 50.dp
private val APP_ICON_PADDING_DP = 10.dp
private const val DRAG_ALPHA = 1.0f
private const val DRAG_SCALE_X = 1.0f
private const val DRAG_SCALE_Y = 1.0f

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
            mutableStateListOf<AppInfo>()
        }
    val neutralApps =
        remember {
            mutableStateListOf<AppInfo>().apply {
                addAll(apps.filter { it.packageName != myPackageName })
            }
        }

    updateApps(goodApps, badApps, neutralApps, personalityViewModel)

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
            neutralApps = neutralApps,
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
    neutralApps: SnapshotStateList<AppInfo>,
    navController: NavController,
    onUpdateSuccess: (() -> Unit),
) {
    val kiwiColors = LocalKiwiColors.current
    val draggingApp = remember { mutableStateOf<AppInfo?>(null) }
    val dragOffset = remember { mutableStateOf(Offset.Zero) }
    val dragStartPosition = remember { mutableStateOf(Offset(0f, 0f)) }
    val dragItemStartPosition = remember { mutableStateOf(Offset(0f, 0f)) }
    var goodColumnRect by remember { mutableStateOf<Rect?>(null) }
    var badColumnRect by remember { mutableStateOf<Rect?>(null) }
    var neutralColumnRect by remember { mutableStateOf<Rect?>(null) }
    var boxPosition by remember { mutableStateOf<Offset?>(null) }

    val dragState =
        remember(draggingApp, dragOffset, dragStartPosition, dragItemStartPosition) {
            DragState(draggingApp, dragOffset, dragStartPosition, dragItemStartPosition)
        }
    val appLists =
        remember(goodApps, badApps, neutralApps) {
            AppLists(goodApps, badApps, neutralApps)
        }
    val columnRects =
        remember(goodColumnRect, badColumnRect, neutralColumnRect) {
            ColumnRects(goodColumnRect, badColumnRect, neutralColumnRect)
        }

    // Map to store each app's position
    val appPositions = remember { mutableMapOf<AppInfo, Rect>() }

    Column(
        modifier = Modifier.padding(getResponsiveSizeHeight(Spacing.medium)),
    ) {
        Kiwi_P2(
            KiwiTextArguments(
                text = "Categorize your apps.\nDrag them between columns.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = kiwiColors.color6,
            ),
        )

        Kiwi_Spacer(Spacing.large)

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .onGloballyPositioned { coordinates ->
                        boxPosition = coordinates.boundsInWindow().topLeft
                    }.pointerInput(isLoading, boxPosition, appPositions, dragState, columnRects, appLists) {
                        handleAppDragGestures(
                            isLoading,
                            boxPosition,
                            appPositions,
                            dragState,
                            columnRects,
                            appLists,
                            personalityViewModel,
                        )
                    },
        ) {
            AppColumnsRow(
                appLists,
                draggingApp.value,
                isLoading,
                appPositions,
                { rect -> goodColumnRect = rect },
                { rect -> neutralColumnRect = rect },
                { rect -> badColumnRect = rect },
            )

            // Render dragging app on top
            DraggingAppOverlay(draggingApp.value, dragItemStartPosition.value, boxPosition, dragOffset.value, isLoading)
        }

        Kiwi_Spacer(Spacing.large)

        Kiwi_FixedSizeButton(
            horizontalMargin = Spacing.xLarge,
            textArguments =
                KiwiTextArguments(
                    "CONTINUE",
                    textAlign = TextAlign.Center,
                ),
            color = kiwiColors.color5A,
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
    neutralApps: SnapshotStateList<AppInfo>,
    personalityViewModel: IPersonalityViewModel,
) {
    goodApps.sortBy { it.name.lowercase() }
    badApps.sortBy { it.name.lowercase() }
    neutralApps.sortBy { it.name.lowercase() }
    personalityViewModel.onAppsChanged(
        goodApps.map { it.packageName },
        badApps.map { it.packageName },
        neutralApps.map { it.packageName },
    )
}

@Composable
private fun AppColumnsRow(
    appLists: AppLists,
    draggingApp: AppInfo?,
    isLoading: Boolean,
    appPositions: MutableMap<AppInfo, Rect>,
    onGoodColumnPositioned: (Rect) -> Unit,
    onNeutralColumnPositioned: (Rect) -> Unit,
    onBadColumnPositioned: (Rect) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        ) {
            ColumnApps(
                title = "Good",
                apps = appLists.goodApps,
                draggingApp = draggingApp,
                isLoading = isLoading,
                onPositionChanged = { app, rect -> appPositions[app] = rect },
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .onGloballyPositioned { onGoodColumnPositioned(it.boundsInWindow()) },
            )
            ColumnApps(
                title = "Evil",
                apps = appLists.badApps,
                draggingApp = draggingApp,
                isLoading = isLoading,
                onPositionChanged = { app, rect -> appPositions[app] = rect },
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .onGloballyPositioned { onBadColumnPositioned(it.boundsInWindow()) },
            )
        }
        Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
            GridApps(
                title = "Neutral",
                apps = appLists.neutralApps,
                draggingApp = draggingApp,
                isLoading = isLoading,
                onPositionChanged = { app, rect -> appPositions[app] = rect },
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .onGloballyPositioned { onNeutralColumnPositioned(it.boundsInWindow()) },
            )
        }
    }
}

@Composable
private fun DraggingAppOverlay(
    draggingApp: AppInfo?,
    dragItemStartPosition: Offset,
    boxPosition: Offset?,
    dragOffset: Offset,
    isLoading: Boolean,
) {
    draggingApp?.let { app ->
        val itemOffsetInBox =
            Offset(
                dragItemStartPosition.x - (boxPosition?.x ?: 0f),
                dragItemStartPosition.y - (boxPosition?.y ?: 0f),
            )
        Box(
            modifier =
                Modifier.offset {
                    IntOffset(
                        (itemOffsetInBox.x + dragOffset.x).toInt(),
                        (itemOffsetInBox.y + dragOffset.y).toInt(),
                    )
                },
        ) {
            AppItem(
                app = app,
                enabled = !isLoading,
                isDragging = true,
                dragOffset = Offset.Zero,
                onPositionChanged = {},
            )
        }
    }
}

@Composable
fun AppItem(
    app: AppInfo,
    enabled: Boolean,
    isDragging: Boolean = false,
    dragOffset: Offset = Offset.Zero,
    onPositionChanged: (Rect) -> Unit = {},
) {
    val kiwiColors = LocalKiwiColors.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .background(kiwiColors.color3, shape = RoundedCornerShape(8.dp))
                .padding(getResponsiveSizeHeight(Spacing.xSmall))
                .onGloballyPositioned { coordinates ->
                    onPositionChanged(coordinates.boundsInWindow())
                }.offset { IntOffset(dragOffset.x.toInt(), dragOffset.y.toInt()) }
                .graphicsLayer {
                    if (isDragging) {
                        alpha = DRAG_ALPHA
                        scaleX = DRAG_SCALE_X
                        scaleY = DRAG_SCALE_Y
                    }
                },
    ) {
        Kiwi_Image(
            painter = rememberDrawablePainter(app.icon),
            alt = "app icon",
            modifier =
                Modifier
                    .size(getResponsiveSizeHeight(APP_ICON_SIZE_DP))
                    .padding(horizontal = getResponsiveSizeHeight(APP_ICON_PADDING_DP))
                    .graphicsLayer { alpha = if (enabled) 1f else APP_ITEM_DISABLED_ALPHA },
        )
        Kiwi_Label2(KiwiTextArguments(app.name))
    }
}

@Composable
fun ColumnApps(
    title: String,
    apps: List<AppInfo>,
    draggingApp: AppInfo?,
    isLoading: Boolean,
    onPositionChanged: (AppInfo, Rect) -> Unit,
    modifier: Modifier = Modifier,
) {
    val kiwiColors = LocalKiwiColors.current

    Column(
        modifier =
            modifier
                .background(kiwiColors.color1, shape = RoundedCornerShape(16.dp))
                .padding(16.dp),
    ) {
        Kiwi_H2(
            KiwiTextArguments(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            ),
        )
        Kiwi_Spacer(Spacing.small)
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        ) {
            items(apps) { app ->
                if (draggingApp != app) {
                    AppItem(
                        app = app,
                        enabled = !isLoading,
                        isDragging = false,
                        dragOffset = Offset.Zero,
                        onPositionChanged = { rect -> onPositionChanged(app, rect) },
                    )
                }
            }
        }
    }
}

@Composable
fun GridApps(
    title: String,
    apps: List<AppInfo>,
    draggingApp: AppInfo?,
    isLoading: Boolean,
    onPositionChanged: (AppInfo, Rect) -> Unit,
    modifier: Modifier = Modifier,
) {
    val kiwiColors = LocalKiwiColors.current

    Column(
        modifier =
            modifier
                .background(kiwiColors.color1, shape = RoundedCornerShape(16.dp))
                .padding(16.dp),
    ) {
        Kiwi_H2(
            KiwiTextArguments(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            ),
        )
        Kiwi_Spacer(Spacing.small)

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(apps) { app ->
                if (draggingApp != app) {
                    AppItem(
                        app = app,
                        enabled = !isLoading,
                        isDragging = false,
                        dragOffset = Offset.Zero,
                        onPositionChanged = { rect -> onPositionChanged(app, rect) },
                    )
                }
            }
        }
    }
}

private suspend fun androidx.compose.ui.input.pointer.PointerInputScope.handleAppDragGestures(
    isLoading: Boolean,
    boxPosition: Offset?,
    appPositions: MutableMap<AppInfo, Rect>,
    dragState: DragState,
    columnRects: ColumnRects,
    appLists: AppLists,
    personalityViewModel: IPersonalityViewModel,
) {
    detectDragGestures(
        onDragStart = { offset ->
            if (!isLoading && boxPosition != null) {
                val globalOffset = Offset(boxPosition.x + offset.x, boxPosition.y + offset.y)
                val appUnderFinger =
                    appPositions.entries
                        .firstOrNull { (_, rect) ->
                            rect.contains(globalOffset)
                        }?.key
                if (appUnderFinger != null) {
                    dragState.draggingApp.value = appUnderFinger
                    dragState.dragStartPosition.value = globalOffset
                    dragState.dragItemStartPosition.value = appPositions[appUnderFinger]?.topLeft ?: Offset.Zero
                    dragState.dragOffset.value = Offset.Zero
                }
            }
        },
        onDrag = { change, dragAmount ->
            if (dragState.draggingApp.value != null) {
                dragState.dragOffset.value =
                    Offset(
                        dragState.dragOffset.value.x + dragAmount.x,
                        dragState.dragOffset.value.y + dragAmount.y,
                    )
                change.consume()
            }
        },
        onDragEnd = {
            dragState.draggingApp.value?.let { app ->
                val currentDragPosition =
                    Offset(
                        dragState.dragStartPosition.value.x + dragState.dragOffset.value.x,
                        dragState.dragStartPosition.value.y + dragState.dragOffset.value.y,
                    )
                val droppedInGood = columnRects.good?.contains(currentDragPosition) == true
                val droppedInBad = columnRects.bad?.contains(currentDragPosition) == true
                val droppedInNeutral = columnRects.neutral?.contains(currentDragPosition) == true

                appLists.goodApps.remove(app)
                appLists.badApps.remove(app)
                appLists.neutralApps.remove(app)

                when {
                    droppedInGood -> appLists.goodApps.add(app)
                    droppedInBad -> appLists.badApps.add(app)
                    droppedInNeutral -> appLists.neutralApps.add(app)
                    else -> appLists.neutralApps.add(app)
                }
                updateApps(appLists.goodApps, appLists.badApps, appLists.neutralApps, personalityViewModel)
            }
            dragState.draggingApp.value = null
            dragState.dragOffset.value = Offset.Zero
        },
    )
}

data class DragState(
    val draggingApp: MutableState<AppInfo?>,
    val dragOffset: MutableState<Offset>,
    val dragStartPosition: MutableState<Offset>,
    val dragItemStartPosition: MutableState<Offset>,
)

data class AppLists(
    val goodApps: SnapshotStateList<AppInfo>,
    val badApps: SnapshotStateList<AppInfo>,
    val neutralApps: SnapshotStateList<AppInfo>,
)

data class ColumnRects(
    val good: Rect?,
    val bad: Rect?,
    val neutral: Rect?,
)
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
                        validPersonalityDTO().neutralApps,
                    ),
                ),
            navController = rememberNavController(),
        )
    }
}
