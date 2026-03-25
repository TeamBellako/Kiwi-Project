package com.bellako.kiwi.features.appbar.model

import com.bellako.kiwi.R
import com.bellako.kiwi.common.data.ScreenRoutes
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.services.eventbus.EventType
import com.bellako.kiwi.common.services.eventbus.listenToEvent
import com.bellako.kiwi.features.appbar.data.AppBarItem
import com.bellako.kiwi.features.appbar.data.AppBarState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@HiltViewModel
class AppBarViewModel
    @Inject
    constructor() :
    BaseViewModel(),
        IAppBarViewModel {
        private val _currentRoute = MutableStateFlow<String?>(null)
        val currentRoute: StateFlow<String?> = _currentRoute.asStateFlow()

        init {
            GlobalScope.launch(Dispatchers.Main) {
                listenToEvent(EventType.QUESTS_UPDATED) { eventPayload ->
                    onNewContent(ScreenRoutes.OBJECTIVES)
                }
            }

            GlobalScope.launch(Dispatchers.Main) {
                listenToEvent(EventType.DAILY_GOALS_UPDATED) { eventPayload ->
                    onNewContent(ScreenRoutes.OBJECTIVES)
                }
            }
        }

        private val _state =
            MutableStateFlow(
                AppBarState(
                    items =
                        listOf(
                            AppBarItem(R.drawable.ic_map, ScreenRoutes.HOME),
                            AppBarItem(R.drawable.ic_skills, ScreenRoutes.SKILLS),
                            AppBarItem(R.drawable.ic_objectives, ScreenRoutes.OBJECTIVES),
                            AppBarItem(R.drawable.ic_troops, ScreenRoutes.WIP),
                            AppBarItem(R.drawable.ic_settings, ScreenRoutes.SETTINGS),
                        ),
                ),
            )

        override val state: StateFlow<AppBarState> = _state.asStateFlow()

        // ---------------------------------------------------------------------------------------------

        override fun onRouteChanged(route: String?) {
            _currentRoute.value = route
        }

        // ---------------------------------------------------------------------------------------------

        override fun onNewContent(route: String) {
            if (_currentRoute.value?.startsWith(route) == true) return

            updateItem(route) {
                copy(hasNewContent = true)
            }
        }

        override fun onNewContentVisited(route: String) {
            updateItem(route) {
                copy(hasNewContent = false)
            }
        }

        // ---------------------------------------------------------------------------------------------

        private fun updateItem(
            route: String,
            transform: AppBarItem.() -> AppBarItem,
        ) {
            _state.update { current ->
                current.copy(
                    items =
                        current.items.map { item ->
                            if (item.route == route) item.transform() else item
                        },
                )
            }
        }
    }
