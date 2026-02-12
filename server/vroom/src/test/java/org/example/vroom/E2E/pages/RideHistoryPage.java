package org.example.vroom.E2E.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RideHistoryPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FindBy(id = "title")
    private WebElement title;

    @FindBy(id = "startDate")
    private WebElement startDateInput;

    @FindBy(id = "endDate")
    private WebElement endDateInput;

    @FindBy(id = "userEmail")
    private WebElement userEmailInput;

    @FindBy(id = "sortBy")
    private WebElement sortBySelect;

    @FindBy(className = "ride-card")
    private List<WebElement> rideCards;

    @FindBy(className = "no-rides-container")
    private WebElement noRidesMessage;

    @FindBy(className = "clear-filters-btn")
    private WebElement clearFiltersButton;



    public RideHistoryPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        PageFactory.initElements(driver, this);
    }

    public boolean isLoaded(){
        try{
            wait.until(ExpectedConditions.urlToBe("http://localhost:4200/ride-history"));
            wait.until(ExpectedConditions.textToBePresentInElement(title, "Ride History"));

            waitForLoaderToFinish();

            return true;
        }catch(Exception e){
            return false;
        }
    }

    public void filterByEmail(String email) {
        userEmailInput.clear();
        userEmailInput.sendKeys(email);

        wait.until(driver ->
                driver.findElements(By.className("ride-card")).isEmpty() || driver.findElement(By.className("spinner")).isDisplayed()
        );

        wait.until(driver -> {
            boolean spinner = driver.findElements(By.className("spinner")).isEmpty();
            boolean noRides = !driver.findElements(By.xpath("//p[text()='No rides found']")).isEmpty();
            boolean rides = !driver.findElements(By.className("ride-card")).isEmpty();

            return spinner && (noRides || rides);
        });
    }

    public void filterByStartDate(LocalDate date) {
        startDateInput.clear();
        startDateInput.sendKeys(date.format(DATE_FORMATTER));

        waitForLoaderToFinish();
    }

    public void filterByEndDate(LocalDate date) {
        endDateInput.clear();
        endDateInput.sendKeys(date.format(DATE_FORMATTER));

        waitForLoaderToFinish();
    }


    public void sortBy(String value) {
        new Select(sortBySelect).selectByValue(value);

        waitForLoaderToFinish();
    }

    public void clearFilters() {
        if (isClearFiltersBtnVisible()) {
            clearFiltersButton.click();
            waitForLoaderToFinish();
        }
    }

    public boolean isClearFiltersBtnVisible() {
        try {
            return clearFiltersButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public int getRideCardsCount() {
        try {
            waitForLoaderToFinish();

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("ride-card")),
                    ExpectedConditions.visibilityOf(noRidesMessage)
            ));

            return rideCards.size();
        } catch (Exception e) {
            return 0;
        }
    }

    private void waitForLoaderToFinish() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("spinner")));
    }
}
