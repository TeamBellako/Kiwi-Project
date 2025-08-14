package com.bellako.kiwi

import android.annotation.SuppressLint
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.common.screens.ScreenRoutes
import com.bellako.kiwi.common.utils.HTTPUtils.createFakeHttpException
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.features.map.screens.MapScreen
import com.bellako.kiwi.features.map.model.MapViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityAppsDTO
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.users.tests.UsersFakeViewModel
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.tests.UsersTestTags
import com.bellako.kiwi.features.users.screens.LogInScreen
import com.bellako.kiwi.features.users.screens.SignUpScreen3_Test

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {
    @get:Rule
    val rule = createComposeRule()

    private lateinit var usersState: UsersState
    private lateinit var usersFakeViewModel: UsersFakeViewModel

    private lateinit var personalityState: PersonalityState
    private lateinit var personalityFakeViewModel: PersonalityFakeViewModel

    @SuppressLint("ViewModelConstructorInComposable")
    @Before
    fun setUp() {
        AudioManager.setEnabled(false)

        usersState = UsersState("finn@thehuman.com", "Math3matical!")
        usersFakeViewModel = UsersFakeViewModel(usersState)

        personalityState = PersonalityState(
            validPersonalityDTO().realName,
            validPersonalityDTO().knightName,
            validPersonalityDTO().build,
            validPersonalityAppsDTO().goodApps,
            validPersonalityAppsDTO().badApps
        )
        personalityFakeViewModel = PersonalityFakeViewModel(personalityState)

        rule.setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = ScreenRoutes.LOGIN) {
                composable(ScreenRoutes.LOGIN) {
                    LogInScreen(
                        usersViewModel = usersFakeViewModel,
                        personalityViewModel = personalityFakeViewModel,
                        navController = navController
                    )
                }
                composable(ScreenRoutes.HOME) {
                    MapScreen(viewModel = MapViewModel())
                }
                composable(ScreenRoutes.SIGNUP3_TEST) {
                    SignUpScreen3_Test(
                        usersViewModel = usersFakeViewModel,
                        personalityViewModel = personalityFakeViewModel,
                        navController = navController
                    )
                }
            }
        }
    }

    @Test
    fun validLogin() {
        usersFakeViewModel.fakeError = false

        rule.onNodeWithTag(UsersTestTags.LOGIN_BUTTON).performClick()
        Thread.sleep(500)
        rule.onNodeWithTag(UsersTestTags.LOGIN_BUTTON).assertDoesNotExist()
    }

    @Test
    fun invalidLogin() {
        usersFakeViewModel.fakeError = true
        usersFakeViewModel.fakeException = createFakeHttpException(401)

        rule.onNodeWithTag(UsersTestTags.LOGIN_BUTTON).performClick()
        rule.onNodeWithTag(UsersTestTags.ERROR_TEXT).assertIsDisplayed()
    }

    @Test
    fun errorOnLogin() {
        usersFakeViewModel.fakeError = true
        usersFakeViewModel.fakeException = createFakeHttpException(500)

        rule.onNodeWithTag(UsersTestTags.LOGIN_BUTTON).performClick()
        rule.onNodeWithTag(CommonTestTags.ERROR_MODAL).assertIsDisplayed()
    }
}
