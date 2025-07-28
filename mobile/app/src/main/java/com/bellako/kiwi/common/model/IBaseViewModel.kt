package com.bellako.kiwi.common.model

import com.bellako.kiwi.common.data.UIState
import kotlinx.coroutines.flow.StateFlow

interface IBaseViewModel<T> {
    val state: StateFlow<T?>
    val isLoading: StateFlow<Boolean>
    val uiState: StateFlow<UIState<Unit>>

    fun <T> handleResult(result: Result<T>, successAction: () -> Unit) : Result<Unit>
    suspend fun <T> handleResultSuspend(result: Result<T>, successAction: suspend () -> Unit) : Result<Unit>
    fun mapExceptionToUIState(e: Throwable): UIState<Unit>
    fun resetUiState()
}
