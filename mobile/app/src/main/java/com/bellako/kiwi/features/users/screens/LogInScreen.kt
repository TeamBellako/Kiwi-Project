package com.bellako.kiwi.features.users.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.common.data.ScreenRoutes
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.screens.components.KiwiAnnotatedStringArguments
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_AnnotatedString_P1
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_H1
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_InfoBox
import com.bellako.kiwi.common.screens.components.Kiwi_InputField
import com.bellako.kiwi.common.screens.components.Kiwi_Label2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.LoadingModal
import com.bellako.kiwi.common.screens.modals.ErrorModalScreen
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.model.IUsersViewModel
import com.bellako.kiwi.features.users.tests.UsersFakeViewModel
import com.bellako.kiwi.features.users.tests.UsersTestFactory.validUsersDTO
import com.bellako.kiwi.features.users.tests.UsersTestTags
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Time for the background to scroll by one full image width — i.e. one
// seamless loop. Slow enough to read as an ambient drift, not obvious motion.
private const val LOGIN_SCROLL_PERIOD_MS = 64000

// Instrumentation tests provide `false` so the infinite scroll transition
// doesn't keep the Compose runtime permanently non-idle (which hangs
// performClick / waitForIdle on CI).
internal val LocalLoginBackgroundAnimated = staticCompositionLocalOf { true }

@Composable
fun LogInScreen(
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val kiwiColors = LocalKiwiColors.current

    val usersState by usersViewModel.state.collectAsState()
    val usersUiState by usersViewModel.uiState.collectAsState()

    @Suppress("MagicNumber")
    val imgPercentage = 0.46f

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(kiwiColors.color2),
        contentAlignment = Alignment.Center,
    ) {
        when (usersUiState) {
            is UIState.GeneralError -> {
                ErrorModalScreen(onButtonClick = {
                    CoroutineScope(Dispatchers.Main).launch {
                        usersViewModel.clearLocalCredentials(context)
                        usersViewModel.resetUiState()
                    }
                })
            }
            else -> {
                ScrollingLoginBackground(imgPercentage)

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(imgPercentage)
                            .align(Alignment.TopStart)
                            .background(
                                Brush.verticalGradient(
                                    colors =
                                        listOf(
                                            Color.Transparent,
                                            Color.Transparent,
                                            kiwiColors.color2,
                                            kiwiColors.color2,
                                        ),
                                    startY = 0f,
                                    endY = Float.POSITIVE_INFINITY,
                                ),
                            ),
                )

                LogInLayout(
                    context = context,
                    isPreview = isPreview,
                    usersViewModel = usersViewModel,
                    usersState = usersState,
                    usersUiState = usersUiState,
                    personalityViewModel = personalityViewModel,
                    navController = navController,
                )
            }
        }
    }
}

/**
 * The login background, scrolling endlessly to the right. Two copies of the
 * image are laid edge to edge and shifted together by exactly one image width
 * per loop, so a fresh copy always slides in from the left to replace the one
 * leaving on the right — making the seam invisible. The shift is read only
 * inside [graphicsLayer], so it runs on the draw phase without recomposition.
 */
@Composable
private fun BoxScope.ScrollingLoginBackground(imgPercentage: Float) {
    val painter = painterResource(R.drawable.login_bg)

    // User-driven horizontal pan, accumulated freely. The background loops,
    // so we never clamp — any value wraps via the modulo below. Drag delta
    // is added directly: dragging the finger right shifts the image right
    // (revealing what was off-screen on the left).
    var userScrollPx by remember { mutableFloatStateOf(0f) }
    val scrollState =
        rememberScrollableState { delta ->
            userScrollPx += delta
            delta
        }

    BoxWithConstraints(
        modifier =
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(imgPercentage)
                .align(Alignment.TopStart)
                .clipToBounds()
                // Pan via touch (and fling) on top of the ambient parallax.
                // Scoped to this band so it can't steal drags from the form
                // underneath.
                .scrollable(
                    state = scrollState,
                    orientation = Orientation.Horizontal,
                ),
    ) {
        // The image is scaled to fill the band's height, so its on-screen
        // width follows from its aspect ratio — that width is one scroll loop.
        val intrinsic = painter.intrinsicSize
        val heightPx = with(LocalDensity.current) { maxHeight.toPx() }
        val imageWidthPx = if (intrinsic.height > 0f) intrinsic.width * heightPx / intrinsic.height else 0f
        val imageWidthDp = with(LocalDensity.current) { imageWidthPx.toDp() }

        val animatedShift =
            if (LocalLoginBackgroundAnimated.current) {
                val transition = rememberInfiniteTransition(label = "login_scroll")
                val progress by transition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec =
                        infiniteRepeatable(
                            animation = tween(LOGIN_SCROLL_PERIOD_MS, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart,
                        ),
                    label = "login_scroll_progress",
                )
                // Negative so the ambient parallax drifts left instead of
                // right; the modulo wrap below normalises the sign so the
                // two-image seam trick is unaffected.
                -progress * imageWidthPx
            } else {
                0f
            }

        // Combined offset wrapped into [0, imageWidthPx) so the two-image
        // trick keeps hiding the seam regardless of how far the user has
        // dragged in either direction. Positive `% ` results aren't enough
        // — Kotlin's `%` keeps the sign of the dividend, so we lift any
        // negative remainder back into range.
        val totalShift =
            if (imageWidthPx > 0f) {
                val raw = (animatedShift + userScrollPx) % imageWidthPx
                if (raw < 0f) raw + imageWidthPx else raw
            } else {
                0f
            }

        // Trailing copy sits one width to the left; leading copy starts on
        // screen. Both slide right by `totalShift`; at totalShift == imageWidth
        // the trailing copy lands exactly where the leading one began.
        LoginBackgroundImage(imageWidthDp, translationXPx = totalShift - imageWidthPx)
        LoginBackgroundImage(imageWidthDp, translationXPx = totalShift)
    }
}

@Composable
private fun LoginBackgroundImage(
    width: Dp,
    translationXPx: Float,
) {
    Kiwi_Image(
        R.drawable.login_bg,
        "Login Background",
        modifier =
            Modifier
                .requiredWidth(width)
                .fillMaxHeight()
                .graphicsLayer { translationX = translationXPx },
        contentScale = ContentScale.FillHeight,
        alignment = Alignment.TopStart,
    )
}

@Composable
private fun LogInLayout(
    context: Context,
    isPreview: Boolean,
    usersViewModel: IUsersViewModel,
    usersState: UsersState?,
    usersUiState: UIState<Unit>,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController,
) {
    val usersIsLoading by usersViewModel.isLoading.collectAsState()
    val personalityIsLoading by personalityViewModel.isLoading.collectAsState()
    var initializing by remember { mutableStateOf(true) }
    var localLoading by remember { mutableStateOf(false) }
    val isLoading by remember { derivedStateOf { initializing || localLoading || usersIsLoading || personalityIsLoading } }

    // check stored credentials for auto login
    LaunchedEffect(Unit) {
        initializing = false
        val isColdStart = !usersViewModel.hasAttemptedAutoLogin()
        usersViewModel.markAutoLoginAttempted()

        val (username, password) = usersViewModel.getLocalCredentials(context)
        if (!username.isNullOrBlank() && !password.isNullOrBlank()) {
            usersViewModel.onEmailChanged(username)
            usersViewModel.onPasswordChanged(password)
            localLoading = performLogin(context, usersViewModel, personalityViewModel, navController)
        } else if (isColdStart && !isPreview) {
            // Cold-start with no stored credentials — drop the user into the
            // sign-up welcome instead of the bare login form. Logout and
            // manual back-nav from sign-up don't trigger this (the flag is
            // already set), so those still land on the login form. The
            // preview is excluded so it can render the login form.
            navController.navigate(ScreenRoutes.SIGNUP1_WELCOME) {
                popUpTo(ScreenRoutes.LOGIN) { inclusive = true }
            }
        }
    }

    usersState?.let { currentState ->

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(getResponsiveSizeHeight(Spacing.large)),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (isLoading || isPreview) {
                LoadingModal()
            }

            LogInForm(
                context = context,
                isLoading = isLoading,
                isPreview = isPreview,
                usersViewModel = usersViewModel,
                usersState = usersState,
                usersUiState = usersUiState,
                personalityViewModel = personalityViewModel,
                navController = navController,
                onLoginSuccess = {
                    localLoading = true
                },
            )
        }

        GoToSignUp(
            isLoading = isLoading,
            isPreview = isPreview,
            navController = navController,
        )
    }
}

@Composable
private fun LogInForm(
    context: Context,
    isLoading: Boolean,
    isPreview: Boolean,
    usersViewModel: IUsersViewModel,
    usersState: UsersState,
    usersUiState: UIState<Unit>,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController,
    onLoginSuccess: (() -> Unit),
) {
    val kiwiColors = LocalKiwiColors.current

    Column(
        modifier =
            Modifier
                .offset(y = getResponsiveSizeHeight(Spacing.xLarge))
                .alpha(if (!isLoading || isPreview) 1f else 0f),
    ) {
        WelcomeText()

        Kiwi_Spacer()

        Kiwi_InputField(
            enabled = !isLoading,
            value = usersState.email,
            onValueChange = { usersViewModel.onEmailChanged(it) },
            label = {
                Kiwi_Label2(
                    KiwiTextArguments(
                        "Email",
                        color = kiwiColors.color7B,
                    ),
                )
            },
            keyboardType = KeyboardType.Email,
            textColor = kiwiColors.color7B,
            color = kiwiColors.color3A,
            testTag = UsersTestTags.EMAIL_FIELD,
        )

        Kiwi_Spacer()

        Kiwi_InputField(
            enabled = !isLoading,
            value = usersState.password,
            onValueChange = { usersViewModel.onPasswordChanged(it) },
            label = {
                Kiwi_Label2(
                    KiwiTextArguments(
                        "Password",
                        color = kiwiColors.color7B,
                    ),
                )
            },
            keyboardType = KeyboardType.Password,
            textColor = kiwiColors.color7B,
            color = kiwiColors.color3A,
            testTag = UsersTestTags.PASSWORD_FIELD,
        )

        Kiwi_Spacer()

        Kiwi_FixedSizeButton(
            textArguments =
                KiwiTextArguments(
                    "LOG IN",
                    color = kiwiColors.colorF,
                    fontWeight = FontWeight.Bold,
                ),
            color = kiwiColors.color5,
            onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    val success = performLogin(context, usersViewModel, personalityViewModel, navController)
                    if (success) {
                        onLoginSuccess()
                    }
                }
            },
            enabled = !isLoading,
            testTag = UsersTestTags.LOGIN_BUTTON,
        )

        Kiwi_Spacer()

        LogInErrorMessage(usersUiState)
    }
}

@Composable
private fun WelcomeText() {
    val kiwiColors = LocalKiwiColors.current

    Kiwi_H1(
        KiwiTextArguments(
            "Welcome Back,\nKnight",
            TextAlign.Center,
            color = kiwiColors.colorF,
            fontWeight = FontWeight.Bold,
            modifier =
                Modifier
                    .fillMaxWidth(),
        ),
    )
}

@Composable
private fun LogInErrorMessage(usersUiState: UIState<Unit>) {
    val kiwiColors = LocalKiwiColors.current
    var errorMessage by remember { mutableStateOf("") }
    errorMessage =
        when (usersUiState) {
            is UIState.Error -> {
                usersUiState.message
            } else -> {
                ""
            }
        }

    Box(modifier = Modifier.alpha(if (errorMessage.isEmpty()) 0f else 1f)) {
        Kiwi_InfoBox(
            message = errorMessage,
            color = kiwiColors.colorR,
            testTag = UsersTestTags.ERROR_TEXT,
        )
    }
}

@Composable
private fun GoToSignUp(
    isLoading: Boolean,
    isPreview: Boolean,
    navController: NavController,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(bottom = getResponsiveSizeHeight(Spacing.xLarge))
                .alpha(if (!isLoading || isPreview) 1f else 0f),
        contentAlignment = Alignment.BottomCenter,
    ) {
        SignUp {
            navController.navigate(ScreenRoutes.SIGNUP1_WELCOME)
        }
    }
}

private suspend fun performLogin(
    context: Context,
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController,
): Boolean {
    // Raise the map-entry loading curtain up front so it fully covers the
    // login network call and the navigation. It is lowered again for any
    // outcome that does NOT land on the map (an unfinished sign-up, a failure).
    usersViewModel.setShowAppLoading(true)
    if (usersViewModel.login(context).isSuccess) {
        // check personality registered and configured
        // navigate to Home or to the corresponding personality test if anything missing
        personalityViewModel.loadPersonality().fold(
            onSuccess = {
                val personality = personalityViewModel.state.value
                val needsApps =
                    personality?.goodApps.isNullOrEmpty() && personality?.badApps.isNullOrEmpty()
                when {
                    personality?.build == "" -> {
                        usersViewModel.setShowAppLoading(false)
                        navController.navigate(ScreenRoutes.SIGNUP3_TEST)
                    }
                    needsApps -> {
                        usersViewModel.setShowAppLoading(false)
                        navController.navigate(ScreenRoutes.SIGNUP4_APPS)
                    }
                    else -> navController.navigate(ScreenRoutes.HOME)
                }
            },
            onFailure = {
                usersViewModel.setShowAppLoading(false)
                navController.navigate(ScreenRoutes.SIGNUP3_TEST)
            },
        )
        return true
    }
    usersViewModel.setShowAppLoading(false)
    return false
}

@Composable
private fun SignUp(onSignUp: () -> Unit) {
    val kiwiColors = LocalKiwiColors.current

    val annotatedString =
        buildAnnotatedString {
            withStyle(
                style =
                    SpanStyle(
                        color = kiwiColors.color6,
                    ),
            ) {
                append("First Time? \nStart Your Adventure ")
            }

            withLink(
                link =
                    LinkAnnotation.Clickable(
                        tag = "HERE",
                        linkInteractionListener = {
                            onSignUp()
                        },
                    ),
            ) {
                withStyle(
                    style =
                        SpanStyle(
                            color = kiwiColors.color7B,
                            textDecoration = TextDecoration.Underline,
                        ),
                ) {
                    append("Here")
                }
            }
        }

    Kiwi_AnnotatedString_P1(
        KiwiAnnotatedStringArguments(
            annotatedString,
            TextAlign.Center,
        ),
    )
}

// -------------------------------------------------------------------------------------------------

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun LogInScreen_Preview() {
    Kiwi_Theme {
        LogInScreen(
            usersViewModel =
                UsersFakeViewModel(
                    UsersState(
                        validUsersDTO().email,
                        validUsersDTO().password,
                        validUsersDTO().registerDate,
                    ),
                ),
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
