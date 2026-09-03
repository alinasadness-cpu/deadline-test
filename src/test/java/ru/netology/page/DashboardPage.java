package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class DashboardPage {

    private final SelenideElement heading = $("[data-test-id='dashboard'] h1");

    public DashboardPage() {
        heading.shouldBe(visible, Duration.ofSeconds(20));
    }

    public void verifyDashboardVisible() {
        heading.shouldBe(visible, Duration.ofSeconds(20));
    }
}