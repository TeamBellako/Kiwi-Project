package com.bellako.kiwi

import android.annotation.SuppressLint
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.common.data.ScreenRoutes
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.common.utils.HTTPUtils.createFakeHttpException
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.tests.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityAppsDTO
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.skills.tests.SkillsFakeViewModel
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.screens.LocalLoginBackgroundAnimated
import com.bellako.kiwi.features.users.screens.LogInScreen
import com.bellako.kiwi.features.users.screens.SignUpScreen3_Test
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
class LoginScreenTest {
    @get:Rule
    val rule = createComposeRule()

    private lateinit var usersState: UsersState
    private lateinit var usersFakeViewModel: UsersFakeViewModel
    private lateinit var personalityState: PersonalityState
    private lateinit var personalityFakeViewModel: PersonalityFakeViewModel
    private lateinit var skillsFakeViewModel: SkillsFakeViewModel

    @SuppressLint("ViewModelConstructorInComposable")
    @Before
    fun setUp() {
        AudioManager.setEnabled(false)

        usersState = UsersState(validUsersDTO().email, validUsersDTO().password, validUsersDTO().registerDate)
        usersFakeViewModel = UsersFakeViewModel(usersState)
        skillsFakeViewModel = SkillsFakeViewModel()

        personalityState =
            PersonalityState(
                validPersonalityDTO().realName,
                validPersonalityDTO().knightName,
                validPersonalityDTO().build,
                validPersonalityAppsDTO().goodApps,
                validPersonalityAppsDTO().badApps,
                validPersonalityAppsDTO().neutralApps,
            )
        personalityFakeViewModel = PersonalityFakeViewModel(personalityState)

        rule.setContent {
            // Disable the LoginScreen's infinite scrolling background so
            // the Compose test runtime can reach idle. The animation itself
            // is exercised by previews, not by this test.
            CompositionLocalProvider(LocalLoginBackgroundAnimated provides false) {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = ScreenRoutes.LOGIN) {
                    composable(ScreenRoutes.LOGIN) {
                        LogInScreen(
                            usersViewModel = usersFakeViewModel,
                            personalityViewModel = personalityFakeViewModel,
                            navController = navController,
                        )
                    }
                    composable(ScreenRoutes.HOME) {
                        // Stub destination. The real MapScreen runs perpetual
                        // animations (mist, cloud sprites, player-node bob,
                        // selected-node wiggle / glow) that keep the Compose
                        // test runtime non-idle, hanging fetchSemanticsNodes()
                        // inside waitUntil. This test only asserts that login
                        // navigation succeeded — it doesn't validate MapScreen
                        // content — so a no-op destination is sufficient.
                    }
                    composable(ScreenRoutes.SIGNUP3_TEST) {
                        SignUpScreen3_Test(
                            usersViewModel = usersFakeViewModel,
                            personalityViewModel = personalityFakeViewModel,
                            skillsViewModel = skillsFakeViewModel,
                            navController = navController,
                        )
                    }
                }
            }
        }
    }

    @Test
    fun validLogin() {
        usersFakeViewModel.fakeError = false

        rule.onNodeWithTag(UsersTestTags.LOGIN_BUTTON).performClick()
        rule.waitUntil(timeoutMillis = 5000) {
            rule.onAllNodesWithTag(UsersTestTags.LOGIN_BUTTON).fetchSemanticsNodes().isEmpty()
        }
        rule.onNodeWithTag(UsersTestTags.LOGIN_BUTTON).assertDoesNotExist()
    }

    @Test
    fun invalidLogin() {
        usersFakeViewModel.fakeError = true
        usersFakeViewModel.fakeException = createFakeHttpException(HTTP_UNAUTHORIZED)

        rule.onNodeWithTag(UsersTestTags.LOGIN_BUTTON).performClick()
        rule.waitUntil(timeoutMillis = 5000) {
            rule
                .onAllNodesWithTag(UsersTestTags.ERROR_TEXT)
                .fetchSemanticsNodes()
                .any { node ->
                    node.config
                        .getOrNull(SemanticsProperties.Text)
                        ?.any { it.text.isNotEmpty() } == true
                }
        }
        rule.onNodeWithTag(UsersTestTags.ERROR_TEXT).assertIsDisplayed()
    }

    @Test
    fun errorOnLogin() {
        usersFakeViewModel.fakeError = true
        usersFakeViewModel.fakeException = createFakeHttpException(HTTP_INTERNAL_ERROR)

        rule.onNodeWithTag(UsersTestTags.LOGIN_BUTTON).performClick()
        rule.waitUntil(timeoutMillis = 5000) {
            rule.onAllNodesWithTag(CommonTestTags.ERROR_MODAL).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithTag(CommonTestTags.ERROR_MODAL).assertIsDisplayed()
    }
}
