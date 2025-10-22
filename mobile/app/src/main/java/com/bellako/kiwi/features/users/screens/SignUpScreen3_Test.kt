package com.bellako.kiwi.features.users.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.analytics.FirebaseEventNames
import com.bellako.kiwi.analytics.firebaseLogEvent
import com.bellako.kiwi.common.data.ScreenRoutes
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Button
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.LoadingModal
import com.bellako.kiwi.common.screens.modals.ErrorModalScreen
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.model.IUsersViewModel
import com.bellako.kiwi.features.users.tests.UsersFakeViewModel
import com.bellako.kiwi.features.users.tests.UsersTestFactory.validUsersDTO
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
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
    val isPreview = LocalInspectionMode.current

    val usersUiState by usersViewModel.uiState.collectAsState()

    val personalityState by personalityViewModel.state.collectAsState()
    val personalityUiState by personalityViewModel.uiState.collectAsState()
    val personalityIsLoading by personalityViewModel.isLoading.collectAsState()

    var localLoading by remember { mutableStateOf(false) }
    val isLoading by remember { derivedStateOf { localLoading || personalityIsLoading } }

    personalityState?.let { currentPersonalityState ->

        if (usersUiState == UIState.GeneralError || personalityUiState == UIState.GeneralError) {
            ErrorModalScreen(onButtonClick = {
                usersViewModel.resetUiState()
                personalityViewModel.resetUiState()
            })
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
                var currentQuestion by remember { mutableIntStateOf(currentPersonalityState.currentQuestion) }

                Kiwi_H2(
                    KiwiTextArguments(
                        currentPersonalityState.questions[currentQuestion].question,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary,
                    ),
                )

                Kiwi_Spacer(Spacing.large)

                currentPersonalityState.questions[currentQuestion].options.forEachIndexed { index, option ->

                    Kiwi_Button(
                        textArguments =
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
                                        firebaseLogEvent(FirebaseEventNames.SIGNUP_3_TEST_COMPLETED)

                                        navController.navigate(ScreenRoutes.SIGNUP4_APPS)
                                        localLoading = true
                                    }
                                }
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
}

// -------------------------------------------------------------------------------------------------

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
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
            navController = rememberNavController(),
        )
    }
}
