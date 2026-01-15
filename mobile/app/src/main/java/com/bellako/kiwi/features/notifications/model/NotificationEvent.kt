package com.bellako.kiwi.features.notifications.model

import androidx.compose.runtime.Composable

/**
 * Evento de notificación genérico que puede ser usado por cualquier módulo
 */
sealed class NotificationEvent {
    /**
     * El contenido composable que se mostrará en la notificación
     */
    abstract val content: @Composable () -> Unit

    /**
     * Notificación de Quest
     */
    data class Quest(
        override val content: @Composable () -> Unit,
    ) : NotificationEvent()

    /**
     * Notificación de Goal
     */
    data class Goal(
        override val content: @Composable () -> Unit,
    ) : NotificationEvent()

    /**
     * Notificación genérica para otros módulos
     */
    data class Generic(
        override val content: @Composable () -> Unit,
    ) : NotificationEvent()
}
