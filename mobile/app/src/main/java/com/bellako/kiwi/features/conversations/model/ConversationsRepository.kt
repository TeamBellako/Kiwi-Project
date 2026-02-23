package com.bellako.kiwi.features.conversations.model

import com.bellako.kiwi.features.conversations.data.ConversationDTO
import com.bellako.kiwi.features.conversations.exceptions.ConversationNotFoundException
import com.bellako.kiwi.features.conversations.exceptions.NetworkException
import com.bellako.kiwi.features.conversations.exceptions.ServerException
import com.bellako.kiwi.features.conversations.exceptions.UnauthorizedException
import retrofit2.HttpException
import java.io.IOException

/**
 * Repositorio para Conversations.
 */
class ConversationsRepository(
    private val api: IConversationsAPI,
) {
    suspend fun getById(id: Long): Result<ConversationDTO> = handleApiCall {
        api.getConversationById(id)
    }

    /**
     * Maneja llamadas a la API con traducción de errores HTTP específicos
     */
    private suspend fun <T> handleApiCall(apiCall: suspend () -> T): Result<T> =
        try {
            Result.success(apiCall())
        } catch (e: HttpException) {
            val error =
                when (e.code()) {
                    404 -> ConversationNotFoundException(e.message() ?: "Conversation not found")
                    401, 403 -> UnauthorizedException(e.message() ?: "Unauthorized")
                    500, 502, 503 -> ServerException(e.message() ?: "Server error")
                    else -> NetworkException("HTTP error ${e.code()}: ${e.message()}")
                }
            Result.failure(error)
        } catch (e: IOException) {
            Result.failure(NetworkException("Network connection failed: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(NetworkException("Unexpected error: ${e.message}"))
        }
}
