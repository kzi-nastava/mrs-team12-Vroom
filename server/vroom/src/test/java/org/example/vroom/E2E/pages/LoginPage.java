package org.example.vroom.E2E.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(name = "email")
    private WebElement emailInput;

    @FindBy(name = "password")
    private WebElement passwordInput;

    @FindBy(className = "forgot-password-button")
    private WebElement forgotButton;

    @FindBy(className = "login-button")
    private WebElement loginButton;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:4200/login");

        PageFactory.initElements(driver, this);
    }

    public void enterEmail(String email){
        emailInput.clear();
        emailInput.sendKeys(email);
    }

    public void enterPassword(String password){
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }

    public void clickForgotButton(){
        forgotButton.click();
    }

    public void clickLoginButton(){
        loginButton.click();
    }


}
