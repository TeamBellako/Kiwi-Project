package com.bellako.kiwi.common.model

import androidx.lifecycle.ViewModel
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.utils.HTTPUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException

abstract class BaseViewModel : ViewModel() {
    protected val _uiState = MutableStateFlow<UIState<Unit>>(UIState.Idle)
    val uiState: StateFlow<UIState<Unit>> = _uiState.asStateFlow()

    protected open val _isLoading = MutableStateFlow(false)
    open val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    protected fun <T> failureWithError(message: String): Result<T> {
        _uiState.value = UIState.Error(message)
        return Result.failure(Exception(message))
    }

    fun <T> handleResult(
        result: Result<T>,
        successAction: () -> Unit,
    ): Result<Unit> =
        result.fold(
            onSuccess = {
                _uiState.value = UIState.Success(Unit)
                successAction()
                Result.success(Unit)
            },
            onFailure = { throwable ->
                _uiState.value = mapExceptionToUIState(throwable)
                Result.failure(throwable)
            },
        )

    suspend fun <T> handleResultSuspend(
        result: Result<T>,
        successAction: suspend () -> Unit,
    ): Result<Unit> =
        result.fold(
            onSuccess = {
                _uiState.value = UIState.Success(Unit)
                successAction()
                Result.success(Unit)
            },
            onFailure = { throwable ->
                _uiState.value = mapExceptionToUIState(throwable)
                Result.failure(throwable)
            },
        )

    fun mapExceptionToUIState(e: Throwable): UIState<Unit> =
        when (e) {
            is HttpException -> {
                if (e.code() >= 500) {
                    UIState.GeneralError
                } else {
                    UIState.Error(extractHttpExceptionMessage(e))
                }
            }
            else -> UIState.GeneralError
        }

    fun resetUiState() {
        _uiState.value = UIState.Idle
    }

    protected fun extractHttpExceptionMessage(exception: HttpException): String {
        val errorBody = exception.response()?.errorBody()?.string()
        return HTTPUtils.parseErrorMessage(errorBody) ?: "Something went wrong"
    }
}
