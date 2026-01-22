package com.bellako.kiwi.features.appbar.model

import com.bellako.kiwi.R
import com.bellako.kiwi.common.data.ScreenRoutes
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.features.appbar.data.AppBarItem
import com.bellako.kiwi.features.appbar.data.AppBarState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AppBarViewModel
    @Inject
    constructor() :
    BaseViewModel(),
        IAppBarViewModel {
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

        override fun onNewContent(route: String) {
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
