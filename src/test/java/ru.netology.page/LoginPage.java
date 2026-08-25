package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class LoginPage {

    private final SelenideElement loginField = $("[data-test-id='login'] input");
    private final SelenideElement passwordField = $("[data-test-id='password'] input");
    private final SelenideElement loginButton = $("[data-test-id='action-login']");
    private final SelenideElement errorNotification = $("[data-test-id='error-notification']");

    public LoginPage() {
        loginField.shouldBe(visible);
    }

    public VerificationPage login(DataHelper.AuthInfo authInfo) {
        loginField.setValue(authInfo.getLogin());
        passwordField.setValue(authInfo.getPassword());
        loginButton.click();
        return new VerificationPage();
    }

    public void loginWithInvalidData(DataHelper.AuthInfo authInfo) {
        loginField.setValue(authInfo.getLogin());
        passwordField.setValue(authInfo.getPassword());
        loginButton.click();
    }

    public void verifyInvalidCredentialsNotification() {
        errorNotification.shouldBe(visible).shouldHave(text("Неверный логин или пароль"));
    }

    public void verifyBlockedUserNotification() {
        errorNotification.shouldBe(visible).shouldHave(text("Пользователь заблокирован"));
    }
}