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
    var isProcessing by remember { mutableStateOf(false) }

    // Escuchar notificaciones entrantes
    LaunchedEffect(Unit) {
        notificationManager.notifications.collect { event ->
            queue.add(event)
        }
    }

//    // Procesar cola de notificaciones cuando hay elementos y no se está procesando
//    LaunchedEffect(queue.size, isProcessing) {
//        if (!isProcessing && queue.isNotEmpty() && current == null) {
//            isProcessing = true
//
//            try {
//                // Tomar siguiente notificación de la cola
//                current = queue.removeAt(0)
//                visible = true
//
//                // Esperar 4 segundos antes de ocultar
//                delay(4000)
//
//                // Animar salida
//                visible = false
//                delay(300)
//
//                // Limpiar notificación actual
//                current = null
//                delay(250)
//            } catch (e: Exception) {
//                // Manejar cualquier error para evitar crash
//                e.printStackTrace()
//                current = null
//                visible = false
//            } finally {
//                isProcessing = false
//            }
//        }
//    }

//    Box(modifier = modifier) {
//        Column(
//            modifier = Modifier.padding(getResponsiveSizeHeight(Spacing.large)),
//        ) {
//            AnimatedVisibility(
//                visible = visible,
//                enter =
//                    slideInVertically(
//                        initialOffsetY = { -it },
//                        animationSpec = tween(300),
//                    ),
//                exit =
//                    slideOutVertically(
//                        targetOffsetY = { -it },
//                        animationSpec = tween(300),
//                    ),
//            ) {
//                // Renderizar el contenido composable de la notificación
//                current?.content?.invoke()
//            }
//        }
//    }
}
