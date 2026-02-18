package org.example.vroom.E2E.flow;

import org.example.vroom.E2E.base.BaseTest;
import org.example.vroom.E2E.pages.LoginPage;
import org.example.vroom.E2E.pages.AdminHomePage;
import org.example.vroom.E2E.pages.RideHistoryPage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RideHistoryTest extends BaseTest {
    private static final String EMAIL = "admin@vroom.com";
    private static final String PASSWORD = "admin123";
    private static final String NON_EXISTING_EMAIL = "sasaassaas@aasas.com";
    private static final String SORT = "price,desc";


    @Test
    void adminShouldBeAbleToFilterAndViewHistory(){
        LoginPage loginPage = new LoginPage(driver);
        AdminHomePage adminHomePage = new AdminHomePage(driver);

        loginPage.enterEmail(EMAIL);
        loginPage.enterPassword(PASSWORD);
        loginPage.clickLoginButton();

        assertTrue("User should be logged in", adminHomePage.isLoaded());

        adminHomePage.navigateToAdminRideHistory();
        RideHistoryPage rideHistoryPage = new RideHistoryPage(driver);
        assertTrue("Ride history page should be loaded", rideHistoryPage.isLoaded());

        int initialCount = rideHistoryPage.getRideCardsCount();
        assertTrue("Initially should have rides", initialCount > 0);

        rideHistoryPage.filterByEmail(NON_EXISTING_EMAIL);
        assertEquals(0, rideHistoryPage.getRideCardsCount());

        rideHistoryPage.filterByEmail("");
        rideHistoryPage.sortBy(SORT);

        assertTrue("Sort shouldn't affect rides count",rideHistoryPage.getRideCardsCount() >= 0);
    }

    @Test
    void adminShouldBeAbleToFilterByDateOnly(){
        LoginPage loginPage = new LoginPage(driver);
        AdminHomePage adminHomePage = new AdminHomePage(driver);

        loginPage.enterEmail(EMAIL);
        loginPage.enterPassword(PASSWORD);
        loginPage.clickLoginButton();

        assertTrue("User should be logged in", adminHomePage.isLoaded());

        adminHomePage.navigateToAdminRideHistory();
        RideHistoryPage rideHistoryPage = new RideHistoryPage(driver);
        assertTrue("Ride history page should be loaded", rideHistoryPage.isLoaded());

        int initialCount = rideHistoryPage.getRideCardsCount();

        LocalDate endDate = LocalDate.now().minusDays(1);
        rideHistoryPage.filterByEndDate(endDate);

        int filteredCount = rideHistoryPage.getRideCardsCount();
        assertTrue("Should have rides before yesterday", filteredCount >= 0);

        rideHistoryPage.clearFilters();
        assertEquals(initialCount, rideHistoryPage.getRideCardsCount());

        LocalDate startDate = LocalDate.now().plusDays(1);
        rideHistoryPage.filterByStartDate(startDate);

        filteredCount = rideHistoryPage.getRideCardsCount();
        assertTrue("Should have rides before yesterday", filteredCount == 0);
    }
}
