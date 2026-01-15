package com.bellako.kiwi.features.notifications.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.bellako.kiwi.features.notifications.model.NotificationEvent
import com.bellako.kiwi.features.notifications.model.NotificationManager
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlinx.coroutines.delay

/**
 * Overlay de notificaciones que muestra notificaciones de diferentes módulos
 * en la parte superior de la pantalla.
 *
 * Este componente gestiona una cola de notificaciones y las muestra una a una
 * con animaciones de entrada y salida.
 *
 * @param notificationManager El gestor de notificaciones compartido
 * @param modifier Modificador opcional para personalizar el layout
 */
@Suppress("MagicNumber")
@Composable
fun NotificationOverlay(
    notificationManager: NotificationManager,
    modifier: Modifier = Modifier,
) {
    val queue = remember { mutableStateListOf<NotificationEvent>() }
    var current by remember { mutableStateOf<NotificationEvent?>(null) }
    var visible by remember { mutableStateOf(false) }

    // Escuchar notificaciones entrantes
    LaunchedEffect(Unit) {
        notificationManager.notifications.collect { event ->
            queue += event
        }
    }

    // Procesar cola con busy-loop
    LaunchedEffect(Unit) {
        while (true) {
            if (current == null && queue.isNotEmpty()) {
                current = queue.removeAt(0)

                visible = true

                delay(4000)

                visible = false
                delay(300)

                current = null
                delay(250)
            }

            delay(16) // busy-loop
        }
    }

    // Escuchar solicitudes externas de dismiss para animar la salida
    LaunchedEffect(notificationManager) {
        notificationManager.dismissRequests.collect {
            if (current != null && visible) {
                // Ejecutar animación de salida como si se cumpliera el timeout
                visible = false
                // esperar a la animación de salida
                delay(300)
                current = null
                // mantener el mismo ritmo que el flujo original
                delay(250)
            }
        }
    }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier.padding(getResponsiveSizeHeight(Spacing.large)),
        ) {
            AnimatedVisibility(
                visible = visible,
                enter =
                    slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = tween(300),
                    ),
                exit =
                    slideOutVertically(
                        targetOffsetY = { -it },
                        animationSpec = tween(300),
                    ),
            ) {
                // Renderizar el contenido composable de la notificación
                current?.content?.invoke()
            }
        }
    }
}
