package com.bellako.kiwi.features.conversations.exceptions

/**
 * Excepciones personalizadas para manejo de errores en Conversations
 */
class ConversationNotFoundException(
    message: String = "Conversation not found",
) : Exception(message)

class ConversationValidationException(
    message: String = "Invalid conversation data",
) : Exception(message)

class UnauthorizedException(
    message: String = "Unauthorized access",
) : Exception(message)

class NetworkException(
    message: String = "Network error occurred",
) : Exception(message)

class ServerException(
    message: String = "Server error occurred",
) : Exception(message)
