package com.kiwi.features.users.data;

import com.kiwi.common.types.Email;
import com.kiwi.common.types.Password;

import java.util.Objects;

public class UsersDomain {
    private Email email;
    private Password password;

    public UsersDomain(Email email, Password password) {
        this.email = email;
        this.password = password;
    }

    public Email getEmail() { return email; }
    public void setEmail(Email email) { this.email = email; }

    public Password getPassword() { return password; }
    public void setPassword(Password password) { this.password = password; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UsersDomain other = (UsersDomain) o;
        return Objects.equals(email, other.email) &&
                Objects.equals(password, other.password);
    }

}
