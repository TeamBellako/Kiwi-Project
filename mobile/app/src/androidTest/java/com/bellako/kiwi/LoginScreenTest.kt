package com.bellako.kiwi

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.services.common.CommonTestTags
import com.bellako.kiwi.ui.screens.ScreenRoutes
import com.bellako.kiwi.services.common.HTTPUtils.createFakeHttpException
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.features.personality.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.PersonalityState
import com.bellako.kiwi.features.personality.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.users.UsersFakeViewModel
import com.bellako.kiwi.features.users.UsersState
import com.bellako.kiwi.features.users.UsersTestTags
import com.bellako.kiwi.features.users.login.LogInScreen


@RunWith(AndroidJUnit4::class)
class LoginScreenTest {
    @get:Rule
    val rule = createComposeRule()

    private lateinit var usersFakeViewModel: UsersFakeViewModel
    private lateinit var usersState: UsersState

    private lateinit var personalityFakeViewModel: PersonalityFakeViewModel
    private lateinit var personalityState: PersonalityState

    @Before
    fun setUp() {
        usersState = UsersState("finn@thehuman.com", "Math3matical!")
        personalityState = PersonalityState(validPersonalityDTO().realName, validPersonalityDTO().knightName, validPersonalityDTO().build)

        usersFakeViewModel = UsersFakeViewModel(usersState)
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
                composable(ScreenRoutes.HOME) {}
            }
        }
    }

    @Test
    fun validLogin() {
        rule.onNodeWithTag(UsersTestTags.LOGIN_BUTTON).performClick()
        rule.onNodeWithTag(UsersTestTags.ERROR_TEXT).assertDoesNotExist()
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
