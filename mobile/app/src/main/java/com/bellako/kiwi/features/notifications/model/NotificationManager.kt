package com.bellako.kiwi.features.notifications.model

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestor global de notificaciones que mantiene una cola de eventos
 * para ser mostrados en la UI
 */
@Singleton
class NotificationManager
    @Inject
    constructor() {
        private val _notifications = MutableSharedFlow<NotificationEvent>()
        val notifications: SharedFlow<NotificationEvent> = _notifications.asSharedFlow()

        /**
         * Emite una nueva notificación para ser mostrada
         */
        suspend fun notify(event: NotificationEvent) {
            _notifications.emit(event)
        }
    }
