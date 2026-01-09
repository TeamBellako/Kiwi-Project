package com.bellako.kiwi.features.goals.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.utils.DateUtils.dateToString
import com.bellako.kiwi.features.goals.data.GoalDTO
import com.bellako.kiwi.features.goals.data.SuggestedGoalDTO
import java.time.LocalDate

class GoalsRepository(
    private val api: IGoalsAPI,
) {
    suspend fun createGoals(goals: List<GoalDTO>): Result<List<GoalDTO>> =
        runCatching {
            api.createGoals(goals)
        }

    suspend fun updateGoalProgress(goalId: String): Result<GoalDTO> =
        runCatching {
            api.updateGoalProgress(goalId)
        }

    suspend fun updateGoal(goal: GoalDTO): Result<GoalDTO> =
        runCatching {
            api.updateGoal(goal.id, goal)
        }

    suspend fun completeGoal(goalId: String): Result<GoalDTO> =
        runCatching {
            api.completeGoal(goalId)
        }

    suspend fun uncompleteGoal(goalId: String): Result<GoalDTO> =
        runCatching {
            api.uncompleteGoal(goalId)
        }

    // region GET
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getGoalsByDate(date: LocalDate): Result<List<GoalDTO>?> =
        runCatching {
            api.getGoalsByDate(dateToString(date))
        }

    suspend fun getAllGoals(): Result<List<GoalDTO>> =
        runCatching {
            api.getAllGoals()
        }

    suspend fun getGoalsInProgress(): Result<List<GoalDTO>> =
        runCatching {
            api.getGoalsInProgress()
        }

    suspend fun getSuggestedGoals(): Result<List<SuggestedGoalDTO>> =
        runCatching {
            api.getSuggestedGoals()
        }
    // endregion
}
