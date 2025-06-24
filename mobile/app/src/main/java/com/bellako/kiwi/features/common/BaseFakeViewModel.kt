package com.bellako.kiwi.features.common

import androidx.lifecycle.ViewModel
import com.bellako.kiwi.services.common.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException
import java.io.IOException

open class BaseFakeViewModel : ViewModel() {

    protected val _uiState = MutableStateFlow<UIState<Unit>>(UIState.Idle)
    val uiState: StateFlow<UIState<Unit>> = _uiState.asStateFlow()

    protected val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun handleError(error: Throwable) {
        _uiState.value = when (error) {
            is HttpException -> {
                if (error.code() >= 500) UIState.GeneralError
                else UIState.Error("Server error: ${error.message()}")
            }
            is IOException -> UIState.GeneralError
            else -> UIState.GeneralError
        }
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
        _uiState.value = if (isLoading) UIState.Loading else UIState.Idle
    }

    fun handleSuccess() {
        _uiState.value = UIState.Success(Unit)
    }

    fun <T> handleResult(result: Result<T>, successAction: () -> Unit): Result<Unit> {
        return result.fold(
            onSuccess = {
                successAction()
                Result.success(Unit)
            },
            onFailure = { throwable ->
                handleError(throwable)
                Result.failure(throwable)
            }
        )
    }

    suspend fun <T> handleResultSuspend(result: Result<T>, successAction: suspend () -> Unit): Result<Unit> {
        return result.fold(
            onSuccess = {
                successAction()
                Result.success(Unit)
            },
            onFailure = { throwable ->
                handleError(throwable)
                Result.failure(throwable)
            }
        )
    }

    open fun mapExceptionToUIState(e: Throwable): UIState<Unit> {
        return when (e) {
            is HttpException -> {
                if (e.code() >= 500) UIState.GeneralError
                else UIState.Error("Server error: ${e.message()}")
            }
            is IOException -> UIState.GeneralError
            else -> UIState.GeneralError
        }
    }
}
