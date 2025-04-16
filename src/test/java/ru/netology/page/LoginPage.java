package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    private final SelenideElement loginField = $("[data-test-id=login] input");
    private final SelenideElement passwordField = $("[data-test-id=password] input");
    private final SelenideElement loginButton = $("[data-test-id=action-login]");
    private final SelenideElement errorNotification = $("[data-test-id='error-notification'] .notification__content");

    public void verifyErrorNotificationVisibility(String exactedText) {
        errorNotification.shouldBe(visible).shouldHave(text(exactedText));
    }

    public void checkButtonUnavailability() {
        loginButton.shouldBe(disabled);
    }

    private void enterLoginAndPassword(String login, String password) {
        loginField.setValue(login);
        passwordField.setValue(password);
        loginButton.click();
    }

    public VerificationPage validLogin(DataHelper.AuthInfo info) {
        enterLoginAndPassword(info.getLogin(), info.getPassword());
        return new VerificationPage();
    }

    public void loginWithWrongPassword(DataHelper.AuthInfo info) {
        enterLoginAndPassword(info.getLogin(), DataHelper.generateRandomPassword());
        loginField.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        passwordField.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
    }
}