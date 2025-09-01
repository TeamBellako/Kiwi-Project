package com.kiwi.features.users.data;

import com.kiwi.common.types.Email;
import com.kiwi.common.types.Password;

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

}
