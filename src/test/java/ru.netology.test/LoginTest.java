package ru.netology.test;

import com.codeborne.selenide.Configuration;
import ru.netology.data.DataHelper;
import ru.netology.db.DbUtils;
import ru.netology.page.LoginPage;
import ru.netology.page.VerificationPage;
import org.junit.jupiter.api.*;

import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTest {

    @BeforeAll
    static void setUp() {
        Configuration.browser = "chrome";
        Configuration.headless = false;
        Configuration.baseUrl = "http://localhost:8080";
        Configuration.timeout = 10000;
    }

    @BeforeEach
    void cleanUp() {
        DbUtils.cleanDatabase();
    }

    @Test
    @DisplayName("Успешный вход с кодом из БД (основной сценарий)")
    void shouldLoginSuccessfullyWithCodeFromDatabase() {
        var authInfo = DataHelper.getValidUser();

        var loginPage = open("/", LoginPage.class);
        var verificationPage = loginPage.login(authInfo);

        verificationPage.waitForPageLoad();

        String actualCode = DbUtils.getLatestAuthCodeForLogin(authInfo.getLogin());
        assertThat(actualCode).isNotNull().isNotEmpty();

        var dashboardPage = verificationPage.verify(actualCode);

        dashboardPage.verifyDashboardVisible();
    }

    @Test
    @DisplayName("Неверный пароль - показывается ошибка")
    void shouldShowErrorWithInvalidPassword() {
        var loginPage = open("/", LoginPage.class);
        loginPage.loginWithInvalidData(DataHelper.getValidLoginInvalidPassword());
        loginPage.verifyInvalidCredentialsNotification();
    }

    @Test
    @DisplayName("Неверный код подтверждения - показывается ошибка")
    void shouldShowErrorWithInvalidVerificationCode() {
        var authInfo = DataHelper.getValidUser();

        var loginPage = open("/", LoginPage.class);
        var verificationPage = loginPage.login(authInfo);
        verificationPage.waitForPageLoad();

        verificationPage.verifyWithInvalidCode(DataHelper.getInvalidVerificationCode());
        verificationPage.verifyInvalidCodeNotification();
    }

    @Test
    @DisplayName("Блокировка пользователя после 3 неудачных попыток")
    void shouldBlockUserAfterThreeFailedAttempts() {
        var authInfo = DataHelper.getValidUser();

        var loginPage = open("/", LoginPage.class);

        for (int i = 0; i < 3; i++) {
            loginPage.loginWithInvalidData(DataHelper.getValidLoginInvalidPassword());
            loginPage.verifyInvalidCredentialsNotification();
            loginPage = open("/", LoginPage.class);
        }

        loginPage.loginWithInvalidData(authInfo);
        loginPage.verifyBlockedUserNotification();

        assertThat(DbUtils.getUserStatus(authInfo.getLogin())).isEqualTo("blocked");
    }

    @Test
    @DisplayName("Блокированный пользователь не может войти")
    void shouldNotAllowBlockedUserToLogin() {
        var authInfo = DataHelper.getBlockedUser();

        var loginPage = open("/", LoginPage.class);
        loginPage.loginWithInvalidData(authInfo);
        loginPage.verifyBlockedUserNotification();
    }
}