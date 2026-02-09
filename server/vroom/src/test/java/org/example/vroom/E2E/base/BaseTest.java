package org.example.vroom.E2E.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.vroom.E2E.utils.DbUtils;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class BaseTest {
    protected WebDriver driver;
    @BeforeEach
    void setUp(){
        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions options = new FirefoxOptions();

        options.addPreference("geo.enabled", true);
        options.addPreference("geo.provider.use_corelocation", true);
        options.addPreference("geo.prompt.testing", true);
        options.addPreference("geo.prompt.testing.allow", true);

        driver = new FirefoxDriver(options);

        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        DbUtils.insertFinishedRide(6L, 7L, 1L);
    }



    @AfterEach
    void tearDown(){
        if(driver != null){
            driver.quit();
        }
    }
}
