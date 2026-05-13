package com.bellako.kiwi

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.utils.HTTPUtils.createFakeHttpException
import com.bellako.kiwi.features.goals.model.GoalsRepository
import com.bellako.kiwi.features.notifications.controller.NotificationEvent
import com.bellako.kiwi.features.notifications.controller.NotificationManager
import com.bellako.kiwi.features.skills.data.SkillDataMapper
import com.bellako.kiwi.features.skills.model.ISkillsAPI
import com.bellako.kiwi.features.skills.model.SkillsRepository
import com.bellako.kiwi.features.skills.model.SkillsViewModel
import com.bellako.kiwi.features.skills.screen.SkillNotificationType
import com.bellako.kiwi.features.skills.tests.SkillsTestFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.net.HttpURLConnection.HTTP_INTERNAL_ERROR
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class SkillsIntegrationTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var api: ISkillsAPI
    private lateinit var goalsRepository: GoalsRepository
    private lateinit var repository: SkillsRepository
    private lateinit var viewModel: SkillsViewModel
    private lateinit var notificationManager: NotificationManager

    @Before
    fun setUp() {
        notificationManager = NotificationManager()
        api = mock(ISkillsAPI::class.java)
        goalsRepository = mock(GoalsRepository::class.java)
        repository = SkillsRepository(api)
        viewModel = SkillsViewModel(repository, goalsRepository, notificationManager, RuntimeEnvironment.getApplication())
    }

    // -------------------------------------------------------------------------
    // Give skill
    // -------------------------------------------------------------------------

    @Test
    fun `give skill adds skill to state and emits NEW notification`() =
        runTest(mainDispatcherRule.dispatcher) {
            val skill = SkillsTestFactory.skill2()
            val skillDto = SkillDataMapper.toDTO(skill)

            whenever(api.giveSkill(skill.id)).thenReturn(skillDto)

            val notificationDeferred =
                async { notificationManager.notifications.first() }

            viewModel.giveSkill(skill.id)
            advanceUntilIdle()

            val notification = notificationDeferred.await()
            assertTrue(notification is NotificationEvent.Skill)

            val skillNotification = notification
            assertEquals(SkillNotificationType.NEW, skillNotification.type)
            assertEquals(skill.id, skillNotification.skill.id)

            val state = viewModel.state.value
            assertTrue(state.skills.containsKey(skill.id))
        }

    // -------------------------------------------------------------------------
    // Cooldown finished
    // -------------------------------------------------------------------------

    @Test
    fun `remove cooldown emits READY notification`() =
        runTest(mainDispatcherRule.dispatcher) {
            val initialState = SkillsTestFactory.validSkillsState()
            viewModel.apply {
                // Set initial state manually
                this::class.java
                    .getDeclaredField("_state")
                    .apply { isAccessible = true }
                    .set(this, kotlinx.coroutines.flow.MutableStateFlow(initialState))
            }

            val skill = initialState.skills[1L]!!
            val skillDto = SkillDataMapper.toDTO(skill).copy(cooldown = false)

            whenever(api.removeCooldown(skill.id)).thenReturn(skillDto)

            val notificationDeferred =
                async { notificationManager.notifications.first() }

            viewModel.removeCooldown(skill.id)
            advanceUntilIdle()

            val notification = notificationDeferred.await()
            assertTrue(notification is NotificationEvent.Skill)

            val skillNotification = notification
            assertEquals(SkillNotificationType.READY, skillNotification.type)
            assertEquals(skill.id, skillNotification.skill.id)
        }
}
