package com.bellako.kiwi.users

import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.users.UsersTestFactory.incorrectPasswordUsersDTO
import com.bellako.kiwi.users.UsersTestFactory.validUsersDTO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UsersIntegrationTest {
    private lateinit var api: IUsersAPI
    private lateinit var repository: UsersRepository
    private lateinit var viewModel: UsersViewModel

    @Before
    fun setUp() {
        api = mock(IUsersAPI::class.java)
        repository = UsersRepository(api)
        viewModel = UsersViewModel(repository)
    }

    @After
    fun tearDown() {
        viewModel.viewModelScope.cancel()
    }

    @Test
    fun `signup with a valid user`() = runTest {
        whenever(api.signup(any())).thenReturn(Response.success(null))

        val result : Result<Unit> = viewModel.signup(validUsersDTO().toState())

        assertTrue(result.isSuccess)
    }

    @Test
    fun `signup with a duplicated user`() = runTest {
        val mockException = HttpException(Response.error<Any>(
            409,
            "Error".toResponseBody(null)
        ))
        doThrow(mockException).whenever(api).signup(any())

        val result : Result<Unit> = viewModel.signup(validUsersDTO().toState())

        assertTrue(result.isFailure)
    }

    @Test
    fun `login with a valid user`() = runTest {
        whenever(api.login(any())).thenReturn(Result.success("mockJwt"))

        val result : Result<String> = viewModel.login(validUsersDTO().toState())

        assertTrue(result.isSuccess)
        assertEquals(result.getOrNull(), "mockJwt")
    }

    @Test
    fun `login with an incorrect password`() = runTest {
        val mockException = HttpException(Response.error<Any>(
            401,
            "Error".toResponseBody(null)
        ))
        doThrow(mockException).whenever(api).login(any())

        val result = viewModel.login(incorrectPasswordUsersDTO().toState())

        assertTrue(result.isFailure)
    }
}