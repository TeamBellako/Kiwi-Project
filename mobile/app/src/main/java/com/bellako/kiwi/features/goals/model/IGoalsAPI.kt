package com.bellako.kiwi.features.goals.model

import com.bellako.kiwi.features.goals.data.GoalDTO
import com.bellako.kiwi.features.goals.data.SuggestedGoalDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface IGoalsAPI {
    @POST("api/user/goals")
    suspend fun createGoals(
        @Body goals: List<GoalDTO>,
    ): List<GoalDTO>

    @PATCH("api/user/goals/{goalId}/update_progress")
    suspend fun updateGoalProgress(
        @Path("goalId") goalId: Long,
    ): GoalDTO

    @PATCH("api/user/goals/{goalId}/update")
    suspend fun updateGoal(
        @Path("goalId") goalId: Long,
        @Body goal: GoalDTO,
    ): GoalDTO

    @PATCH("api/user/goals/{goalId}/complete")
    suspend fun completeGoal(
        @Path("goalId") goalId: Long,
    ): GoalDTO

    @PATCH("api/user/goals/{goalId}/uncompleted")
    suspend fun uncompleteGoal(
        @Path("goalId") goalId: Long,
    ): GoalDTO

    @GET("api/user/goals/{goalId}")
    suspend fun getGoalById(
        @Query("date") date: Long,
    ): GoalDTO?

    @GET("api/user/goals")
    suspend fun getGoalsByDate(
        @Query("date") date: String,
    ): List<GoalDTO>?

    @GET("api/user/goals/all")
    suspend fun getAllGoals(): List<GoalDTO>

    @GET("api/user/goals/in_progress")
    suspend fun getGoalsInProgress(): List<GoalDTO>

    @GET("api/user/goals/suggestions")
    suspend fun getSuggestedGoals(): List<SuggestedGoalDTO>

    @GET("api/user/goals/app_usage")
    suspend fun getAppGoals(): List<GoalDTO>
}
