package com.bellako.kiwi.features.goals.model

import com.bellako.kiwi.features.goals.data.GoalDTO
import com.bellako.kiwi.features.goals.data.GoalsListDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface IGoalsAPI {
    @POST("api/user/goals")
    suspend fun createGoals(
        @Body dto: GoalsListDTO,
    ): GoalsListDTO

    @PUT("api/user/goals")
    suspend fun updateGoal(
        @Body dto: GoalDTO,
    ): GoalDTO

    @GET("api/user/goals")
    suspend fun getGoalsByDate(
        @Query("date") date: String,
    ): GoalsListDTO?

    @GET("api/user/goals/all")
    suspend fun getAllGoals(): List<GoalsListDTO>
}
