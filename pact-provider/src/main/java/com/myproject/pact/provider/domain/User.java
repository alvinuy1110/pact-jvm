package com.myproject.pact.provider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String name;
    private String email;
    private int age;
    private String accountNum;

    public User(String name, String email, int age) {
        this.name = name;
        this.email=email;
        this.age=age;
    }
}
