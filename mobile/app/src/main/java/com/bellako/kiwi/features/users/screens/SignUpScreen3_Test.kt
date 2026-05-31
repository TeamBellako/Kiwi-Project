package com.bellako.kiwi.features.users.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.analytics.FirebaseEventNames
import com.bellako.kiwi.analytics.firebaseLogEvent
import com.bellako.kiwi.common.data.ScreenRoutes
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_H1
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Label1
import com.bellako.kiwi.common.screens.components.Kiwi_Label3
import com.bellako.kiwi.common.screens.components.Kiwi_P1
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.LoadingModal
import com.bellako.kiwi.common.screens.modals.ErrorModalScreen
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.features.nodes.screens.LocalNodeEntryTransition
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.model.ISkillsViewModel
import com.bellako.kiwi.features.skills.screen.SkillBackground
import com.bellako.kiwi.features.skills.screen.skillStatusColor
import com.bellako.kiwi.features.skills.screen.skillStatusText
import com.bellako.kiwi.features.skills.tests.SkillsFakeViewModel
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.model.IUsersViewModel
import com.bellako.kiwi.features.users.tests.UsersFakeViewModel
import com.bellako.kiwi.features.users.tests.UsersTestFactory.validUsersDTO
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlin.math.roundToInt

// Staggered scale-pop for the questionnaire options, mirroring the conversation
// options panel (ConversationScreen): each option scales up with an EaseOutBack
// overshoot, OPTION_STAGGER_MS apart.
private const val OPTION_POP_MS = 450
private const val OPTION_STAGGER_MS = 140

// Progress-bar styling, matched to the login loading bar (LoginLoadingScreen)
// for a consistent look: color6 bar over a faint track, inside a translucent
// rounded pill.
private const val PROGRESS_TRACK_ALPHA = 0.25f
private const val PROGRESS_BG_ALPHA = 0.7f

// Fixed slots so the question and answers keep their positions no matter how
// many lines each text wraps to — the content is centered within these, so a
// 1-line and a 3-line question occupy the same space and the answers below
// never shift between questions. Bump these if a longer question/answer clips.
private val QUESTION_AREA_HEIGHT = 120.dp
private val OPTION_SLOT_HEIGHT = 72.dp

// Pushes the progress bar down from the very top of the screen.
private val PROGRESS_BAR_TOP_INSET = 24.dp

// Build-reveal animation (BuildModal). Text elements fade in; the skills pop
// with a scaled EaseOutBack overshoot, staggered like the combat deck intro
// (SKILL_* mirror CombatIntro's values). The reveal is held until the skills
// have loaded so the final layout is already in place — every element then
// fades/pops in from its final position without anything shifting.
private const val BUILD_FADE_MS = 700
private const val BUILD_TITLE_GAP_MS = 150L
private const val SKILLS_SETTLE_MS = 80L
private const val SKILLS_BEFORE_POP_MS = 250L
private const val SKILL_POP_MS = 400
private const val SKILL_STAGGER_MS = 150
private const val SKILL_MIN_SCALE = 0.5f
private const val BUILD_BUTTON_GAP_MS = 300L
private const val BUILD_BUTTON_FADE_MS = 600

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
@Composable
fun SignUpScreen3_Test(
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    skillsViewModel: ISkillsViewModel,
    navController: NavController,
) {
    SignUpScreen {
        Question(
            usersViewModel,
            personalityViewModel,
            skillsViewModel,
            navController,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun Question(
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    skillsViewModel: ISkillsViewModel,
    navController: NavController,
) {
    val isPreview = LocalInspectionMode.current

    val usersUiState by usersViewModel.uiState.collectAsState()

    val personalityState by personalityViewModel.state.collectAsState()
    val personalityUiState by personalityViewModel.uiState.collectAsState()
    val personalityIsLoading by personalityViewModel.isLoading.collectAsState()

    val skillsIsLoading by skillsViewModel.isLoading.collectAsState()

    var localLoading by remember { mutableStateOf(false) }
    val isLoading by remember { derivedStateOf { localLoading || personalityIsLoading || skillsIsLoading } }

    personalityState?.let { currentPersonalityState ->
        if (usersUiState == UIState.GeneralError || personalityUiState == UIState.GeneralError) {
            ErrorModalScreen(onButtonClick = {
                usersViewModel.resetUiState()
                personalityViewModel.resetUiState()
            })
        } else {
            Options(personalityViewModel, skillsViewModel, navController, currentPersonalityState, isLoading, isPreview)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun Options(
    personalityViewModel: IPersonalityViewModel,
    skillsViewModel: ISkillsViewModel,
    navController: NavController,
    currentPersonalityState: PersonalityState,
    isLoading: Boolean,
    isPreview: Boolean,
) {
    var shouldShowBuildModal by remember { mutableStateOf(false) }

    if (shouldShowBuildModal) {
        BuildModal(personalityViewModel, skillsViewModel, navController)
    } else {
        val kiwiColors = LocalKiwiColors.current
        val nodeEntry = LocalNodeEntryTransition.current
        var currentQuestion by remember { mutableIntStateOf(currentPersonalityState.currentQuestion) }

        val totalQuestions = currentPersonalityState.questions.size
        val progress by remember(currentQuestion, totalQuestions) {
            derivedStateOf { (currentQuestion + 1).toFloat() / totalQuestions.toFloat() }
        }

        val options = currentPersonalityState.questions[currentQuestion].options

        // Lift the step veil once we've arrived on the questionnaire (no-op when
        // we got here without one — e.g. resuming sign-up from login).
        LaunchedEffect(Unit) { nodeEntry?.fadeOut() }

        // Drives the staggered scale-pop of the options. Restarts whenever the
        // question changes so each new set pops in; on the first question we
        // hold until the entry veil has lifted so the pops aren't wasted behind
        // it.
        val optionsClock = remember { Animatable(0f) }
        LaunchedEffect(currentQuestion) {
            optionsClock.snapTo(0f)
            if (nodeEntry != null) {
                snapshotFlow { nodeEntry.veilAlpha }.first { it <= 0f }
            }
            val total = OPTION_POP_MS + (options.size - 1).coerceAtLeast(0) * OPTION_STAGGER_MS
            optionsClock.animateTo(total.toFloat(), tween(total, easing = LinearEasing))
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(getResponsiveSizeHeight(Spacing.medium))
                    .testTag(CommonTestTags.USERS_SCREEN),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // (2) Progress bar near the top, nudged down a little from the edge.
            Kiwi_Spacer(PROGRESS_BAR_TOP_INSET)
            QuestionnaireProgressBar(progress = progress)

            Kiwi_Spacer(Spacing.xLarge)

            // (3) Fixed-height slots keep the question and answers in place no
            // matter how many lines their text wraps to, so nothing shifts as
            // the player moves between questions.
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(getResponsiveSizeHeight(QUESTION_AREA_HEIGHT)),
                contentAlignment = Alignment.Center,
            ) {
                Kiwi_H2(
                    KiwiTextArguments(
                        currentPersonalityState.questions[currentQuestion].question,
                        textAlign = TextAlign.Center,
                        color = kiwiColors.color6,
                    ),
                )
            }

            Kiwi_Spacer(Spacing.large)

            options.forEachIndexed { index, option ->
                val itemStart = index * OPTION_STAGGER_MS
                val rawP = ((optionsClock.value - itemStart) / OPTION_POP_MS).coerceIn(0f, 1f)
                val popScale = if (rawP <= 0f) 0f else EaseOutBack.transform(rawP).coerceAtLeast(0f)

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(getResponsiveSizeHeight(OPTION_SLOT_HEIGHT)),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(modifier = Modifier.scale(popScale)) {
                        Kiwi_FixedSizeButton(
                            textArguments =
                                KiwiTextArguments(
                                    option,
                                    color = kiwiColors.color6,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(8.dp),
                                ),
                            color = kiwiColors.color3A,
                            onClick = {
                                currentPersonalityState.answers[currentQuestion] = index

                                firebaseLogEvent(
                                    FirebaseEventNames.PERSONALIZATION_QUESTION_ANSWERED,
                                    mapOf(
                                        "question" to currentPersonalityState.questions[currentQuestion].question,
                                        "answer" to currentPersonalityState.questions[currentQuestion].options[index],
                                    ),
                                )

                                if (currentQuestion + 1 < currentPersonalityState.questions.size) {
                                    ++currentQuestion
                                } else {
                                    shouldShowBuildModal = true
                                }
                            },
                            enabled = !isLoading,
                        )
                    }
                }

                Kiwi_Spacer()
            }
        }

        if (isLoading || isPreview) {
            LoadingModal()
        }
    }
}

// Progress bar styled to match the login loading bar (LoginLoadingScreen): a
// color6 indicator over a faint track with a percentage readout, inside a
// translucent rounded pill.
@Composable
private fun QuestionnaireProgressBar(progress: Float) {
    val kiwiColors = LocalKiwiColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    color = kiwiColors.color2.copy(alpha = PROGRESS_BG_ALPHA),
                    shape = RoundedCornerShape(getResponsiveSizeHeight(Spacing.small)),
                ).padding(
                    horizontal = getResponsiveSizeHeight(Spacing.medium),
                    vertical = getResponsiveSizeHeight(Spacing.small),
                ),
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier =
                Modifier
                    .weight(1f)
                    .testTag("questionnaire_progress_bar"),
            color = kiwiColors.color6,
            trackColor = kiwiColors.color6.copy(alpha = PROGRESS_TRACK_ALPHA),
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
        )
        Kiwi_P2(
            KiwiTextArguments(
                "${(progress * 100).roundToInt()}%",
                textAlign = TextAlign.Center,
                color = kiwiColors.color6,
                modifier = Modifier.padding(start = Spacing.medium),
            ),
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Suppress("MagicNumber")
private fun BuildModal(
    personalityViewModel: IPersonalityViewModel,
    skillsViewModel: ISkillsViewModel,
    navController: NavController,
) {
    val personalityState by personalityViewModel.state.collectAsState()
    val skillsState by skillsViewModel.state.collectAsState()
    val personalityIsLoading by personalityViewModel.isLoading.collectAsState()
    val skillsIsLoading by skillsViewModel.isLoading.collectAsState()

    val skills = skillsState?.allSkills ?: emptyList()

    val nodeEntry = LocalNodeEntryTransition.current
    val veilScope = rememberCoroutineScope()

    // Reveal drivers. Held at 0 (invisible) until the skills load so the layout
    // is final before anything animates; then text fades and skills pop in.
    val titleAlpha = remember { Animatable(0f) }
    val buildAlpha = remember { Animatable(0f) }
    val skillClock = remember { Animatable(0f) }
    val buttonAlpha = remember { Animatable(0f) }

    // Settings re-uses this screen to let the user retake the personality
    // test. In that mode we stop after the build is shown — no apps step.
    val fromSettings =
        remember {
            navController.previousBackStackEntry?.destination?.route == ScreenRoutes.SETTINGS
        }

    LaunchedEffect(Unit) {
        if (personalityViewModel.updateBuild().isSuccess) {
            firebaseLogEvent(FirebaseEventNames.SIGNUP_3_TEST_COMPLETED)
            // Re-fetching personality is the trigger for the backend to
            // reconcile the user's skills against the new build (drops the
            // old build's starter set, grants the new build's, keeps the
            // rest). Has to happen before loadSkills() so the skills GET
            // returns the post-reconciliation state.
            personalityViewModel.loadPersonality()
            skillsViewModel.loadSkills()
        }
    }

    // Orchestrate the whole reveal once the skills are in (loadSkills runs after
    // loadPersonality above, so by now the build name is set too). Waiting means
    // the skill rows are already laid out — the reveal plays over a fixed layout
    // so nothing reflows as elements appear.
    LaunchedEffect(skills.isNotEmpty()) {
        if (skills.isEmpty()) return@LaunchedEffect
        delay(SKILLS_SETTLE_MS)
        titleAlpha.animateTo(1f, tween(BUILD_FADE_MS, easing = LinearEasing))
        delay(BUILD_TITLE_GAP_MS)
        buildAlpha.animateTo(1f, tween(BUILD_FADE_MS, easing = LinearEasing))
        delay(SKILLS_BEFORE_POP_MS)
        val total = SKILL_POP_MS + (skills.size - 1).coerceAtLeast(0) * SKILL_STAGGER_MS
        skillClock.animateTo(total.toFloat(), tween(total, easing = LinearEasing))
        delay(BUILD_BUTTON_GAP_MS)
        buttonAlpha.animateTo(1f, tween(BUILD_BUTTON_FADE_MS, easing = LinearEasing))
    }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = getResponsiveSizeHeight(24.dp))
                .padding(horizontal = getResponsiveSizeHeight(24.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val kiwiColors = LocalKiwiColors.current

        Kiwi_H1(
            KiwiTextArguments(
                "Your initial build is...",
                TextAlign.Center,
                modifier =
                    Modifier
                        .alpha(titleAlpha.value)
                        .padding(
                            top = getResponsiveSizeHeight(Spacing.medium),
                            bottom = getResponsiveSizeHeight(Spacing.small),
                        ),
            ),
        )

        Column(
            modifier = Modifier.alpha(buildAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Kiwi_H2(
                KiwiTextArguments(
                    personalityState?.build ?: "",
                    TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier =
                        Modifier.padding(
                            top = getResponsiveSizeHeight(Spacing.medium),
                            bottom = getResponsiveSizeHeight(Spacing.small),
                        ),
                ),
            )

            Kiwi_Spacer()

            Kiwi_P1(
                KiwiTextArguments(
                    text = "And these are your initial skills:",
                    TextAlign.Center,
                    modifier =
                        Modifier.padding(
                            top = getResponsiveSizeHeight(Spacing.medium),
                            bottom = getResponsiveSizeHeight(Spacing.small),
                        ),
                ),
            )
        }

        skills.chunked(2).forEachIndexed { rowIndex, rowSkills ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.small)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                rowSkills.forEachIndexed { colIndex, skill ->
                    // Flat index across the 2-wide grid drives the stagger.
                    val flatIndex = rowIndex * 2 + colIndex
                    val itemStart = flatIndex * SKILL_STAGGER_MS
                    val rawP = ((skillClock.value - itemStart) / SKILL_POP_MS).coerceIn(0f, 1f)
                    val curve = if (rawP <= 0f) 0f else EaseOutBack.transform(rawP).coerceAtLeast(0f)
                    // Scale never collapses past SKILL_MIN_SCALE so the slot's
                    // layout bounds stay put; alpha (rawP) does the hiding.
                    val popScale = SKILL_MIN_SCALE + (1f - SKILL_MIN_SCALE) * curve

                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .scale(popScale)
                                .alpha(rawP),
                    ) {
                        OnboardingSkillItem(skill)
                    }
                }
                if (rowSkills.size == 1) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
            Kiwi_Spacer(Spacing.small)
        }

        Column(
            modifier = Modifier.alpha(buttonAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Kiwi_Spacer()
            Kiwi_FixedSizeButton(
                textArguments =
                    KiwiTextArguments(
                        "Get Started",
                        color = kiwiColors.color6,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp),
                    ),
                color = kiwiColors.color3A,
                // Gate taps until the button has actually faded in — it occupies
                // its slot from the start (alpha 0), so without this the user
                // could blind-tap it early.
                enabled = !personalityIsLoading && !skillsIsLoading && buttonAlpha.value > 0.99f,
                onClick = {
                    if (fromSettings) {
                        navController.popBackStack()
                    } else {
                        // Veil the build → app-selection step change.
                        signupVeilNavigate(nodeEntry, veilScope, navController, ScreenRoutes.SIGNUP4_APPS)
                    }
                },
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun OnboardingSkillItem(skill: SkillDomain) {
    val kiwiColors = LocalKiwiColors.current

    Box(modifier = Modifier.fillMaxWidth()) {
        SkillBackground(skill, 0f)

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.padding(start = getResponsiveSizeHeight(20.dp))) {
                Kiwi_Image(
                    skill.icon,
                    "Skill Icon",
                    modifier = Modifier.size(getResponsiveSizeHeight(40.dp)),
                )
            }

            Column(modifier = Modifier.padding(end = getResponsiveSizeHeight(15.dp))) {
                Kiwi_Label1(
                    KiwiTextArguments(
                        text = skill.name,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ),
                )
                Kiwi_Label3(
                    KiwiTextArguments(
                        color = skillStatusColor(kiwiColors, skill.isCooldown),
                        text = skillStatusText(skill.isCooldown),
                        italic = true,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ),
                )
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SignUpScreen3_Test_Preview() {
    Kiwi_Theme {
        SignUpScreen3_Test(
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
            skillsViewModel = SkillsFakeViewModel(),
            navController = rememberNavController(),
        )
    }
}
