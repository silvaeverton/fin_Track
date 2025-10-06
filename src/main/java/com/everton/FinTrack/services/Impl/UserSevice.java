package com.everton.FinTrack.services.Impl;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserSevice {
    private final Map<String, String> users = Map.of(
            "admin", "123",
            "everton", "1234"
    );

    public boolean authenticate(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }
}
