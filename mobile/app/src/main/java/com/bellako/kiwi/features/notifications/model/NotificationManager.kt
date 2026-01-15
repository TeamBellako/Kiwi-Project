package com.bellako.kiwi.features.notifications.model

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestor global de notificaciones que mantiene una cola de eventos
 */
@Singleton
class NotificationManager
    @Inject
    constructor() {
        private val _notificationsChannel = Channel<NotificationEvent>(Channel.UNLIMITED)
        val notifications = _notificationsChannel.receiveAsFlow()

        /**
         * Emite una nueva notificación para ser mostrada
         */
        suspend fun notify(event: NotificationEvent) {
            _notificationsChannel.send(event)
        }

        // Dismiss requests: permite que un modal pida que la overlay anime la salida de la notificacion
        private val _dismissRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
        val dismissRequests: SharedFlow<Unit> = _dismissRequests.asSharedFlow()

        fun dismissCurrent() {
            _dismissRequests.tryEmit(Unit)
        }
    }
