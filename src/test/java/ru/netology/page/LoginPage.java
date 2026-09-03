package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

    private final SelenideElement loginField = $("[data-test-id='login'] input");
    private final SelenideElement passwordField = $("[data-test-id='password'] input");
    private final SelenideElement loginButton = $("[data-test-id='action-login']");
    private final SelenideElement errorNotification = $("[data-test-id='error-notification']");

    public LoginPage() {
        loginField.shouldBe(visible, Duration.ofSeconds(20));
    }

    private void fillLoginForm(DataHelper.AuthInfo authInfo) {
        loginField.setValue(authInfo.getLogin());
        passwordField.setValue(authInfo.getPassword());
        loginButton.click();
    }

    public VerificationPage login(DataHelper.AuthInfo authInfo) {
        fillLoginForm(authInfo);
        return new VerificationPage();
    }

    public void loginWithInvalidData(DataHelper.AuthInfo authInfo) {
        fillLoginForm(authInfo);
    }

    public void verifyErrorNotification(String expectedText) {
        errorNotification.shouldBe(visible, Duration.ofSeconds(10)).shouldHave(com.codeborne.selenide.Condition.text(expectedText));
    }

    public void verifyInvalidCredentialsNotification() {
        verifyErrorNotification("Ошибка! Неверный логин или пароль");
    }

    public void verifyBlockedUserNotification() {
        verifyErrorNotification("Пользователь заблокирован");
    }

    public void waitForPageLoad() {
        loginField.shouldBe(visible, Duration.ofSeconds(20));
    }
}