package com.bellako.kiwi.features.goals.model

import com.bellako.kiwi.features.goals.data.GoalDTO
import com.bellako.kiwi.features.goals.data.UserAppUsageDTO
import com.bellako.kiwi.features.goals.data.UserGoalStatusDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface IGoalsAPI {
    @POST("api/user/goals")
    suspend fun createGoals(
        @Body goals: List<UserGoalStatusDTO>,
    ): List<UserGoalStatusDTO>

    @PATCH("api/user/goals/{goalId}/update_progress")
    suspend fun updateGoalProgress(
        @Path("goalId") goalId: Long,
    ): UserGoalStatusDTO

    @PATCH("api/user/goals/{goalId}/update")
    suspend fun updateGoal(
        @Path("goalId") goalId: Long,
        @Body goal: UserGoalStatusDTO,
    ): UserGoalStatusDTO

    @PATCH("api/user/goals/{goalId}/complete")
    suspend fun completeGoal(
        @Path("goalId") goalId: Long,
    ): UserGoalStatusDTO

    @PATCH("api/user/goals/{goalId}/uncompleted")
    suspend fun uncompleteGoal(
        @Path("goalId") goalId: Long,
    ): UserGoalStatusDTO

    @GET("api/user/goals/{goalId}")
    suspend fun getGoalById(
        @Path("goalId") goalId: Long,
    ): UserGoalStatusDTO

    @GET("api/user/goals")
    suspend fun getGoalsByDate(
        @Query("date") date: String,
    ): List<UserGoalStatusDTO>?

    @GET("api/user/goals/all")
    suspend fun getAllGoals(): List<UserGoalStatusDTO>

    @GET("api/user/goals/in_progress")
    suspend fun getGoalsInProgress(): List<UserGoalStatusDTO>

    @GET("api/user/goals/suggestions")
    suspend fun getGoalDefinitions(): List<GoalDTO>

    @GET("api/user/goals/app_usage")
    suspend fun getAppGoals(): List<UserGoalStatusDTO>

    @GET("api/user/goals/skill")
    suspend fun getSkillGoals(): List<UserGoalStatusDTO>

    @POST("api/user/app-usage")
    suspend fun saveAppUsageBaseline(
        @Body dto: UserAppUsageDTO,
    ): UserAppUsageDTO

    @POST("api/user/goals/app_usage/auto_review")
    suspend fun autoReviewAppUsageGoals(): List<UserGoalStatusDTO>
}
