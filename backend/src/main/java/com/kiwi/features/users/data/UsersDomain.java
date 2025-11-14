package com.kiwi.features.users.data;

import com.kiwi.common.types.Email;
import com.kiwi.common.types.Password;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Setter
@Getter
public class UsersDomain {
    private Email email;
    private Password password;
    private LocalDate registerDate;

    public UsersDomain(Email email, Password password, LocalDate registerDate) {
        this.email = email;
        this.password = password;
        this.registerDate = registerDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UsersDomain other = (UsersDomain) o;
        return Objects.equals(email, other.email) &&
                Objects.equals(password, other.password) &&
                Objects.equals(registerDate, other.registerDate);
    }

}
