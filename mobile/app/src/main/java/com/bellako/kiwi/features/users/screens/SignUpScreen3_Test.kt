package com.bellako.kiwi.features.users.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.LoadingModal
import com.bellako.kiwi.common.screens.modals.ErrorModalScreen
import com.bellako.kiwi.common.tests.CommonTestTags
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
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(getResponsiveSizeHeight(Spacing.medium))
                    .testTag(CommonTestTags.USERS_SCREEN),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val kiwiColors = LocalKiwiColors.current
            var currentQuestion by remember { mutableIntStateOf(currentPersonalityState.currentQuestion) }

            val totalQuestions = currentPersonalityState.questions.size
            val progress by remember(currentQuestion, totalQuestions) {
                derivedStateOf { (currentQuestion + 1).toFloat() / totalQuestions.toFloat() }
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = getResponsiveSizeHeight(Spacing.medium))
                        .testTag("questionnaire_progress_bar"),
                color = kiwiColors.color3A,
                trackColor = kiwiColors.color3A.copy(alpha = 0.25f),
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )

            Kiwi_H2(
                KiwiTextArguments(
                    currentPersonalityState.questions[currentQuestion].question,
                    textAlign = TextAlign.Center,
                    color = kiwiColors.color6,
                ),
            )

            Kiwi_Spacer(Spacing.large)

            currentPersonalityState.questions[currentQuestion].options.forEachIndexed { index, option ->

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

                Kiwi_Spacer()
            }
        }

        if (isLoading || isPreview) {
            LoadingModal()
        }
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

    var buildVisible by remember { mutableStateOf(false) }
    var skillsVisible by remember { mutableStateOf(false) }
    var buttonVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (personalityViewModel.updateBuild().isSuccess) {
            firebaseLogEvent(FirebaseEventNames.SIGNUP_3_TEST_COMPLETED)
            skillsViewModel.loadSkills()
        }
    }

    LaunchedEffect(Unit) {
        delay(100)
        buildVisible = true
    }

    LaunchedEffect(skills.isNotEmpty()) {
        if (skills.isEmpty()) return@LaunchedEffect
        delay(900)
        skillsVisible = true
        delay(600)
        buttonVisible = true
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
                    Modifier.padding(
                        top = getResponsiveSizeHeight(Spacing.medium),
                        bottom = getResponsiveSizeHeight(Spacing.small),
                    ),
            ),
        )

        AnimatedVisibility(
            visible = buildVisible,
            enter = fadeIn(animationSpec = tween(700)),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
        }

        skills.chunked(2).forEachIndexed { rowIndex, rowSkills ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.small)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                rowSkills.forEach { skill ->
                    AnimatedVisibility(
                        visible = skillsVisible,
                        enter = fadeIn(animationSpec = tween(600)),
                        modifier = Modifier.weight(1f),
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

        AnimatedVisibility(
            visible = buttonVisible,
            enter = fadeIn(animationSpec = tween(600)),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                    enabled = !personalityIsLoading && !skillsIsLoading,
                    onClick = {
                        navController.navigate(ScreenRoutes.SIGNUP4_APPS)
                    },
                )
            }
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
