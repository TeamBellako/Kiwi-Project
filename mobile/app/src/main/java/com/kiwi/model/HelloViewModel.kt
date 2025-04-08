import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwi.network.APIService
import kotlinx.coroutines.launch

data class HelloState(
    val status: String = "idle",   // 'idle', 'loading', 'succeeded', 'failed'
    val message: String = ""
)

class HelloViewModel(private val apiService: APIService) : ViewModel() {

    private val _state = mutableStateOf(HelloState())
    var state = _state

    fun fetchMessage(id: Int) {
        _state.value = HelloState(status = "loading")
        viewModelScope.launch {
            try {
                val response = apiService.getMessage(id)
                if (response.isSuccessful) {
                    _state.value = HelloState(status = "succeeded", message = response.body()?.message ?: "No message")
                } else {
                    _state.value = HelloState(status = "failed", message = "Error fetching message")
                }
            } catch (e: Exception) {
                _state.value = HelloState(status = "failed", message = e.message ?: "Unknown error")
            }
        }
    }
}



