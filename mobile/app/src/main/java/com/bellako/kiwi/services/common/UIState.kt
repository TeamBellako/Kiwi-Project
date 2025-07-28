package com.bellako.kiwi.services.common

sealed class UIState<out T> {
    object Idle : UIState<Nothing>()
    object Loading : UIState<Nothing>()
    data class Success<T>(val data: T) : UIState<T>()
    data class Error(val message: String = "Something went wrong.") : UIState<Nothing>()
    object GeneralError : UIState<Nothing>()
}