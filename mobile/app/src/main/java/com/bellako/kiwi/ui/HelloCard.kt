import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import com.bellako.kiwi.network.RetrofitClient

@Composable
fun HelloCard(viewModel: HelloViewModel, id: Int) {
    val state : HelloState = viewModel.state.value

    LaunchedEffect(id) {
        if (state.status == "idle") {
            viewModel.fetchMessage(id)
        }
    }

    when (state.status) {
        "loading" -> {
            CircularProgressIndicator()
            Text("Loading...")
        }
        "succeeded" -> {
            Text("Message: ${state.message}")
        }
        "failed" -> {
            Text("Error: ${state.message}")
        }
        else -> {
            Text("Welcome!")
        }
    }
}

@Preview
@Composable
fun PreviewHelloCard() {
    HelloCard(viewModel = HelloViewModel(RetrofitClient.apiService), id = 1)
}
