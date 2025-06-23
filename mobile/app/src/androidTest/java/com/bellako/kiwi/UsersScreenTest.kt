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
import com.bellako.kiwi.features.users.UsersFakeViewModel
import com.bellako.kiwi.features.users.UsersScreen
import com.bellako.kiwi.features.users.UsersState
import com.bellako.kiwi.features.users.UsersTestTags


@RunWith(AndroidJUnit4::class)
class UsersScreenTest {
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
            NavHost(navController = navController, startDestination = ScreenRoutes.USERS) {
                composable(ScreenRoutes.USERS) {
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

        rule.onNodeWithTag(UsersTestTags.ERROR_TEXT).assertIsDisplayed()
    }

    @Test
    fun errorOnSignup() {
        fakeViewModel.fakeError = true
        fakeViewModel.fakeException = createFakeHttpException(500)

        rule.onNodeWithTag(UsersTestTags.SIGNUP_BUTTON).performClick()

        rule.onNodeWithTag(CommonTestTags.ERROR_MODAL).assertIsDisplayed()
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

        rule.onNodeWithTag(UsersTestTags.ERROR_TEXT).assertIsDisplayed()
    }

    @Test
    fun errorOnLogin() {
        fakeViewModel.fakeError = true
        fakeViewModel.fakeException = createFakeHttpException(500)

        rule.onNodeWithTag(UsersTestTags.LOGIN_BUTTON).performClick()

        rule.onNodeWithTag(CommonTestTags.ERROR_MODAL).assertIsDisplayed()
    }
}
