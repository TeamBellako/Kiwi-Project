package com.bellako.kiwi

import com.bellako.kiwi.network.AuthRepository
import com.bellako.kiwi.login.ILoginAPI
import com.bellako.kiwi.login.ILoginViewModel
import com.bellako.kiwi.login.LoginRepository
import com.bellako.kiwi.login.LoginTestFactory.incorrectPasswordLoginDTO
import com.bellako.kiwi.login.LoginTestFactory.validLoginDTO
import com.bellako.kiwi.login.LoginViewModel
import com.bellako.kiwi.utils.HTTPUtils.createFakeHttpException
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import retrofit2.Response
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LoginIntegrationTest {
    private lateinit var api: ILoginAPI
    private lateinit var repository: LoginRepository
    private lateinit var viewModel: ILoginViewModel
    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        api = mock(ILoginAPI::class.java)
        authRepository = AuthRepository()
        repository = LoginRepository(api)
        viewModel = LoginViewModel(repository, authRepository)
    }

    @Test
    fun `signup with a valid user`() = runTest {
        whenever(api.signup(any())).thenReturn(Response.success(Unit))
        whenever(api.login(any())).thenReturn(mapOf("jwt" to "mockJwt"))

        val result : Result<Unit> = viewModel.signup(validLoginDTO().toState())

        assertTrue(result.isSuccess)
    }

    @Test
    fun `signup with a duplicated user`() = runTest {
        doThrow(createFakeHttpException(409)).whenever(api).signup(any())

        val result : Result<Unit> = viewModel.signup(validLoginDTO().toState())

        assertTrue(result.isFailure)
    }

    @Test
    fun `login with a valid user`() = runTest {
        whenever(api.login(any())).thenReturn(mapOf("jwt" to "mockJwt"))

        val result : Result<Unit> = viewModel.login(validLoginDTO().toState())

        assertTrue(result.isSuccess)
        assertTrue(authRepository.isJwtTokenSet())
    }

    @Test
    fun `login with a valid user but jwt is missing`() = runTest {
        whenever(api.login(any())).thenReturn(emptyMap())

        val result : Result<Unit> = viewModel.login(validLoginDTO().toState())

        assertTrue(result.isFailure)
        assertFalse(authRepository.isJwtTokenSet())
    }

    @Test
    fun `login with an incorrect password`() = runTest {
        doThrow(createFakeHttpException(401)).whenever(api).login(any())

        val result : Result<Unit> = viewModel.login(incorrectPasswordLoginDTO().toState())

        assertTrue(result.isFailure)
    }
}