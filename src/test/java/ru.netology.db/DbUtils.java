package ru.netology.db;

import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;

public class DbUtils {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/app?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "app";
    private static final String DB_PASSWORD = "pass";

    @SneakyThrows
    private static Connection getConnection() {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    @SneakyThrows
    public static String getUserIdByLogin(String login) {
        QueryRunner runner = new QueryRunner();
        String sql = "SELECT id FROM users WHERE login = ?";
        try (Connection conn = getConnection()) {
            User user = runner.query(conn, sql, new BeanHandler<>(User.class), login);
            return user != null ? user.getId() : null;
        }
    }

    @SneakyThrows
    public static String getUserStatus(String login) {
        QueryRunner runner = new QueryRunner();
        String sql = "SELECT status FROM users WHERE login = ?";
        try (Connection conn = getConnection()) {
            User user = runner.query(conn, sql, new BeanHandler<>(User.class), login);
            return user != null ? user.getStatus() : null;
        }
    }

    @SneakyThrows
    public static String getLatestAuthCodeForLogin(String login) {
        QueryRunner runner = new QueryRunner();
        String sql = "SELECT ac.code FROM auth_codes ac " +
                "JOIN users u ON u.id = ac.user_id " +
                "WHERE u.login = ? ORDER BY ac.created DESC LIMIT 1";
        try (Connection conn = getConnection()) {
            AuthCode authCode = runner.query(conn, sql, new BeanHandler<>(AuthCode.class), login);
            return authCode != null ? authCode.getCode() : null;
        }
    }

    @SneakyThrows
    public static void cleanDatabase() {
        QueryRunner runner = new QueryRunner();
        try (Connection conn = getConnection()) {

            runner.update(conn, "DELETE FROM card_transactions");
            runner.update(conn, "DELETE FROM auth_codes");
            runner.update(conn, "DELETE FROM cards");
            runner.update(conn, "DELETE FROM users");


            runner.update(conn,
                    "INSERT INTO users (id, login, password, status) VALUES " +
                            "('550e8400-e29b-41d4-a716-446655440000', 'vasya', " +
                            "'$2a$10$7XK9YzT4Pq8WlM3nR5sV7uQxYzAbCdEfGhIjKlMnOpQrStUvWxYz', 'active')," +
                            "('550e8400-e29b-41d4-a716-446655440001', 'petya', " +
                            "'$2a$10$7XK9YzT4Pq8WlM3nR5sV7uQxYzAbCdEfGhIjKlMnOpQrStUvWxYz', 'active')," +
                            "('550e8400-e29b-41d4-a716-446655440002', 'masha', " +
                            "'$2a$10$7XK9YzT4Pq8WlM3nR5sV7uQxYzAbCdEfGhIjKlMnOpQrStUvWxYz', 'active')"
            );
        }
    }

    @Data
    public static class User {
        private String id;
        private String login;
        private String password;
        private String status;
    }

    @Data
    public static class AuthCode {
        private String id;
        private String userId;
        private String code;
        private LocalDateTime created;
    }
}