package com.bellako.kiwi.features.users.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.common.utils.DateUtils.formatDate
import java.time.LocalDate

object UsersDataMapper {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(dto: UsersDTO): Result<UsersDomain> {
        val emailResult = Email.of(dto.email)
        return emailResult.fold(
            onSuccess = { validEmail ->
                val passwordResult = Password.of(dto.password)
                passwordResult.fold(
                    onSuccess = { validPassword ->
                        Result.success(UsersDomain(validEmail, validPassword, LocalDate.parse(dto.registerDate)))
                    },
                    onFailure = { err -> Result.failure(err) },
                )
            },
            onFailure = { err -> Result.failure(err) },
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(state: UsersState): Result<UsersDomain> {
        val emailResult = Email.of(state.email)
        return emailResult.fold(
            onSuccess = { validEmail ->
                val passwordResult = Password.of(state.password)
                passwordResult.fold(
                    onSuccess = { validPassword ->
                        Result.success(UsersDomain(validEmail, validPassword, LocalDate.parse(state.registerDate)))
                    },
                    onFailure = { err -> Result.failure(err) },
                )
            },
            onFailure = { err -> Result.failure(err) },
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toState(domain: UsersDomain): UsersState =
        UsersState(
            email = domain.email.value,
            password = domain.password.value,
            registerDate = formatDate(domain.registerDate),
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun toState(dto: UsersDTO): Result<UsersState> {
        val domain = toDomain(dto)
        return domain.fold(
            onSuccess = { validDomain ->
                Result.success(toState(validDomain))
            },
            onFailure = { err -> Result.failure(err) },
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toDTO(domain: UsersDomain): UsersDTO =
        UsersDTO(
            email = domain.email.value,
            password = domain.password.value,
            registerDate = formatDate(domain.registerDate),
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun toDTO(state: UsersState): Result<UsersDTO> {
        val domain = toDomain(state)
        return domain.fold(
            onSuccess = { validDomain ->
                Result.success(toDTO(validDomain))
            },
            onFailure = { err -> Result.failure(err) },
        )
    }
}
