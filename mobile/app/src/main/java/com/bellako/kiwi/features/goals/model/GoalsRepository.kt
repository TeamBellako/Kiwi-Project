package com.bellako.kiwi.features.goals.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.utils.DateUtils.dateToString
import com.bellako.kiwi.features.goals.data.GoalDTO
import com.bellako.kiwi.features.goals.data.GoalsListDTO
import com.bellako.kiwi.features.goals.data.SuggestedGoalDTO
import java.time.LocalDate

class GoalsRepository(
    private val api: IGoalsAPI,
) {
    suspend fun createGoals(dto: GoalsListDTO): Result<GoalsListDTO> =
        runCatching {
            api.createGoals(dto)
        }

    suspend fun completeGoal(goalId: String): Result<GoalDTO> =
        runCatching {
            api.completeGoal(goalId)
        }

    suspend fun uncompleteGoal(goalId: String): Result<GoalDTO> =
        runCatching {
            api.uncompleteGoal(goalId)
        }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getGoalsByDate(date: LocalDate): Result<GoalsListDTO?> =
        runCatching {
            api.getGoalsByDate(dateToString(date))
        }

    suspend fun getAllGoals(): Result<List<GoalsListDTO>> =
        runCatching {
            api.getAllGoals()
        }

    suspend fun getGoalsInProgress(): Result<List<GoalsListDTO>> =
        runCatching {
            api.getGoalsInProgress()
        }

    suspend fun getSuggestedGoals(): Result<List<SuggestedGoalDTO>> =
        runCatching {
            api.getSuggestedGoals()
        }
}
