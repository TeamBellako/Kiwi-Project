package com.bellako.kiwi

import android.annotation.SuppressLint
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.common.data.ScreenRoutes
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.common.utils.HTTPUtils.createFakeHttpException
import com.bellako.kiwi.features.dashboard.screens.LocalGoalsViewModel
import com.bellako.kiwi.features.goals.tests.GoalsFakeViewModel
import com.bellako.kiwi.features.map.model.MapViewModel
import com.bellako.kiwi.features.map.screens.MapScreen
import com.bellako.kiwi.features.nodes.data.NodeStatus
import com.bellako.kiwi.features.nodes.data.NodesDomain
import com.bellako.kiwi.features.nodes.data.NodesState
import com.bellako.kiwi.features.nodes.tests.NodesFakeViewModel
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.tests.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityAppsDTO
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.users.data.UsersState
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
    private lateinit var nodesState: NodesState
    private lateinit var usersFakeViewModel: UsersFakeViewModel
    private lateinit var mapviewModel: MapViewModel
    private lateinit var nodesFakeViewModel: NodesFakeViewModel
    private lateinit var goalsFakeViewModel: GoalsFakeViewModel

    private lateinit var personalityState: PersonalityState
    private lateinit var personalityFakeViewModel: PersonalityFakeViewModel

    @SuppressLint("ViewModelConstructorInComposable")
    @Before
    fun setUp() {
        AudioManager.setEnabled(false)

        usersState = UsersState(validUsersDTO().email, validUsersDTO().password, validUsersDTO().registerDate)
        usersFakeViewModel = UsersFakeViewModel(usersState)
        nodesState = NodesState(List<NodesDomain>(1) { NodesDomain(1, 1, NodeStatus.INACCESSIBLE, 0, 0.0f, 0.0f) })
        nodesFakeViewModel = NodesFakeViewModel(nodesState)
        mapviewModel = MapViewModel()
        goalsFakeViewModel = GoalsFakeViewModel()

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
            val navController = rememberNavController()

            CompositionLocalProvider(LocalGoalsViewModel provides goalsFakeViewModel) {
                NavHost(navController = navController, startDestination = ScreenRoutes.LOGIN) {
                    composable(ScreenRoutes.LOGIN) {
                        LogInScreen(
                            usersViewModel = usersFakeViewModel,
                            personalityViewModel = personalityFakeViewModel,
                            navController = navController,
                        )
                    }
                    composable(ScreenRoutes.HOME) {
                        MapScreen(
                            mapViewModel = mapviewModel,
                            nodesViewModel = nodesFakeViewModel,
                        )
                    }
                    composable(ScreenRoutes.SIGNUP3_TEST) {
                        SignUpScreen3_Test(
                            usersViewModel = usersFakeViewModel,
                            personalityViewModel = personalityFakeViewModel,
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
        Thread.sleep(500)
        rule.onNodeWithTag(UsersTestTags.LOGIN_BUTTON).assertDoesNotExist()
    }

    @Test
    fun invalidLogin() {
        usersFakeViewModel.fakeError = true
        usersFakeViewModel.fakeException = createFakeHttpException(HTTP_UNAUTHORIZED)

        rule.onNodeWithTag(UsersTestTags.LOGIN_BUTTON).performClick()
        rule.onNodeWithTag(UsersTestTags.ERROR_TEXT).assertIsDisplayed()
    }

    @Test
    fun errorOnLogin() {
        usersFakeViewModel.fakeError = true
        usersFakeViewModel.fakeException = createFakeHttpException(HTTP_INTERNAL_ERROR)

        rule.onNodeWithTag(UsersTestTags.LOGIN_BUTTON).performClick()
        rule.onNodeWithTag(CommonTestTags.ERROR_MODAL).assertIsDisplayed()
    }
}
