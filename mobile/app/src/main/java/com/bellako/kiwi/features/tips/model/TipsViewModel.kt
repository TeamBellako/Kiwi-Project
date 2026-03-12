package com.bellako.kiwi.features.tips.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.services.eventbus.EventPayload
import com.bellako.kiwi.common.services.eventbus.EventType
import com.bellako.kiwi.common.services.eventbus.listenToEvent
import com.bellako.kiwi.features.tips.data.TipState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class TipsViewModel
    @Inject
    constructor(
        private val tipsRepository: TipsRepository,
    ) : BaseViewModel(),
        ITipsViewModel {
        private val _state = MutableStateFlow(TipState(0, "", "", ""))
        override val state: StateFlow<TipState> = _state.asStateFlow()

        init {
            GlobalScope.launch(Dispatchers.Main) {
                listenToEvent(EventType.START_TIP) { eventPayload ->
                    val payload = eventPayload as EventPayload.EntityIdPayload
                    giveSkill(payload.targetEntityId.toLong())
                }
            }
        }

        override fun getTip(id: Long) {
            TODO("Not yet implemented")
        }
    }
