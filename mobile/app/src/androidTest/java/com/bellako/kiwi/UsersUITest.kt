package com.bellako.kiwi

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.common.CommonTestTags
import com.bellako.kiwi.users.UsersFakeViewModel
import com.bellako.kiwi.users.UsersScreen
import com.bellako.kiwi.users.UsersState
import com.bellako.kiwi.users.UsersTestTags
import com.bellako.kiwi.utils.HTTPUtils.createFakeHttpException
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class UsersUITest {
    @get:Rule
    val rule = createComposeRule()

    private lateinit var fakeViewModel: UsersFakeViewModel
    private lateinit var state: UsersState

    private lateinit var onLoginSuccessMock : () -> Unit

    @Before
    fun setUp() {
        state = UsersState("finn@thehuman.com", "Math3matical!")

        fakeViewModel = UsersFakeViewModel(
            state,
            false
        )

        onLoginSuccessMock = mock<() -> Unit>()
        rule.setContent {
            UsersScreen(fakeViewModel, onLoginSuccessMock)
        }
    }

    @Test
    fun validSignup () {
        rule.onNodeWithTag(UsersTestTags.SIGNUP_BUTTON).performClick()

        rule.onNodeWithTag(UsersTestTags.SUCCESS_TEXT).assertIsDisplayed()
        rule.onNodeWithTag(UsersTestTags.ERROR_TEXT).assertIsNotDisplayed()
    }

    @Test
    fun invalidSignup () {
        fakeViewModel.fakeError = true
        fakeViewModel.fakeException = Exception("Signup error")

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
    fun validLogin () {
        rule.onNodeWithTag(UsersTestTags.LOGIN_BUTTON).performClick()

        rule.onNodeWithTag(UsersTestTags.ERROR_TEXT).assertIsNotDisplayed()
        verify(onLoginSuccessMock).invoke()
    }

    @Test
    fun invalidLogin () {
        fakeViewModel.fakeError = true
        fakeViewModel.fakeException = Exception("Login error")

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