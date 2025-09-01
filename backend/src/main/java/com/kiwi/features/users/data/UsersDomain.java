package com.kiwi.features.users.data;

import com.kiwi.common.types.Email;
import com.kiwi.common.types.Password;

import java.time.LocalDate;
import java.util.Objects;

public class UsersDomain {
    private Email email;
    private Password password;
    private LocalDate registerDate;

    public UsersDomain(Email email, Password password, LocalDate registerDate) {
        this.email = email;
        this.password = password;
        this.registerDate = registerDate;
    }

    public Email getEmail() { return email; }
    public void setEmail(Email email) { this.email = email; }

    public Password getPassword() { return password; }
    public void setPassword(Password password) { this.password = password; }


    public LocalDate getRegisterDate() { return registerDate; }
    public void setRegisterDate(LocalDate registerDate) { this.registerDate = registerDate; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UsersDomain other = (UsersDomain) o;
        return Objects.equals(email, other.email) &&
                Objects.equals(password, other.password) &&
                Objects.equals(registerDate, other.registerDate);
    }

}
