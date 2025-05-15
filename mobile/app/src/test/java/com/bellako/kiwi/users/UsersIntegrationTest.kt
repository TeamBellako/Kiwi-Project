package com.bellako.kiwi.users

import com.bellako.kiwi.users.UsersTestFactory.incorrectPasswordUsersDTO
import com.bellako.kiwi.users.UsersTestFactory.validUsersDTO
import com.bellako.kiwi.utils.HTTPUtils.createFakeHttpException
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import retrofit2.Response
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UsersIntegrationTest {
    private lateinit var api: IUsersAPI
    private lateinit var repository: UsersRepository
    private lateinit var viewModel: IUsersViewModel

    @Before
    fun setUp() {
        api = mock(IUsersAPI::class.java)
        repository = UsersRepository(api)
        viewModel = UsersViewModel(repository)
    }

    @Test
    fun `signup with a valid user`() = runTest {
        whenever(api.signup(any())).thenReturn(Response.success(null))

        val result : Result<Unit> = viewModel.signup(validUsersDTO().toState())

        assertTrue(result.isSuccess)
    }

    @Test
    fun `signup with a duplicated user`() = runTest {
        doThrow(createFakeHttpException(409)).whenever(api).signup(any())

        val result : Result<Unit> = viewModel.signup(validUsersDTO().toState())

        assertTrue(result.isFailure)
    }

    @Test
    fun `login with a valid user`() = runTest {
        val mockJwtToken : String = "mockJwt"
        whenever(api.login(any())).thenReturn(Result.success(mockJwtToken))

        val result : Result<String> = viewModel.login(validUsersDTO().toState())

        assertTrue(result.isSuccess)
        assertEquals(result.getOrNull(), mockJwtToken)
    }

    @Test
    fun `login with an incorrect password`() = runTest {
        doThrow(createFakeHttpException(401)).whenever(api).login(any())

        val result = viewModel.login(incorrectPasswordUsersDTO().toState())

        assertTrue(result.isFailure)
    }
}