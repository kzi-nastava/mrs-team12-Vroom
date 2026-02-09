package org.example.vroom.E2E.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class UserHomePage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(id = "rideHistoryButton")
    private WebElement rideHistoryButton;

    public UserHomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void clickRideHistoryButton() {
        rideHistoryButton.click();
    }

    public boolean isLoaded(){
        try{
            wait.until(ExpectedConditions.elementToBeClickable(rideHistoryButton));
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
