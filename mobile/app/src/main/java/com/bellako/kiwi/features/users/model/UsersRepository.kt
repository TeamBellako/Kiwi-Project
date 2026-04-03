package com.bellako.kiwi.features.users.model

import com.bellako.kiwi.features.users.data.LoggedDTO
import com.bellako.kiwi.features.users.data.LoginDTO
import com.bellako.kiwi.features.users.data.UserPointsDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UsersRepository(
    private val api: IUsersAPI,
) {
    private val _currentPoints = MutableStateFlow(0)
    val currentPoints: StateFlow<Int> = _currentPoints.asStateFlow()

    suspend fun signup(dto: LoginDTO): Result<String> =
        runCatching {
            api.signup(dto)["message"] ?: return Result.failure(
                Exception(api.signup(dto)["error"]),
            )
        }

    suspend fun login(dto: LoginDTO): Result<LoggedDTO> =
        runCatching {
            api.login(dto)
        }

    suspend fun getMyUserPoints(): Result<UserPointsDTO> =
        runCatching {
            api.getMyUserPoints()
        }.also { result ->
            result.onSuccess { points ->
                _currentPoints.value = points.currentPoints
            }
        }
}
