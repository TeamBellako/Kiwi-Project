package com.bellako.kiwi

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.bellako.kiwi.common.utils.HTTPUtils.createFakeHttpException
import com.bellako.kiwi.features.users.model.AuthRepository
import com.bellako.kiwi.features.users.model.IUsersAPI
import com.bellako.kiwi.features.users.model.IUsersViewModel
import com.bellako.kiwi.features.users.model.UsersRepository
import com.bellako.kiwi.features.users.model.UsersViewModel
import com.bellako.kiwi.features.users.tests.UsersTestFactory.invalidUsersDTO
import com.bellako.kiwi.features.users.tests.UsersTestFactory.validLoggedDTO
import com.bellako.kiwi.features.users.tests.UsersTestFactory.validUsersDTO
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import kotlin.test.Test
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
    fun `signup with a valid user`() =
        runTest {
            whenever(api.signup(any())).thenReturn(mapOf("message" to "Created successfully"))
            whenever(api.login(any())).thenReturn(validLoggedDTO())

            viewModel.onEmailChanged(validUsersDTO().email)
            viewModel.onPasswordChanged(validUsersDTO().password)
            val result: Result<Unit> = viewModel.signup(context)

            assertTrue(result.isSuccess)
        }

    @Test
    fun `signup with a duplicated user`() =
        runTest {
            doThrow(createFakeHttpException(409)).whenever(api).signup(any())

            viewModel.onEmailChanged(validUsersDTO().email)
            viewModel.onPasswordChanged(validUsersDTO().password)
            val result: Result<Unit> = viewModel.signup(context)

            assertTrue(result.isFailure)
        }

    @Test
    fun `login with a valid user`() =
        runTest {
            whenever(api.login(any())).thenReturn(validLoggedDTO())

            viewModel.onEmailChanged(validUsersDTO().email)
            viewModel.onPasswordChanged(validUsersDTO().password)
            val result: Result<Unit> = viewModel.login(context)

            assertTrue(result.isSuccess)
            assertTrue(authRepository.isJwtTokenSet())
        }

    @Test
    fun `login with an incorrect password`() =
        runTest {
            doThrow(createFakeHttpException(HTTP_UNAUTHORIZED)).whenever(api).login(any())

            viewModel.onEmailChanged(validUsersDTO().email)
            viewModel.onPasswordChanged(invalidUsersDTO().password)
            val result: Result<Unit> = viewModel.login(context)

            assertTrue(result.isFailure)
        }
}
