package org.example.vroom.E2E.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RideHistoryMoreInfo {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(id = "leaveReviewButton")
    private WebElement leaveReviewButton;

    @FindBy(id="reviewInfo")
    private WebElement reviewInfo;

    @FindBy(id="driverRating")
    private WebElement driverRating;

    @FindBy(id="vehicleRating")
    private WebElement vehicleRating;

    @FindBy(id="comment")
    private WebElement comment;

    public RideHistoryMoreInfo(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void clickLeaveReviewButton() {
        leaveReviewButton.click();
    }

    public boolean buttonIsLoaded() {
        try{
            wait.until(ExpectedConditions.visibilityOf(leaveReviewButton));
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public boolean reviewIsLoaded() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("leaveReviewButton")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("reviewInfo")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String driverRating() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("driverRating")))
                .getText().split("/")[0].trim();
    }

    public String vehicleRating() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("vehicleRating")))
                .getText().split("/")[0].trim();
    }

    public String comment() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("comment")))
                .getText().trim();
    }
}
