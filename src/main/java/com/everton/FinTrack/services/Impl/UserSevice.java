package com.everton.FinTrack.services.Impl;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserSevice {
    private final Map<String, String> users = Map.of(
            "jheniffer", "08162814@",
            "everton", "159123@"
    );

    public boolean authenticate(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }
}
