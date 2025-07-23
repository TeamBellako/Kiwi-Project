package com.bellako.kiwi.features.users.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.services.common.CommonTestTags
import com.bellako.kiwi.ui.modals.ErrorModal
import com.bellako.kiwi.services.common.UIState
import com.bellako.kiwi.ui.screens.ScreenRoutes
import com.bellako.kiwi.ui.components.Kiwi_TextArguments
import com.bellako.kiwi.ui.theme.KiwiTheme
import com.bellako.kiwi.ui.components.Kiwi_Image
import com.bellako.kiwi.ui.theme.Spacing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.bellako.kiwi.features.personality.IPersonalityViewModel
import com.bellako.kiwi.features.personality.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.PersonalityState
import com.bellako.kiwi.features.personality.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.users.IUsersViewModel
import com.bellako.kiwi.features.users.UsersFakeViewModel
import com.bellako.kiwi.features.users.UsersState
import com.bellako.kiwi.ui.components.Kiwi_Button
import com.bellako.kiwi.ui.components.Kiwi_H2
import com.bellako.kiwi.ui.components.Kiwi_Spacer
import com.bellako.kiwi.ui.theme.getResponsiveRelativeSize


@Composable
fun SignUpTestScreen(
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Kiwi_Image(
            R.drawable.ph_onboarding_bkg,
            "Sign Up Background",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(getResponsiveRelativeSize(Spacing.medium)),
            contentAlignment = Alignment.Center
        ) {
            Question(
                usersViewModel,
                personalityViewModel,
                navController
            )
        }
    }
}

@Composable
private fun Question(
    usersViewModel: IUsersViewModel,
    personalityViewModel: IPersonalityViewModel,
    navController: NavController
) {
    val context = LocalContext.current

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .testTag(CommonTestTags.USERS_SCREEN),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    var currentQuestion by remember { mutableIntStateOf(currentPersonalityState.currentQuestion) }

                    Kiwi_H2(
                        Kiwi_TextArguments(
                            currentPersonalityState.questions[currentQuestion].question,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )

                    Kiwi_Spacer(Spacing.large)

                    currentPersonalityState.questions[currentQuestion].options.forEachIndexed { index, option ->

                        Kiwi_Button(
                            Kiwi_TextArguments(
                                option,
                                color = MaterialTheme.colorScheme.secondary,
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            onClick = {
                                currentPersonalityState.answers[currentQuestion] = index
                                if (currentQuestion + 1 < currentPersonalityState.questions.size) {
                                    ++currentQuestion
                                } else {
                                    navController.navigate(ScreenRoutes.HOME)
                                    CoroutineScope(Dispatchers.Main).launch {
                                        if (personalityViewModel.updateBuild().isSuccess) {
                                            navController.navigate(ScreenRoutes.HOME)
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

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun SignUpTestScreenPreview() {
    KiwiTheme {
        SignUpTestScreen(
            UsersFakeViewModel(
                UsersState("finn@thehuman.com", "Math3matical!"),
            ),
            PersonalityFakeViewModel(
                PersonalityState(validPersonalityDTO().realName, validPersonalityDTO().knightName, validPersonalityDTO().build),
            ),
            navController = rememberNavController()
        )
    }
}
