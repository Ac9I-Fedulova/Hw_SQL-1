package ru.netology.test;

import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertAll;
import static ru.netology.data.SQLHelper.cleanAuthCodes;
import static ru.netology.data.SQLHelper.cleanDatabase;

public class BankLoginPersonalAccountTest {
    LoginPage loginPage;
    DataHelper.AuthInfo authInfo = DataHelper.getAuthInfo();

    @AfterAll
    static void teardownAll() {
        cleanDatabase();
    }

    @BeforeEach
    void setUp() {
        loginPage = open("http://localhost:9999", LoginPage.class);
    }

    @AfterEach
    void teardown() {
        cleanAuthCodes();
    }

    @Test
    @DisplayName("Должны успешно войти в личный кабинет")
    void shouldLoginSuccessfully() {
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisiblity();
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode.getCode());
    }

    @Test
    @DisplayName("Должны получить уведомление об ошибке при входе со случайным пользователем без добавления в базу")
    void shouldGetErrorNotificationIfLoginInvalidUser() {
        var authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotificationVisiblity("Ошибка! Неверно указан логин или пароль");
    }

    @Test
    @DisplayName("Должны получить уведомление об ошибке при входе валидного пользователя с  невалидным кодом подтверждения")
    void shouldGetErrorNotificationIfLoginValidUserAndRandomVerificationCode() {
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisiblity();
        var verificationCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(verificationCode.getCode());
        verificationPage.verifyErrorNotificationVisiblity("Ошибка! Неверно указан код! Попробуйте ещё раз.");
    }

    @Test
    @DisplayName("Должны получить уведомление о блокировке аккаунта пользователя после трёх попыток неверного ввода пароля")
    void shouldLockSystemAfterThreeAttemptsEnterInvalidPassword() {
        var authInfo = DataHelper.generateRandomUser();
        for (int i = 0; i < 3; i++) {
            loginPage.invalidLogin(authInfo);
        }
        assertAll(() -> loginPage.verifyErrorNotificationVisiblity("Аккаунт заблокирован после трех неудачных попыток входа," +
                        "Вам следует обратитесь в офис Банка"),
                () -> loginPage.checkButtonUnavailability());
    }
}