package com.bellako.kiwi.features.users.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.screens.ScreenRoutes
import com.bellako.kiwi.common.screens.components.KiwiH2
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Button
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.modals.ErrorModal
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.model.IUsersViewModel
import com.bellako.kiwi.features.users.tests.UsersFakeViewModel
import com.bellako.kiwi.features.users.tests.UsersTestFactory.validUsersDTO
import com.bellako.kiwi.ui.KiwiTheme
import com.bellako.kiwi.ui.Spacing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen3_Test(
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController,
) {
    SignUpScreen {
        Question(
            usersViewModel,
            personalityViewModel,
            navController,
        )
    }
}

@Composable
private fun Question(
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController,
) {
    val uiState by usersViewModel.uiState.collectAsState()
    val personalityState by personalityViewModel.state.collectAsState()

    personalityState?.let { currentPersonalityState ->

        when (uiState) {
            is UIState.GeneralError -> {
                ErrorModal(onRetry = {
                    CoroutineScope(Dispatchers.Main).launch {
                        if (personalityViewModel.updateBuild().isSuccess) {
                            navController.navigate(ScreenRoutes.HOME)
                        }
                    }
                })
            }

            else -> {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .testTag(CommonTestTags.USERS_SCREEN),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    var currentQuestion by remember { mutableIntStateOf(currentPersonalityState.currentQuestion) }

                    KiwiH2(
                        KiwiTextArguments(
                            currentPersonalityState.questions[currentQuestion].question,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary,
                        ),
                    )

                    Kiwi_Spacer(Spacing.large)

                    currentPersonalityState.questions[currentQuestion].options.forEachIndexed { index, option ->

                        Kiwi_Button(
                            KiwiTextArguments(
                                option,
                                color = MaterialTheme.colorScheme.secondary,
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            onClick = {
                                currentPersonalityState.answers[currentQuestion] = index
                                if (currentQuestion + 1 < currentPersonalityState.questions.size) {
                                    ++currentQuestion
                                } else {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        if (personalityViewModel.updateBuild().isSuccess) {
                                            navController.navigate(ScreenRoutes.SIGNUP4_APPS)
                                        }
                                    }
                                }
                            },
                        )

                        Kiwi_Spacer()
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun SignUpScreen3_TestPreview() {
    KiwiTheme {
        SignUpScreen3_Test(
            UsersFakeViewModel(UsersState(validUsersDTO().email, validUsersDTO().password)),
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
