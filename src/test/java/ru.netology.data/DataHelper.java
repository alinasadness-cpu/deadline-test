package ru.netology.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

public class DataHelper {


    public static AuthInfo getValidUser() {
        return new AuthInfo("vasya", "password");
    }

    public static AuthInfo getSecondValidUser() {
        return new AuthInfo("petya", "password");
    }

    public static AuthInfo getValidLoginInvalidPassword() {
        return new AuthInfo("vasya", "wrong_password");
    }

    public static AuthInfo getBlockedUser() {
        return new AuthInfo("masha", "password");
    }


    public static String generateValidVerificationCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public static String getInvalidVerificationCode() {
        return "000000";
    }


    @Data
    @AllArgsConstructor
    public static class AuthInfo {
        private String login;
        private String password;
    }
}