package org.example.vroom.E2E.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

public class OrderFromFavoritesPage {

    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(tagName = "h1")
    private WebElement title;

    @FindBy(className = "ride-card")
    private List<WebElement> rideCards;

    public OrderFromFavoritesPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public boolean isLoaded() {
        try {
            wait.until(ExpectedConditions.textToBePresentInElement(title, "Order from favorites"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int getFavoriteRoutesCount() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("ride-card")));
        return rideCards.size();
    }

    public void selectStandardVehicleForFirstRoute() {
        WebElement firstCard = rideCards.get(0);
        WebElement standardRadio = firstCard.findElement(By.cssSelector("input[value='STANDARD']"));
        standardRadio.click();
    }

    public void allowKidsForFirstRoute() {
        WebElement firstCard = rideCards.get(0);
        WebElement checkbox = firstCard.findElement(By.xpath(".//input[@type='checkbox'][1]"));
        checkbox.click();
    }

    public void clickUseThisRouteForFirst() {
        WebElement firstCard = rideCards.get(0);
        WebElement useButton = firstCard.findElement(By.xpath(".//button[contains(text(),'Use this route')]"));
        useButton.click();
    }
}