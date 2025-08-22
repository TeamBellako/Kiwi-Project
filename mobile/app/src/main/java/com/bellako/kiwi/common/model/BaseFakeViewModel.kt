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
    @Suppress("ktlint:standard:backing-property-naming")
    protected val _uiState = MutableStateFlow<UIState<Unit>>(UIState.Idle)
    val uiState: StateFlow<UIState<Unit>> = _uiState.asStateFlow()

    @Suppress("ktlint:standard:backing-property-naming")
    protected val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun handleError(error: Throwable) {
        _uiState.value =
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
            }
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
        _uiState.value = if (isLoading) UIState.Loading else UIState.Idle
    }

    fun handleSuccess() {
        _uiState.value = UIState.Success(Unit)
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

    fun resetUiState() {
        _uiState.value = UIState.Idle
    }

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
