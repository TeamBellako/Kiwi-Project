import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kiwi.network.APIService
import com.kiwi.network.HelloResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class HelloViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var apiService: APIService

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun testFetchMessage_success() = runTest {
        val mockResponse = Response.success(HelloResponse("Hello World!"))
        whenever(apiService.getMessage(1)).thenReturn(mockResponse)

        val viewModel = HelloViewModel(apiService)

        viewModel.fetchMessage(1)

        assert(viewModel.state.value.status == "succeeded")
        assert(viewModel.state.value.message == "Hello World!")
    }

    @Test
    fun testFetchMessage_failure() = runTest {
        val errorMessage = "Error fetching message"

        val mockResponse = Response.error<HelloResponse>(
            500,
            "".toResponseBody(null)
        )
        whenever(apiService.getMessage(1)).thenReturn(mockResponse)

        val viewModel = HelloViewModel(apiService)

        viewModel.fetchMessage(1)

        assert(viewModel.state.value.status == "failed")
        assert(viewModel.state.value.message == errorMessage)
    }
}
