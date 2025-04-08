package com.kiwi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "hello_db")
public class HelloDB {
    @Id
    @Column(name = "id")
    private int id;
    
    @Column(name = "message")
    private String message;

    public HelloDB() {
    }

    public HelloDB(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
