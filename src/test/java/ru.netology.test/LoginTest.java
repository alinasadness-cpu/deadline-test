package ru.netology.test;

import com.codeborne.selenide.Configuration;
import ru.netology.data.DataHelper;
import ru.netology.db.DbUtils;
import ru.netology.page.LoginPage;
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
    @Order(1)
    @DisplayName("Успешный вход с валидными данными и кодом из БД")
    void shouldLoginSuccessfully() {
        var authInfo = DataHelper.getValidUser();
        String userId = DbUtils.getUserIdByLogin(authInfo.getLogin());
        String verificationCode = DataHelper.generateValidVerificationCode();
        DbUtils.insertAuthCode(userId, verificationCode);

        var loginPage = open("/", LoginPage.class);
        var verificationPage = loginPage.login(authInfo);
        var dashboardPage = verificationPage.verify(verificationCode);

        dashboardPage.verifyDashboardVisible();
    }

    @Test
    @Order(2)
    @DisplayName("Неверный пароль - показывается ошибка")
    void shouldShowErrorWithInvalidPassword() {
        var loginPage = open("/", LoginPage.class);
        loginPage.loginWithInvalidData(DataHelper.getValidLoginInvalidPassword());
        loginPage.verifyInvalidCredentialsNotification();
    }

    @Test
    @Order(3)
    @DisplayName("Неверный код подтверждения - показывается ошибка")
    void shouldShowErrorWithInvalidVerificationCode() {
        var authInfo = DataHelper.getValidUser();
        String userId = DbUtils.getUserIdByLogin(authInfo.getLogin());
        String correctCode = DataHelper.generateValidVerificationCode();
        DbUtils.insertAuthCode(userId, correctCode);

        var loginPage = open("/", LoginPage.class);
        var verificationPage = loginPage.login(authInfo);
        verificationPage.verifyWithInvalidCode(DataHelper.getInvalidVerificationCode());

        verificationPage.verifyInvalidCodeNotification();
    }

    @Test
    @Order(4)
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
    @Order(5)
    @DisplayName("Получение кода из БД для входа")
    void shouldGetVerificationCodeFromDatabaseForLogin() {
        var authInfo = DataHelper.getValidUser();
        String userId = DbUtils.getUserIdByLogin(authInfo.getLogin());
        String expectedCode = DataHelper.generateValidVerificationCode();
        DbUtils.insertAuthCode(userId, expectedCode);

        String codeFromDb = DbUtils.getLatestAuthCodeForLogin(authInfo.getLogin());
        assertThat(codeFromDb).isEqualTo(expectedCode);

        var loginPage = open("/", LoginPage.class);
        var verificationPage = loginPage.login(authInfo);
        var dashboardPage = verificationPage.verify(codeFromDb);

        dashboardPage.verifyDashboardVisible();
    }
}