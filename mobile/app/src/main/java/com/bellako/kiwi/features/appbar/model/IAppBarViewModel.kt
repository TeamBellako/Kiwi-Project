package com.bellako.kiwi.features.appbar.model

import com.bellako.kiwi.common.model.IBaseViewModel
import com.bellako.kiwi.features.appbar.data.AppBarState
import kotlinx.coroutines.flow.StateFlow

interface IAppBarViewModel : IBaseViewModel<AppBarState> {
    override val state: StateFlow<AppBarState>

    fun onNewContent(route: String)

    fun onNewContentVisited(route: String)
}
