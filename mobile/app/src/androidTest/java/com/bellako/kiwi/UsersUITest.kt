package com.bellako.kiwi

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.common.CommonTestTags
import com.bellako.kiwi.ui.ScreenRoutes
import com.bellako.kiwi.users.UsersFakeViewModel
import com.bellako.kiwi.users.UsersScreen
import com.bellako.kiwi.users.UsersState
import com.bellako.kiwi.users.UsersTestTags
import com.bellako.kiwi.utils.HTTPUtils.createFakeHttpException
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@RunWith(AndroidJUnit4::class)
class UsersUITest {
    @get:Rule
    val rule = createComposeRule()

    private lateinit var fakeViewModel: UsersFakeViewModel
    private lateinit var state: UsersState

    @Before
    fun setUp() {
        state = UsersState("finn@thehuman.com", "Math3matical!")

        fakeViewModel = UsersFakeViewModel(
            state,
            false
        )

        rule.setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = ScreenRoutes.LOGIN) {
                composable(ScreenRoutes.LOGIN) {
                    UsersScreen(
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
        rule.onNodeWithTag(UsersTestTags.SIGNUP_BUTTON).performClick()

        rule.onNodeWithTag(UsersTestTags.ERROR_TEXT).assertDoesNotExist()
    }

    @Test
    fun invalidSignup() {
        fakeViewModel.fakeError = true
        fakeViewModel.fakeException = createFakeHttpException(401)

        rule.onNodeWithTag(UsersTestTags.SIGNUP_BUTTON).performClick()

        rule.onNodeWithTag(UsersTestTags.SUCCESS_TEXT).assertIsNotDisplayed()
        rule.onNodeWithTag(UsersTestTags.ERROR_TEXT).assertIsDisplayed()
    }

    @Test
    fun errorOnSignup() {
        fakeViewModel.fakeError = true
        fakeViewModel.fakeException = createFakeHttpException(500)

        rule.onNodeWithTag(UsersTestTags.SIGNUP_BUTTON).performClick()

        rule.onNodeWithTag(CommonTestTags.ERROR_SCREEN).assertIsDisplayed()
    }

    @Test
    fun validLogin() {
        rule.onNodeWithTag(UsersTestTags.LOGIN_BUTTON).performClick()

        rule.onNodeWithTag(UsersTestTags.ERROR_TEXT).assertDoesNotExist()
    }

    @Test
    fun invalidLogin() {
        fakeViewModel.fakeError = true
        fakeViewModel.fakeException = createFakeHttpException(401)

        rule.onNodeWithTag(UsersTestTags.LOGIN_BUTTON).performClick()

        rule.onNodeWithTag(UsersTestTags.SUCCESS_TEXT).assertIsNotDisplayed()

        rule.onNodeWithTag(UsersTestTags.ERROR_TEXT).assertIsDisplayed()
    }

    @Test
    fun errorOnLogin() {
        fakeViewModel.fakeError = true
        fakeViewModel.fakeException = createFakeHttpException(500)

        rule.onNodeWithTag(UsersTestTags.LOGIN_BUTTON).performClick()

        rule.onNodeWithTag(CommonTestTags.ERROR_SCREEN).assertIsDisplayed()
    }
}
