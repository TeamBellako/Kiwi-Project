package com.bellako.kiwi.features.users.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.bellako.kiwi.common.services.eventbus.EventBus
import com.bellako.kiwi.common.services.eventbus.EventPayload
import com.bellako.kiwi.common.services.eventbus.EventType
import com.bellako.kiwi.features.nodes.model.INodesViewModel
import com.bellako.kiwi.features.nodes.tests.NodesFakeViewModel
import com.bellako.kiwi.features.skills.model.ISkillsViewModel
import com.bellako.kiwi.features.skills.tests.SkillsFakeViewModel
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.LoadingModal
import com.bellako.kiwi.common.screens.modals.ErrorModalScreen
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import com.bellako.kiwi.features.goals.tests.GoalsFakeViewModel
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.model.IUsersViewModel
import com.bellako.kiwi.features.users.tests.UsersFakeViewModel
import com.bellako.kiwi.features.users.tests.UsersTestTags
import com.bellako.kiwi.ui.KIWI_DISABLED_ALPHA
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val APP_CARD_WIDTH = 96.dp
private val APP_CARD_HEIGHT = 112.dp
private val APP_CARD_CORNER = 16.dp
private val APP_SECTION_CORNER = 20.dp
private val APP_ICON_SIZE_DP = 40.dp
private val APP_ICON_CONTAINER_SIZE_DP = 52.dp

private const val DRAG_ALPHA = 1.0f
private const val DRAG_SCALE_X = 1.0f
private const val DRAG_SCALE_Y = 1.0f
private const val NEUTRAL_APPS_GRID_SIZE = 3

private val GOOD_APP_PACKAGES =
    setOf(
        "com.bellako.kiwi",
        "org.daylio",
        "com.google.android.apps.books",
        "com.amazon.kindle",
        "com.headspace.android",
        "com.calm.android",
        "com.google.android.apps.fitness",
        "com.fitbit.FitbitMobile",
        "com.duolingo",
        "org.khanacademy.android",
        "com.notion.id",
        "com.todoist",
        "com.ticktick.task",
        "com.microsoft.todos",
        "com.google.android.keep",
        "com.evernote",
        "forestapp.cc",
        "com.sleepcycle",
    )

private val BAD_APP_PACKAGES =
    setOf(
        "com.zhiliaoapp.musically",
        "com.instagram.android",
        "com.twitter.android",
        "com.facebook.katana",
        "com.facebook.lite",
        "com.snapchat.android",
        "com.reddit.frontpage",
        "com.pinterest",
        "com.google.android.youtube",
        "tv.twitch.android.app",
        "com.netflix.mediaclient",
        "com.discord",
        "com.king.candycrushsaga",
        "com.supercell.clashroyale",
        "com.bet365.bet365",
    )

@Composable
fun SignUpScreen4_Apps(
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    goalsViewModel: IGoalsViewModel,
    nodesViewModel: INodesViewModel,
    skillsViewModel: ISkillsViewModel,
    navController: NavController,
) {
    SignUpScreen(showSmoke = false) {
        AppClassification(
            usersViewModel,
            personalityViewModel,
            navController,
            goalsViewModel,
            nodesViewModel,
            skillsViewModel,
        )
    }
}

data class AppInfo(
    val packageName: String,
    val name: String,
    val icon: Drawable,
)

@Composable
fun AppClassification(
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController,
    goalsViewModel: IGoalsViewModel,
    nodesViewModel: INodesViewModel,
    skillsViewModel: ISkillsViewModel,
) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val myPackageName = context.packageName
    val isPreview = LocalInspectionMode.current

    val personalityUiState by personalityViewModel.uiState.collectAsState()
    val personalityIsLoading by personalityViewModel.isLoading.collectAsState()

    var localLoading by remember { mutableStateOf(false) }
    val isLoading by remember { derivedStateOf { localLoading || personalityIsLoading } }

    val apps =
        remember(packageManager, myPackageName) {
            val realApps =
                try {
                    // The user-visible app set = anything with a launcher
                    // activity. This covers preinstalled apps with a drawer
                    // icon and any Play Store install, and excludes pure OS
                    // framework packages (no launcher, no Play presence).
                    val launcherIntent =
                        Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_LAUNCHER)
                        }
                    val launchable =
                        packageManager
                            .queryIntentActivities(launcherIntent, 0)
                            .associateBy { it.activityInfo.packageName }
                            .values
                            .map { resolveInfo ->
                                val applicationInfo = resolveInfo.activityInfo.applicationInfo
                                val name = packageManager.getApplicationLabel(applicationInfo).toString()
                                val icon = packageManager.getApplicationIcon(applicationInfo)
                                AppInfo(applicationInfo.packageName, name, icon)
                            }

                    // GrowTale must always appear in the picker — re-add it
                    // if the launcher query didn't return it for any reason.
                    val withGrowTale =
                        if (launchable.any { it.packageName == myPackageName }) {
                            launchable
                        } else {
                            launchable +
                                AppInfo(
                                    packageName = myPackageName,
                                    name = "GrowTale",
                                    icon = ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round)!!,
                                )
                        }
                    withGrowTale.sortedBy { it.name.lowercase() }
                } catch (_: Exception) {
                    emptyList()
                }

            realApps.ifEmpty {
                listOf(
                    AppInfo(
                        packageName = myPackageName,
                        name = "GrowTale",
                        icon = ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round)!!,
                    ),
                    AppInfo(
                        packageName = "com.instagram.android",
                        name = "Instagram",
                        icon = ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round)!!,
                    ),
                    AppInfo(
                        packageName = "com.todoist",
                        name = "Todoist",
                        icon = ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round)!!,
                    ),
                )
            }
        }

    val initialBuckets =
        remember(apps, myPackageName) {
            buildInitialAppBuckets(apps, myPackageName)
        }

    val goodApps = remember { mutableStateListOf<AppInfo>() }
    val badApps = remember { mutableStateListOf<AppInfo>() }
    val neutralApps = remember { mutableStateListOf<AppInfo>() }

    val listsInitialized = remember { mutableStateOf(false) }

    if (!listsInitialized.value) {
        goodApps.clear()
        badApps.clear()
        neutralApps.clear()

        goodApps.addAll(initialBuckets.first)
        badApps.addAll(initialBuckets.second)
        neutralApps.addAll(initialBuckets.third)

        listsInitialized.value = true
    }

    LaunchedEffect(goodApps.toList(), badApps.toList(), neutralApps.toList()) {
        personalityViewModel.onAppsChanged(
            goodApps.map { it.packageName },
            badApps.map { it.packageName },
            neutralApps.map { it.packageName },
        )
    }

    if (personalityUiState == UIState.GeneralError) {
        ErrorModalScreen(
            onButtonClick = {
                personalityViewModel.resetUiState()
            },
        )
    } else {
        AppClassificationColumns(
            isLoading = isLoading,
            usersViewModel = usersViewModel,
            personalityViewModel = personalityViewModel,
            goalsViewModel = goalsViewModel,
            nodesViewModel = nodesViewModel,
            skillsViewModel = skillsViewModel,
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

private fun buildInitialAppBuckets(
    apps: List<AppInfo>,
    myPackageName: String,
): Triple<List<AppInfo>, List<AppInfo>, List<AppInfo>> {
    val good = mutableListOf<AppInfo>()
    val bad = mutableListOf<AppInfo>()
    val neutral = mutableListOf<AppInfo>()

    apps.forEach { app ->
        when {
            app.packageName == myPackageName -> good.add(app)
            GOOD_APP_PACKAGES.contains(app.packageName) -> good.add(app)
            BAD_APP_PACKAGES.contains(app.packageName) -> bad.add(app)
            else -> neutral.add(app)
        }
    }

    return Triple(
        good.sortedBy { it.name.lowercase() },
        bad.sortedBy { it.name.lowercase() },
        neutral.sortedBy { it.name.lowercase() },
    )
}

@Composable
fun AppClassificationColumns(
    isLoading: Boolean,
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    goalsViewModel: IGoalsViewModel,
    nodesViewModel: INodesViewModel,
    skillsViewModel: ISkillsViewModel,
    goodApps: SnapshotStateList<AppInfo>,
    badApps: SnapshotStateList<AppInfo>,
    neutralApps: SnapshotStateList<AppInfo>,
    navController: NavController,
    onUpdateSuccess: (() -> Unit),
) {
    val kiwiColors = LocalKiwiColors.current
    val haptic = LocalHapticFeedback.current

    val draggingApp = remember { mutableStateOf<AppInfo?>(null) }
    val dragOffset = remember { mutableStateOf(Offset.Zero) }
    val dragStartPosition = remember { mutableStateOf(Offset.Zero) }
    val dragItemStartPosition = remember { mutableStateOf(Offset.Zero) }

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

    val appPositions = remember { mutableMapOf<AppInfo, Rect>() }

    Column(
        modifier = Modifier.padding(getResponsiveSizeHeight(Spacing.medium)),
    ) {
        Kiwi_Spacer(Spacing.large)

        Kiwi_P2(
            KiwiTextArguments(
                text = "Hold and drag to move apps",
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
                            isLoading = isLoading,
                            boxPosition = boxPosition,
                            appPositions = appPositions,
                            dragState = dragState,
                            columnRects = columnRects,
                            appLists = appLists,
                            onDragStarted = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                        )
                    },
        ) {
            AppColumnsRow(
                appLists = appLists,
                draggingApp = draggingApp.value,
                isLoading = isLoading,
                appPositions = appPositions,
                onGoodColumnPositioned = { rect -> goodColumnRect = rect },
                onNeutralColumnPositioned = { rect -> neutralColumnRect = rect },
                onBadColumnPositioned = { rect -> badColumnRect = rect },
            )

            DraggingAppOverlay(
                draggingApp = draggingApp.value,
                dragItemStartPosition = dragItemStartPosition.value,
                boxPosition = boxPosition,
                dragOffset = dragOffset.value,
                isLoading = isLoading,
            )
        }

        Kiwi_Spacer(Spacing.large)

        Kiwi_FixedSizeButton(
            horizontalMargin = Spacing.xLarge,
            textArguments =
                KiwiTextArguments(
                    "Confirm",
                    textAlign = TextAlign.Center,
                ),
            color = kiwiColors.color5A,
            onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    // Settings re-uses this screen for "change apps". In that mode
                    // we save and slide back down to Settings — no map-entry curtain,
                    // no baseline reset (the baseline is fixed at signup).
                    val fromSettings =
                        navController.previousBackStackEntry?.destination?.route == ScreenRoutes.SETTINGS
                    if (!fromSettings) {
                        // App selection is the final sign-up step — raise the
                        // map-entry loading curtain now so it covers the save and
                        // the navigation into the map.
                        usersViewModel.setShowAppLoading(true)
                    }
                    personalityViewModel.onAppsChanged(
                        goodApps.map { it.packageName },
                        badApps.map { it.packageName },
                        neutralApps.map { it.packageName },
                    )
                    if (personalityViewModel.updateApps().isSuccess) {
                        if (fromSettings) {
                            navController.popBackStack()
                        } else {
                            firebaseLogEvent(FirebaseEventNames.SIGNUP_4_APPS_COMPLETED)
                            goalsViewModel.saveBaselineAppUsage(
                                goodApps.map { it.packageName },
                                badApps.map { it.packageName },
                            )
                            // Drive the fresh-account opening beat right here, under
                            // the loading curtain. The map's own LaunchedEffect was
                            // racing the backend's first-node provisioning on this
                            // path — by awaiting the round-trip before navigating,
                            // the map mounts with the first node already COMPLETED.
                            val executedNode = nodesViewModel.autoExecuteFirstNode(0)
                            // Auto-place one starter skill on the deck so the
                            // fresh account has something playable in combat on
                            // their very first map session. Awaited so the
                            // backend round-trip lands before HOME mounts.
                            skillsViewModel.equipStarterIfNeeded()
                            navController.navigate(ScreenRoutes.HOME)
                            onUpdateSuccess()
                            executedNode?.let { node ->
                                if (node.onExecutionEvent.isNotBlank() && node.onExecutionEvent != "_") {
                                    EventBus.emitEvent(
                                        EventType.valueOf(node.onExecutionEvent),
                                        EventPayload.EntityIdPayload(node.onExecutionEntityId),
                                    )
                                }
                            }
                        }
                    } else if (!fromSettings) {
                        usersViewModel.setShowAppLoading(false)
                    }
                }
            },
            enabled = !isLoading && goodApps.isNotEmpty() && badApps.isNotEmpty(),
            testTag = UsersTestTags.SIGNUP_BUTTON,
        )
    }
}

private fun updateApps(
    goodApps: SnapshotStateList<AppInfo>,
    badApps: SnapshotStateList<AppInfo>,
    neutralApps: SnapshotStateList<AppInfo>,
) {
    goodApps.sortBy { it.name.lowercase() }
    badApps.sortBy { it.name.lowercase() }
    neutralApps.sortBy { it.name.lowercase() }
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

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        ) {
            GridApps(
                title = "Neutral",
                apps = appLists.neutralApps,
                draggingApp = draggingApp,
                isLoading = isLoading,
                onPositionChanged = { app, rect -> appPositions[app] = rect },
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
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
        verticalArrangement = Arrangement.Center,
        modifier =
            Modifier
                .width(APP_CARD_WIDTH)
                .height(APP_CARD_HEIGHT)
                .offset { IntOffset(dragOffset.x.toInt(), dragOffset.y.toInt()) }
                .onGloballyPositioned { coordinates ->
                    onPositionChanged(coordinates.boundsInWindow())
                }.graphicsLayer {
                    alpha =
                        when {
                            isDragging -> DRAG_ALPHA
                            !enabled -> KIWI_DISABLED_ALPHA
                            else -> 1f
                        }
                    scaleX = if (isDragging) DRAG_SCALE_X else 1f
                    scaleY = if (isDragging) DRAG_SCALE_Y else 1f
                }.shadow(
                    elevation = if (isDragging) 10.dp else 4.dp,
                    shape = RoundedCornerShape(APP_CARD_CORNER),
                ).clip(RoundedCornerShape(APP_CARD_CORNER))
                .background(kiwiColors.color3)
                .border(
                    width = 1.dp,
                    color = kiwiColors.color2,
                    shape = RoundedCornerShape(APP_CARD_CORNER),
                ).padding(8.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .size(APP_ICON_CONTAINER_SIZE_DP)
                    .clip(CircleShape)
                    .background(kiwiColors.color1),
        ) {
            Kiwi_Image(
                painter = rememberDrawablePainter(app.icon),
                alt = "app icon",
                modifier = Modifier.size(APP_ICON_SIZE_DP),
            )
        }

        Kiwi_Spacer(Spacing.xSmall)

        Text(
            text = app.name,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Medium,
            color = kiwiColors.color6,
            modifier = Modifier.fillMaxWidth(),
        )
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
                .shadow(6.dp, RoundedCornerShape(APP_SECTION_CORNER))
                .clip(RoundedCornerShape(APP_SECTION_CORNER))
                .background(kiwiColors.color1)
                .border(
                    width = 1.dp,
                    color = kiwiColors.color2,
                    shape = RoundedCornerShape(APP_SECTION_CORNER),
                ).padding(16.dp),
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            contentPadding = PaddingValues(vertical = 4.dp),
        ) {
            items(items = apps, key = { it.packageName }) { app ->
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
                .shadow(6.dp, RoundedCornerShape(APP_SECTION_CORNER))
                .clip(RoundedCornerShape(APP_SECTION_CORNER))
                .background(kiwiColors.color1)
                .border(
                    width = 1.dp,
                    color = kiwiColors.color2,
                    shape = RoundedCornerShape(APP_SECTION_CORNER),
                ).padding(16.dp),
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
            columns = GridCells.Fixed(NEUTRAL_APPS_GRID_SIZE),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(items = apps, key = { it.packageName }) { app ->
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
    onDragStarted: () -> Unit,
) {
    detectDragGesturesAfterLongPress(
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
                    dragState.dragItemStartPosition.value =
                        appPositions[appUnderFinger]?.topLeft ?: Offset.Zero
                    dragState.dragOffset.value = Offset.Zero
                    onDragStarted()
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

                updateApps(
                    appLists.goodApps,
                    appLists.badApps,
                    appLists.neutralApps,
                )
            }

            dragState.draggingApp.value = null
            dragState.dragOffset.value = Offset.Zero
        },
        onDragCancel = {
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

@SuppressLint("ViewModelConstructorInComposable")
@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun SignUpScreen4_Apps_Preview() {
    Kiwi_Theme {
        SignUpScreen4_Apps(
            usersViewModel = UsersFakeViewModel(UsersState("", "", "")),
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
            goalsViewModel = GoalsFakeViewModel(),
            nodesViewModel = NodesFakeViewModel(),
            skillsViewModel = SkillsFakeViewModel(),
        )
    }
}
