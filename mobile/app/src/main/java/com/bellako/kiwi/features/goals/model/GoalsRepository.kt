package com.bellako.kiwi.features.goals.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.utils.DateUtils.dateToString
import com.bellako.kiwi.features.goals.data.GoalDTO
import com.bellako.kiwi.features.goals.data.UserGoalStatusDTO
import java.time.LocalDate

class GoalsRepository(
    private val api: IGoalsAPI,
) {
    suspend fun createGoals(goals: List<UserGoalStatusDTO>): Result<List<UserGoalStatusDTO>> =
        runCatching {
            api.createGoals(goals)
        }

    suspend fun updateGoalProgress(goalId: Long): Result<UserGoalStatusDTO> =
        runCatching {
            api.updateGoalProgress(goalId)
        }

    suspend fun updateGoal(goal: UserGoalStatusDTO): Result<UserGoalStatusDTO> =
        runCatching {
            api.updateGoal(goal.id, goal)
        }

    suspend fun completeGoal(goalId: Long): Result<UserGoalStatusDTO> =
        runCatching {
            api.completeGoal(goalId)
        }

    suspend fun uncompleteGoal(goalId: Long): Result<UserGoalStatusDTO> =
        runCatching {
            api.uncompleteGoal(goalId)
        }

    // region GET
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getGoalById(goalId: Long): Result<UserGoalStatusDTO> =
        runCatching {
            api.getGoalById(goalId)
        }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getGoalsByDate(date: LocalDate): Result<List<UserGoalStatusDTO>?> =
        runCatching {
            api.getGoalsByDate(dateToString(date))
        }

    suspend fun getAllGoals(): Result<List<UserGoalStatusDTO>> =
        runCatching {
            api.getAllGoals()
        }

    suspend fun getGoalsInProgress(): Result<List<UserGoalStatusDTO>> =
        runCatching {
            api.getGoalsInProgress()
        }

    suspend fun getGoalDefinitions(): Result<List<GoalDTO>> =
        runCatching {
            api.getGoalDefinitions()
        }

    suspend fun getAppGoals(): Result<List<UserGoalStatusDTO>> =
        runCatching {
            api.getAppGoals()
        }

    suspend fun getSkillGoals(): Result<List<UserGoalStatusDTO>> =
        runCatching {
            api.getSkillGoals()
        }

    // endregion
}
