package org.example.vroom.E2E.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class NavbarPage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(className = "logout-text")
    private WebElement logoutBtn;

    @FindBy(id = "admin-ride-history")
    private WebElement adminRideHistoryLink;

    public NavbarPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        PageFactory.initElements(driver, this);
    }

    public boolean isLoggedIn(){
        try{
            wait.until(ExpectedConditions.visibilityOf(logoutBtn));
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public void navigateToAdminRideHistory(){
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("admin-ride-history")));
        wait.until(ExpectedConditions.elementToBeClickable(adminRideHistoryLink));

        adminRideHistoryLink.click();
    }
}
