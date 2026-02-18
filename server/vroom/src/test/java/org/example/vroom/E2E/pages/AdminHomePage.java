package org.example.vroom.E2E.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AdminHomePage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//span[text()='Past Rides']")
    private WebElement pastRidesLink;

    @FindBy(className = "title")
    private WebElement titletext;

    public AdminHomePage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        PageFactory.initElements(driver, this);
    }

    public boolean isLoaded(){
        try{
            wait.until(ExpectedConditions.textToBePresentInElement(titletext, "Administrator Dashboard"));
            wait.until(ExpectedConditions.visibilityOf(pastRidesLink));

            return true;
        }catch(Exception e){
            return false;
        }
    }

    public void navigateToAdminRideHistory(){
        wait.until(ExpectedConditions.visibilityOf(pastRidesLink));
        wait.until(ExpectedConditions.elementToBeClickable(pastRidesLink));

        pastRidesLink.click();
    }
}
