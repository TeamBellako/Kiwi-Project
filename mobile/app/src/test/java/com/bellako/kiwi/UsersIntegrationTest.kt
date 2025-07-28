package com.bellako.kiwi

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.bellako.kiwi.features.users.IUsersAPI
import com.bellako.kiwi.features.users.IUsersViewModel
import com.bellako.kiwi.features.users.UsersRepository
import com.bellako.kiwi.features.users.UsersViewModel
import com.bellako.kiwi.services.network.AuthRepository
import com.bellako.kiwi.services.common.HTTPUtils.createFakeHttpException
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import retrofit2.Response
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class UsersIntegrationTest {
    private lateinit var api: IUsersAPI
    private lateinit var repository: UsersRepository
    private lateinit var viewModel: IUsersViewModel
    private lateinit var authRepository: AuthRepository

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setUp() {
        api = mock(IUsersAPI::class.java)
        authRepository = AuthRepository()
        repository = UsersRepository(api)
        viewModel = UsersViewModel(repository, authRepository)
    }

    @Test
    fun `signup with a valid user`() = runTest {
        whenever(api.signup(any())).thenReturn(Response.success(Unit))
        whenever(api.login(any())).thenReturn(mapOf("jwt" to "mockJwt"))

        viewModel.onEmailChanged("finn@thehuman.com")
        viewModel.onPasswordChanged("Math3matical!")
        val result : Result<Unit> = viewModel.signup(context)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `signup with a duplicated user`() = runTest {
        doThrow(createFakeHttpException(409)).whenever(api).signup(any())

        viewModel.onEmailChanged("finn@thehuman.com")
        viewModel.onPasswordChanged("Math3matical!")
        val result : Result<Unit> = viewModel.signup(context)

        assertTrue(result.isFailure)
    }

    @Test
    fun `login with a valid user`() = runTest {
        whenever(api.login(any())).thenReturn(mapOf("jwt" to "mockJwt"))

        viewModel.onEmailChanged("finn@thehuman.com")
        viewModel.onPasswordChanged("Math3matical!")
        val result : Result<Unit> = viewModel.login(context)

        assertTrue(result.isSuccess)
        assertTrue(authRepository.isJwtTokenSet())
    }

    @Test
    fun `login with a valid user but jwt is missing`() = runTest {
        whenever(api.login(any())).thenReturn(emptyMap())

        viewModel.onEmailChanged("finn@thehuman.com")
        viewModel.onPasswordChanged("Math3matical!")
        val result : Result<Unit> = viewModel.login(context)

        assertTrue(result.isFailure)
        assertFalse(authRepository.isJwtTokenSet())
    }

    @Test
    fun `login with an incorrect password`() = runTest {
        doThrow(createFakeHttpException(401)).whenever(api).login(any())

        viewModel.onEmailChanged("finn@thehuman.com")
        viewModel.onPasswordChanged("Math3matical!1")
        val result : Result<Unit> = viewModel.login(context)

        assertTrue(result.isFailure)
    }
}