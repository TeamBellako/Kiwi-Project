package com.bellako.kiwi.features.goals.model

import com.bellako.kiwi.features.goals.data.GoalDTO
import com.bellako.kiwi.features.goals.data.GoalsListDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface IGoalsAPI {
    @POST("api/user/goals")
    suspend fun createGoals(
        @Body dto: GoalsListDTO,
    ): GoalsListDTO

    @PATCH("api/user/goals/{goalId}/complete")
    suspend fun completeGoal(
        @Path("goalId") goalId: String,
    ): GoalDTO

    @PATCH("api/user/goals/{goalId}/uncompleted")
    suspend fun uncompleteGoal(
        @Path("goalId") goalId: String,
    ): GoalDTO

    @GET("api/user/goals")
    suspend fun getGoalsByDate(
        @Query("date") date: String,
    ): GoalsListDTO?

    @GET("api/user/goals/all")
    suspend fun getAllGoals(): List<GoalsListDTO>

    @GET("api/user/goals/in_progress")
    suspend fun getGoalsInProgress(): List<GoalsListDTO>
}
