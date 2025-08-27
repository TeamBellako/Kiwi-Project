package com.kiwi.users;

import com.kiwi.features.users.data.UsersDTO;

public class UsersTestFactory {
    public static UsersDTO validUserDTO() {
        return new UsersDTO("finn@thehuman.com", "Math3matic!");
    }
    
    public static UsersDTO invalidUserDTO() {
        return new UsersDTO("football", "kk");
    }
}
