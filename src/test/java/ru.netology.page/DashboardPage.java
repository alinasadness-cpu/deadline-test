package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class DashboardPage {

    private final SelenideElement heading = $("h1");

    public DashboardPage() {
        heading.shouldBe(visible).shouldHave(text("Личный кабинет"));
    }

    public void verifyDashboardVisible() {
        heading.shouldBe(visible);
    }
}
