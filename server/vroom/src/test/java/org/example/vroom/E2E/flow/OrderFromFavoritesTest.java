package org.example.vroom.E2E.flow;

import org.example.vroom.E2E.base.BaseTest;
import org.example.vroom.E2E.pages.LoginPage;
import org.example.vroom.E2E.pages.OrderFromFavoritesPage;
import org.example.vroom.E2E.pages.UserHomePage;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.util.AssertionErrors.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class OrderFromFavoritesTest extends BaseTest {

    @Test
    void userShouldOrderRideFromFavorites() {
        driver.get("http://localhost:4200/login");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterEmail(testUser.getEmail());
        loginPage.enterPassword(testUser.getPassword());
        loginPage.clickLoginButton();
        UserHomePage userHomePage = new UserHomePage(driver);
        assertTrue("User should be logged in", userHomePage.isLoaded());

        userHomePage.clickOrderFromFavorites();

        OrderFromFavoritesPage page = new OrderFromFavoritesPage(driver);
        assertTrue("Favorite rides page should be loaded", page.isLoaded());

        page.selectStandardVehicleForFirstRoute();
        page.allowKidsForFirstRoute();
        page.clickUseThisRouteForFirst();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Ride successfully ordered!", alert.getText());
        alert.accept();
    }
}
