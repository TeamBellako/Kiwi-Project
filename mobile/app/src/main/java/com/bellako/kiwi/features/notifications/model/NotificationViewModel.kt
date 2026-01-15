package com.bellako.kiwi.features.notifications.model

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel que expone el NotificationManager para uso en Composables.
 *
 * Este ViewModel solo existe para permitir que los composables accedan
 * al NotificationManager singleton mediante hiltViewModel().
 *
 * Uso:
 * ```
 * @Composable
 * fun MyScreen(
 *     notificationViewModel: NotificationViewModel = hiltViewModel()
 * ) {
 *     val notificationManager = notificationViewModel.notificationManager
 * }
 * ```
 */
@HiltViewModel
class NotificationViewModel
    @Inject
    constructor(
        val notificationManager: NotificationManager,
    ) : ViewModel()
