package com.kiwi.users;

public class UsersTestFactory {
    public static UsersDTO validUserDTO() {
        return new UsersDTO(
                "finn@thehuman.com",
                "Math3matical!"
        );
    }
    
    public static UsersDTO invalidUserDTO() {
        return new UsersDTO(
                "bmolovesfootball",
                "kk"
        );
    }
}
