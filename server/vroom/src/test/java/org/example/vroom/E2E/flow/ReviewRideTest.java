package org.example.vroom.E2E.flow;

import org.example.vroom.E2E.base.BaseTest;
import org.example.vroom.E2E.pages.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ReviewRideTest extends BaseTest {
    private static final String EMAIL = "asdfghyei7@gmail.com";
    private static final String PASSWORD = "Sifra123!";
    private static final int driverRating = 3;
    private static final int vehicleRating = 5;
    private static final String comment = "review comment";

    @Test
    void happyPath(){
        LoginPage loginPage = new LoginPage(driver);
        UserHomePage userHomePage = new UserHomePage(driver);
        loginPage.enterEmail(EMAIL);
        loginPage.enterPassword(PASSWORD);
        loginPage.clickLoginButton();
        assertTrue("User should be logged in", userHomePage.isLoaded());

        userHomePage.clickRideHistoryButton();
        UserRideHistoryPage userRideHistoryPage = new UserRideHistoryPage(driver);
        assertTrue("Ride history should open", userRideHistoryPage.isLoaded());

        userRideHistoryPage.clickOnRideCard();
        RideHistoryMoreInfo moreInfo = new RideHistoryMoreInfo(driver);
        assertTrue("Popup should open", moreInfo.buttonIsLoaded());

        moreInfo.clickLeaveReviewButton();
        ReviewForm reviewForm = new ReviewForm(driver);
        assertTrue("Review form should open", reviewForm.isLoaded());

        reviewForm.fillReviewForm(driverRating, vehicleRating, comment);

        assertTrue("Review form should close and load reviews", moreInfo.reviewIsLoaded());
        assertEquals(String.valueOf(driverRating), moreInfo.driverRating());
        assertEquals(String.valueOf(vehicleRating), moreInfo.vehicleRating());
        assertEquals(comment, moreInfo.comment());
    }
}
