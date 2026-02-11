package org.example.vroom.E2E.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class UserRideHistoryPage {

    private WebDriver driver;
    private WebDriverWait wait;


    @FindBy(xpath = "//*[@id='rideCardsContainer']/*[1]")
    private WebElement rideCard;

    public UserRideHistoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void clickOnRideCard() {
        rideCard.click();
    }

    public boolean isLoaded() {
        try{
            wait.until(ExpectedConditions.visibilityOf(rideCard));
            return true;
        }catch (Exception e) {
            return false;
        }
    }
}
