package com.bellako.kiwi

import android.annotation.SuppressLint
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
import com.bellako.kiwi.features.map.model.MapViewModel
import com.bellako.kiwi.features.map.screens.MapScreen
import com.bellako.kiwi.features.nodes.data.NodeStatus
import com.bellako.kiwi.features.nodes.data.NodesDomain
import com.bellako.kiwi.features.nodes.data.NodesState
import com.bellako.kiwi.features.nodes.tests.NodesFakeViewModel
import com.bellako.kiwi.features.nodes.tests.NodesTestFactory
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.tests.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityAppsDTO
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.quests.tests.QuestsFakeViewModel
import com.bellako.kiwi.features.quests.tests.QuestsTestFactory
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.screens.SignUpScreen4_Apps
import com.bellako.kiwi.features.users.tests.UsersFakeViewModel
import com.bellako.kiwi.features.users.tests.UsersTestFactory.validUsersDTO
import com.bellako.kiwi.features.users.tests.UsersTestTags
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection.HTTP_INTERNAL_ERROR

@RunWith(AndroidJUnit4::class)
class SignUpScreen4Test {
    @get:Rule
    val rule = createComposeRule()

    private lateinit var usersFakeViewModel: UsersFakeViewModel
    private lateinit var usersState: UsersState
    private lateinit var nodesState: NodesState
    private lateinit var nodesFakeViewModel: NodesFakeViewModel
    private lateinit var questsFakeViewModel: QuestsFakeViewModel
    private lateinit var mapviewModel: MapViewModel
    private lateinit var personalityFakeViewModel: PersonalityFakeViewModel
    private lateinit var personalityState: PersonalityState

    @SuppressLint("ViewModelConstructorInComposable")
    @Before
    fun setUp() {
        AudioManager.setEnabled(false)

        usersState = UsersState(validUsersDTO().email, validUsersDTO().password, validUsersDTO().registerDate)
        personalityState =
            PersonalityState(
                validPersonalityDTO().realName,
                validPersonalityDTO().knightName,
                validPersonalityDTO().build,
                validPersonalityAppsDTO().goodApps,
                validPersonalityAppsDTO().badApps,
                validPersonalityAppsDTO().neutralApps,
            )

        usersFakeViewModel = UsersFakeViewModel(usersState)
        personalityFakeViewModel = PersonalityFakeViewModel(personalityState)

        nodesFakeViewModel = NodesFakeViewModel(NodesTestFactory.validNodesState())
        questsFakeViewModel = QuestsFakeViewModel(QuestsTestFactory.validQuestsState())
        mapviewModel = MapViewModel()

        rule.setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = ScreenRoutes.SIGNUP4_APPS) {
                composable(ScreenRoutes.SIGNUP4_APPS) {
                    SignUpScreen4_Apps(
                        personalityViewModel = personalityFakeViewModel,
                        navController = navController,
                    )
                }
                composable(ScreenRoutes.HOME) {
                    MapScreen(
                        nodesViewModel = nodesFakeViewModel,
                        mapViewModel = mapviewModel,
                        questsViewModel = questsFakeViewModel,
                        navController = navController,
                    )
                }
            }
        }
    }

    @Test
    fun screen4_validApps() {
        personalityFakeViewModel.fakeError = false

        rule.onNodeWithTag(UsersTestTags.SIGNUP_BUTTON).performClick()
        Thread.sleep(500)
        rule.onNodeWithTag(UsersTestTags.SIGNUP_BUTTON).assertDoesNotExist()
    }

    @Test
    fun screen4_errorOnSignupApps() {
        personalityFakeViewModel.fakeError = true
        personalityFakeViewModel.fakeException = createFakeHttpException(HTTP_INTERNAL_ERROR)

        rule.onNodeWithTag(UsersTestTags.SIGNUP_BUTTON).performClick()
        Thread.sleep(500)
        rule.onNodeWithTag(CommonTestTags.ERROR_MODAL).assertIsDisplayed()
    }
}
