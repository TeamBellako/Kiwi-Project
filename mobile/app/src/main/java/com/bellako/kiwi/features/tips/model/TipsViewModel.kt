package com.bellako.kiwi.features.tips.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.common.services.eventbus.EventPayload
import com.bellako.kiwi.common.services.eventbus.EventType
import com.bellako.kiwi.common.services.eventbus.listenToEvent
import com.bellako.kiwi.common.utils.Logger.warn
import com.bellako.kiwi.features.tips.data.TipDomain
import com.bellako.kiwi.features.tips.data.TipMapper
import com.bellako.kiwi.features.tips.data.TipState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.security.GeneralSecurityException
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

        private val _isVisible = MutableStateFlow(false)
        val isVisible: StateFlow<Boolean> = _isVisible.asStateFlow()

        init {
            GlobalScope.launch(Dispatchers.Main) {
                listenToEvent(EventType.START_TIP) { eventPayload ->
                    val payload = eventPayload as EventPayload.EntityIdPayload
                    showTip(payload.targetEntityId.toLong())
                }
            }
        }

        override suspend fun getTip(id: Long): TipDomain {
            val dto = tipsRepository.getTip(id)
            return TipMapper.toDomain(dto)
        }

        fun showTip(id: Long) {
            viewModelScope.launch {
                try {
                    val tip: TipDomain = getTip(id)
                    _state.value = TipMapper.toState(tip)
                    _isVisible.value = true
                } catch (e: GeneralSecurityException) {
                    warn("Encryption error: ${e.message}")
                } catch (e: IOException) {
                    warn("DataStore error: ${e.message}")
                }
            }
        }

        override fun closeTip() {
            _isVisible.value = false
        }
    }
