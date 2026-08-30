package ru.netology.test;

import ru.netology.data.DataHelper;
import ru.netology.db.DbUtils;
import ru.netology.page.LoginPage;
import org.junit.jupiter.api.*;

import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTest {

    @AfterAll
    static void cleanUp() {
        DbUtils.cleanDatabase();
    }

    @Test
    @DisplayName("Успешный вход с кодом из БД")
    void shouldLoginSuccessfullyWithCodeFromDatabase() {
        var authInfo = DataHelper.getValidUser();

        var loginPage = open("http://localhost:9999", LoginPage.class);
        var verificationPage = loginPage.login(authInfo);
        verificationPage.waitForPageLoad();

        String actualCode = DbUtils.getLatestAuthCodeForLogin(authInfo.getLogin());
        assertThat(actualCode).isNotNull().isNotEmpty();

        var dashboardPage = verificationPage.verify(actualCode);
        dashboardPage.verifyDashboardVisible();
    }

    @Test
    @DisplayName("Неверный пароль")
    void shouldShowErrorWithInvalidPassword() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        loginPage.loginWithInvalidData(DataHelper.getValidLoginInvalidPassword());
        loginPage.verifyInvalidCredentialsNotification();
    }

    @Test
    @DisplayName("Неверный код")
    void shouldShowErrorWithInvalidVerificationCode() {
        var authInfo = DataHelper.getValidUser();

        var loginPage = open("http://localhost:9999", LoginPage.class);
        var verificationPage = loginPage.login(authInfo);
        verificationPage.waitForPageLoad();

        verificationPage.verifyWithInvalidCode(DataHelper.getInvalidVerificationCode());
        verificationPage.verifyInvalidCodeNotification();
    }

    @Test
    @DisplayName("Блокировка после 3 неудачных попыток")
    void shouldBlockUserAfterThreeFailedAttempts() {
        var authInfo = DataHelper.getValidUser();

        var loginPage = open("http://localhost:9999", LoginPage.class);
        
        for (int i = 0; i < 3; i++) {
            loginPage.loginWithInvalidData(DataHelper.getValidLoginInvalidPassword());
            loginPage.verifyInvalidCredentialsNotification();
            loginPage = open("http://localhost:9999", LoginPage.class);
        }

        loginPage.loginWithInvalidData(authInfo);
        loginPage.verifyBlockedUserNotification();

        assertThat(DbUtils.getUserStatus(authInfo.getLogin())).isEqualTo("blocked");
    }
}
