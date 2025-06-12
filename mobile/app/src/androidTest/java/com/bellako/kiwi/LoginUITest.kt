package com.bellako.kiwi

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.common.CommonTestTags
import com.bellako.kiwi.common.ScreenRoutes
import com.bellako.kiwi.login.LoginFakeViewModel
import com.bellako.kiwi.login.LoginScreen
import com.bellako.kiwi.login.LoginState
import com.bellako.kiwi.login.LoginTestTags
import com.bellako.kiwi.utils.HTTPUtils.createFakeHttpException
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@RunWith(AndroidJUnit4::class)
class LoginUITest {
    @get:Rule
    val rule = createComposeRule()

    private lateinit var fakeViewModel: LoginFakeViewModel
    private lateinit var state: LoginState

    @Before
    fun setUp() {
        state = LoginState("finn@thehuman.com", "Math3matical!")

        fakeViewModel = LoginFakeViewModel(
            state,
            false
        )

        rule.setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = ScreenRoutes.LOGIN) {
                composable(ScreenRoutes.LOGIN) {
                    LoginScreen(
                        viewModel = fakeViewModel,
                        navController = navController
                    )
                }
                composable(ScreenRoutes.HOME) {}
            }
        }
    }

    @Test
    fun validSignup() {
        rule.onNodeWithTag(LoginTestTags.SIGNUP_BUTTON).performClick()

        rule.onNodeWithTag(LoginTestTags.ERROR_TEXT).assertDoesNotExist()
    }

    @Test
    fun invalidSignup() {
        fakeViewModel.fakeError = true
        fakeViewModel.fakeException = createFakeHttpException(401)

        rule.onNodeWithTag(LoginTestTags.SIGNUP_BUTTON).performClick()

        rule.onNodeWithTag(LoginTestTags.SUCCESS_TEXT).assertIsNotDisplayed()
        rule.onNodeWithTag(LoginTestTags.ERROR_TEXT).assertIsDisplayed()
    }

    @Test
    fun errorOnSignup() {
        fakeViewModel.fakeError = true
        fakeViewModel.fakeException = createFakeHttpException(500)

        rule.onNodeWithTag(LoginTestTags.SIGNUP_BUTTON).performClick()

        rule.onNodeWithTag(CommonTestTags.ERROR_MODAL).assertIsDisplayed()
    }

    @Test
    fun validLogin() {
        rule.onNodeWithTag(LoginTestTags.LOGIN_BUTTON).performClick()

        rule.onNodeWithTag(LoginTestTags.ERROR_TEXT).assertDoesNotExist()
    }

    @Test
    fun invalidLogin() {
        fakeViewModel.fakeError = true
        fakeViewModel.fakeException = createFakeHttpException(401)

        rule.onNodeWithTag(LoginTestTags.LOGIN_BUTTON).performClick()

        rule.onNodeWithTag(LoginTestTags.SUCCESS_TEXT).assertIsNotDisplayed()

        rule.onNodeWithTag(LoginTestTags.ERROR_TEXT).assertIsDisplayed()
    }

    @Test
    fun errorOnLogin() {
        fakeViewModel.fakeError = true
        fakeViewModel.fakeException = createFakeHttpException(500)

        rule.onNodeWithTag(LoginTestTags.LOGIN_BUTTON).performClick()

        rule.onNodeWithTag(CommonTestTags.ERROR_MODAL).assertIsDisplayed()
    }
}
