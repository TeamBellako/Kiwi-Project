package com.bellako.kiwi.features.notifications.controller

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager
    @Inject
    constructor() {
        private val _notifications =
            MutableSharedFlow<NotificationEvent>(
                extraBufferCapacity = 10,
                onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST,
            )

        val notifications: SharedFlow<NotificationEvent> =
            _notifications.asSharedFlow()

        fun notify(event: NotificationEvent) {
            _notifications.tryEmit(event)
        }

        // Explicit request to force dismiss
        private val _dismissRequests =
            MutableSharedFlow<Unit>(extraBufferCapacity = 1)

        val dismissRequests: SharedFlow<Unit> =
            _dismissRequests.asSharedFlow()

        fun dismissCurrent() {
            _dismissRequests.tryEmit(Unit)
        }
    }
