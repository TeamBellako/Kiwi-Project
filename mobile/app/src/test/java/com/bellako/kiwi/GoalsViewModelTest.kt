package com.bellako.kiwi

import com.bellako.kiwi.common.utils.DateUtils
import com.bellako.kiwi.features.goals.data.GoalCategory
import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalType
import com.bellako.kiwi.features.goals.data.UserGoalStatusDTO
import com.bellako.kiwi.features.goals.model.GoalsRepository
import com.bellako.kiwi.features.goals.model.GoalsViewModel
import com.bellako.kiwi.features.goals.model.IGoalsAPI
import com.bellako.kiwi.features.notifications.controller.NotificationManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class GoalsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Fake API implementation to control behavior and count invocations
    class FakeGoalsAPI {
        private val storage = mutableMapOf<Long, UserGoalStatusDTO>()
        var getGoalsByDateCalls = 0
        var getGoalsInProgressCalls = 0
        var updateProgressCalls = 0
        var createGoalsCalls = 0

        init {
            val today = DateUtils.dateToString(LocalDate.now())
            val g1 =
                UserGoalStatusDTO(
                    id = 1L,
                    goalId = 1L,
                    name = "Action 1",
                    target = 10,
                    action = "Action 1",
                    type = "EXERCISE",
                    category = "DAILY_CHALLENGES",
                    status = "IN_PROGRESS",
                    reward = 100,
                    date = today,
                    value = 0,
                )
            val g2 =
                UserGoalStatusDTO(
                    id = 2L,
                    goalId = 2L,
                    name = "Action 2",
                    target = 20,
                    action = "Action 2",
                    type = "EXERCISE",
                    category = "DAILY_CHALLENGES",
                    status = "IN_PROGRESS",
                    reward = 200,
                    date = today,
                    value = 5,
                )
            storage[g1.id] = g1
            storage[g2.id] = g2
        }

        fun getGoalsByDate(date: String): List<UserGoalStatusDTO> {
            getGoalsByDateCalls++
            // return all goals with matching date
            return storage.values.filter { it.date == date }
        }

        fun getGoalsInProgress(): List<UserGoalStatusDTO> {
            getGoalsInProgressCalls++
            return storage.values.filter { it.status != "COMPLETED" }
        }

        fun updateGoalProgress(goalId: Long): UserGoalStatusDTO {
            updateProgressCalls++
            val existing = storage[goalId] ?: throw NoSuchElementException("Not found")
            val newValue = (existing.value + 1).coerceAtMost(existing.target)
            val status = if (newValue >= existing.target) "COMPLETED" else existing.status
            val updated = existing.copy(value = newValue, status = status)
            storage[goalId] = updated
            return updated
        }

        fun createGoals(goals: List<UserGoalStatusDTO>): List<UserGoalStatusDTO> {
            createGoalsCalls++
            goals.forEach { storage[it.id] = it }
            return goals
        }
    }

    private fun buildViewModel(fakeApi: FakeGoalsAPI): GoalsViewModel {
        val repo =
            GoalsRepository(
                object : IGoalsAPI {
                    override suspend fun createGoals(goals: List<UserGoalStatusDTO>): List<UserGoalStatusDTO> = fakeApi.createGoals(goals)

                    override suspend fun updateGoalProgress(goalId: Long): UserGoalStatusDTO = fakeApi.updateGoalProgress(goalId)

                    override suspend fun updateGoal(
                        goalId: Long,
                        goal: UserGoalStatusDTO,
                    ): UserGoalStatusDTO = throw UnsupportedOperationException("Not needed in tests")

                    override suspend fun completeGoal(goalId: Long): UserGoalStatusDTO =
                        throw UnsupportedOperationException("Not needed in tests")

                    override suspend fun uncompleteGoal(goalId: Long): UserGoalStatusDTO =
                        throw UnsupportedOperationException("Not needed in tests")

                    override suspend fun getGoalById(goalId: Long): UserGoalStatusDTO {
                        TODO("Not yet implemented")
                    }

                    override suspend fun getGoalsByDate(date: String): List<UserGoalStatusDTO> = fakeApi.getGoalsByDate(date)

                    override suspend fun getAllGoals(): List<UserGoalStatusDTO> = throw UnsupportedOperationException("Not needed in tests")

                    override suspend fun getGoalsInProgress(): List<UserGoalStatusDTO> = fakeApi.getGoalsInProgress()

                    override suspend fun getGoalDefinitions(): List<com.bellako.kiwi.features.goals.data.GoalDTO> =
                        throw UnsupportedOperationException("Not needed in tests")

                    override suspend fun getAppGoals(): List<UserGoalStatusDTO> {
                        TODO("Not yet implemented")
                    }

                    override suspend fun getSkillGoals(): List<UserGoalStatusDTO> {
                        TODO("Not yet implemented")
                    }
                },
            )

        return GoalsViewModel(repo, NotificationManager())
    }

    @Test
    fun test_getGoalsByDate_usesCache() =
        runTest {
            val fakeApi = FakeGoalsAPI()
            val vm = buildViewModel(fakeApi)
            val today = DateUtils.dateToString(LocalDate.now())

            val first = vm.getGoalsByDate(today)
            assert(first.isSuccess)
            Assert.assertEquals(1, fakeApi.getGoalsByDateCalls)

            val second = vm.getGoalsByDate(today)
            assert(second.isSuccess)
            // Should be cache hit, so API not called again
            Assert.assertEquals(1, fakeApi.getGoalsByDateCalls)
        }

    @Test
    fun test_updateGoalProgress_updatesCache() =
        runTest {
            val fakeApi = FakeGoalsAPI()
            val vm = buildViewModel(fakeApi)
            val today = DateUtils.dateToString(LocalDate.now())

            // Load into cache
            val loaded = vm.getGoalsByDate(today)
            assert(loaded.isSuccess)
            Assert.assertEquals(1, fakeApi.getGoalsByDateCalls)

            // Update progress for goal 1
            val updated = vm.updateGoalProgress(1L)
            assert(updated.isSuccess)
            Assert.assertEquals(1, fakeApi.updateProgressCalls)

            // Fetch again - should be cache hit and reflect updated value
            val after = vm.getGoalsByDate(today)
            assert(after.isSuccess)
            val goals = after.getOrNull()!!
            val g1 = goals.find { it.id == 1L }!!
            Assert.assertEquals(1, g1.value)
        }

    @Test
    fun test_createGoals_addsToCache() =
        runTest {
            val fakeApi = FakeGoalsAPI()
            val vm = buildViewModel(fakeApi)
            val today = DateUtils.dateToString(LocalDate.now())

            // Create new goal definition and call createGoalsFromDefinitions
            val suggested =
                UserGoalStatusDTO(
                    id = 3L,
                    goalId = 3L,
                    name = "Action 3",
                    target = 5,
                    action = "Action 3",
                    type = "EXERCISE",
                    category = "DAILY_CHALLENGES",
                    status = "IN_PROGRESS",
                    reward = 50,
                    date = today,
                    value = 0,
                )
            fakeApi.createGoals(listOf(suggested))

            val after = vm.getGoalsByDate(today)
            assert(after.isSuccess)
            val list = after.getOrNull()!!
            Assert.assertTrue(list.any { it.id == 3L })
        }
}
