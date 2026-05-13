package com.bellako.kiwi.common.model

import androidx.lifecycle.ViewModel
import com.bellako.kiwi.common.data.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection.HTTP_INTERNAL_ERROR

open class BaseFakeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UIState<Unit>>(UIState.Idle)
    val uiState: StateFlow<UIState<Unit>> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun setIsLoading(inIsLoading: Boolean) {
        _isLoading.value = inIsLoading
    }

    fun setUiState(inUiState: UIState<Unit>) {
        _uiState.value = inUiState
    }

    fun resetUiState() {
        setUiState(UIState.Idle)
    }

    fun handleError(error: Throwable) {
        setUiState(
            when (error) {
                is HttpException -> {
                    if (error.code() >= HTTP_INTERNAL_ERROR) {
                        UIState.GeneralError
                    } else {
                        UIState.Error("Server error: ${error.message()}")
                    }
                }
                is IOException -> UIState.GeneralError
                else -> UIState.GeneralError
            },
        )
    }

    fun handleSuccess() {
        setUiState(UIState.Success(Unit))
    }

    fun <T> handleResult(
        result: Result<T>,
        successAction: () -> Unit,
    ): Result<Unit> =
        result.fold(
            onSuccess = {
                successAction()
                Result.success(Unit)
            },
            onFailure = { throwable ->
                handleError(throwable)
                Result.failure(throwable)
            },
        )

    suspend fun <T> handleResultSuspend(
        result: Result<T>,
        successAction: suspend () -> Unit,
    ): Result<Unit> =
        result.fold(
            onSuccess = {
                successAction()
                Result.success(Unit)
            },
            onFailure = { throwable ->
                handleError(throwable)
                Result.failure(throwable)
            },
        )

    open fun mapExceptionToUIState(e: Throwable): UIState<Unit> =
        when (e) {
            is HttpException -> {
                if (e.code() >= HTTP_INTERNAL_ERROR) {
                    UIState.GeneralError
                } else {
                    UIState.Error("Server error: ${e.message()}")
                }
            }
            is IOException -> UIState.GeneralError
            else -> UIState.GeneralError
        }
}
