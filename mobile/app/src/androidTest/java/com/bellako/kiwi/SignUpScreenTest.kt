package com.bellako.kiwi

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.common.screens.ScreenRoutes
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.common.utils.HTTPUtils.createFakeHttpException
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.tests.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityAppsDTO
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.screens.SignUpScreen2_Form
import com.bellako.kiwi.features.users.screens.SignUpScreen3_Test
import com.bellako.kiwi.features.users.screens.SignUpScreen4_Apps
import com.bellako.kiwi.features.users.tests.UsersFakeViewModel
import com.bellako.kiwi.features.users.tests.UsersTestFactory.validUsersDTO
import com.bellako.kiwi.features.users.tests.UsersTestTags
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection.HTTP_INTERNAL_ERROR
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

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

        usersState = UsersState(validUsersDTO().email, validUsersDTO().password)
        personalityState =
            PersonalityState(
                validPersonalityDTO().realName,
                validPersonalityDTO().knightName,
                validPersonalityDTO().build,
                validPersonalityAppsDTO().goodApps,
                validPersonalityAppsDTO().badApps,
            )

        usersFakeViewModel = UsersFakeViewModel(usersState)
        personalityFakeViewModel = PersonalityFakeViewModel(personalityState)

        rule.setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = ScreenRoutes.SIGNUP2_FORM) {
                composable(ScreenRoutes.SIGNUP2_FORM) {
                    SignUpScreen2_Form(
                        usersViewModel = usersFakeViewModel,
                        personalityViewModel = personalityFakeViewModel,
                        navController = navController,
                    )
                }
                composable(ScreenRoutes.SIGNUP3_TEST) {
                    SignUpScreen3_Test(
                        usersViewModel = usersFakeViewModel,
                        personalityViewModel = personalityFakeViewModel,
                        navController = navController,
                    )
                }
                composable(ScreenRoutes.SIGNUP4_APPS) {
                    SignUpScreen4_Apps(
                        personalityViewModel = personalityFakeViewModel,
                        navController = navController,
                    )
                }
            }
        }
    }

    @Test
    fun screen2_validSignup() {
        usersFakeViewModel.fakeError = false

        rule.onNodeWithTag(UsersTestTags.SIGNUP_BUTTON).performClick()
        Thread.sleep(500)
        rule.onNodeWithTag(UsersTestTags.SIGNUP_BUTTON).assertDoesNotExist()
    }

    @Test
    fun screen2_invalidSignupUser() {
        usersFakeViewModel.fakeError = true
        usersFakeViewModel.fakeException = createFakeHttpException(HTTP_UNAUTHORIZED)

        rule.onNodeWithTag(UsersTestTags.SIGNUP_BUTTON).performClick()
        Thread.sleep(500)
        rule.onNodeWithTag(UsersTestTags.ERROR_TEXT).assertIsDisplayed()
    }

    @Test
    fun screen2_errorOnSignupUser() {
        usersFakeViewModel.fakeError = true
        usersFakeViewModel.fakeException = createFakeHttpException(HTTP_INTERNAL_ERROR)

        rule.onNodeWithTag(UsersTestTags.SIGNUP_BUTTON).performClick()
        Thread.sleep(500)
        rule.onNodeWithTag(CommonTestTags.ERROR_MODAL).assertIsDisplayed()
    }

    @Test
    fun screen4_validUpdateApps() {
        personalityFakeViewModel.fakeError = false

        rule.onNodeWithTag(UsersTestTags.SIGNUP_BUTTON).performClick()
        Thread.sleep(500)
        rule.onNodeWithTag(UsersTestTags.SIGNUP_BUTTON).assertDoesNotExist()
    }

    @Test
    fun screen4_invalidUpdateApps() {
        personalityFakeViewModel.fakeError = true
        personalityFakeViewModel.fakeException = createFakeHttpException(HTTP_INTERNAL_ERROR)

        rule.onNodeWithTag(UsersTestTags.SIGNUP_BUTTON).performClick()
        Thread.sleep(500)
        rule.onNodeWithTag(CommonTestTags.ERROR_MODAL).assertIsDisplayed()
    }

    @Test
    fun screen4_errorUpdateApps() {
        personalityFakeViewModel.fakeError = true
        usersFakeViewModel.fakeException = createFakeHttpException(HTTP_INTERNAL_ERROR)

        rule.onNodeWithTag(UsersTestTags.SIGNUP_BUTTON).performClick()
        Thread.sleep(500)
        rule.onNodeWithTag(CommonTestTags.ERROR_MODAL).assertIsDisplayed()
    }
}
