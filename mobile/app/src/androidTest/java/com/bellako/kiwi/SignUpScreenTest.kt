package com.bellako.kiwi

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.common.screens.ScreenRoutes
import com.bellako.kiwi.common.model.HTTPUtils.createFakeHttpException
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.features.personality.tests.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.users.tests.UsersFakeViewModel
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.tests.UsersTestTags
import com.bellako.kiwi.features.users.screens.SignUpScreen
import com.bellako.kiwi.features.users.screens.SignUpTestScreen


@RunWith(AndroidJUnit4::class)
class SignUpScreenTest {
    @get:Rule
    val rule = createComposeRule()

    private lateinit var usersFakeViewModel: UsersFakeViewModel
    private lateinit var usersState: UsersState

    private lateinit var personalityFakeViewModel: PersonalityFakeViewModel
    private lateinit var personalityState: PersonalityState

    @Before
    fun setUp() {
        AudioManager.setEnabled(false)

        usersState = UsersState("finn@thehuman.com", "Math3matical!")
        personalityState = PersonalityState(validPersonalityDTO().realName, validPersonalityDTO().knightName, validPersonalityDTO().build)

        usersFakeViewModel = UsersFakeViewModel(usersState)
        personalityFakeViewModel = PersonalityFakeViewModel(personalityState)

        rule.setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = ScreenRoutes.SIGNUP) {
                composable(ScreenRoutes.SIGNUP) {
                    SignUpScreen(
                        usersViewModel = usersFakeViewModel,
                        personalityViewModel = personalityFakeViewModel,
                        navController = navController
                    )
                }
                composable(ScreenRoutes.SIGNUP_TEST) {
                    SignUpTestScreen(
                        usersViewModel = usersFakeViewModel,
                        personalityViewModel = personalityFakeViewModel,
                        navController = navController
                    )
                }
            }
        }
    }

    @Test
    fun validSignup() {
        usersFakeViewModel.fakeError = false

        rule.onNodeWithTag(UsersTestTags.SIGNUP_BUTTON).performClick()
        Thread.sleep(500)
        rule.onNodeWithTag(UsersTestTags.SIGNUP_BUTTON).assertDoesNotExist()
    }

    @Test
    fun invalidSignup() {
        usersFakeViewModel.fakeError = true
        usersFakeViewModel.fakeException = createFakeHttpException(401)

        rule.onNodeWithTag(UsersTestTags.SIGNUP_BUTTON).performClick()
        rule.onNodeWithTag(UsersTestTags.ERROR_TEXT).assertIsDisplayed()
    }

    @Test
    fun errorOnSignup() {
        usersFakeViewModel.fakeError = true
        usersFakeViewModel.fakeException = createFakeHttpException(500)

        rule.onNodeWithTag(UsersTestTags.SIGNUP_BUTTON).performClick()
        rule.onNodeWithTag(CommonTestTags.ERROR_MODAL).assertIsDisplayed()
    }

}
