package com.bellako.kiwi.common.model

import androidx.lifecycle.ViewModel
import com.bellako.kiwi.common.data.UIState
import com.bellako.kiwi.common.utils.HTTPUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException
import java.net.HttpURLConnection.HTTP_INTERNAL_ERROR

abstract class BaseViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UIState<Unit>>(UIState.Idle)
    val uiState: StateFlow<UIState<Unit>> = _uiState.asStateFlow()

    fun setUiState(inUiState: UIState<Unit>) {
        _uiState.value = inUiState
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun setIsLoading(inIsLoading: Boolean) {
        _isLoading.value = inIsLoading
    }

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
                if (e.code() >= HTTP_INTERNAL_ERROR) {
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
