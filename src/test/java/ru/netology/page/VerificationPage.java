package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class VerificationPage {

    private final SelenideElement codeField = $("[data-test-id='code'] input");
    private final SelenideElement verifyButton = $("[data-test-id='action-verify']");
    private final SelenideElement errorNotification = $("[data-test-id='error-notification']");

    public VerificationPage() {
        codeField.shouldBe(visible, Duration.ofSeconds(20));
    }

    public DashboardPage verify(String code) {
        codeField.setValue(code);
        verifyButton.click();
        return new DashboardPage();
    }

    public void verifyWithInvalidCode(String code) {
        codeField.setValue(code);
        verifyButton.click();
    }

    public void waitForPageLoad() {
        codeField.shouldBe(visible, Duration.ofSeconds(20));
    }

    public void verifyErrorNotification(String expectedText) {
        errorNotification.shouldBe(visible, Duration.ofSeconds(10)).shouldHave(com.codeborne.selenide.Condition.text(expectedText));
    }

    public void verifyInvalidCodeNotification() {
        verifyErrorNotification("Ошибка! Неверный код");
    }
}