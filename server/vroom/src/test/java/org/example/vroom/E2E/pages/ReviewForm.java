package org.example.vroom.E2E.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ReviewForm {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(id="commentInput")
    private WebElement commentInput;

    @FindBy(className = "submit-button")
    private WebElement submitButton;

    public ReviewForm(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void fillReviewForm(int driverRating, int vehicleRating, String comment) {
        driver.findElement(By.xpath("//div[@id='driverRating']/span[" + driverRating + "]")).click();
        driver.findElement(By.xpath("//div[@id='vehicleRating']/span[" + vehicleRating + "]")).click();
        commentInput.clear();
        commentInput.sendKeys(comment);
        submitButton.click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        wait.until(ExpectedConditions.invisibilityOf(commentInput));
    }

    public boolean isLoaded(){
        try{
            wait.until(ExpectedConditions.elementToBeClickable(submitButton));
            return true;
        }catch(Exception e){
            return false;
        }
    }


}
