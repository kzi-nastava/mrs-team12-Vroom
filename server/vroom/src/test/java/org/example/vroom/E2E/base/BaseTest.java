package org.example.vroom.E2E.base;

import io.github.bonigarcia.wdm.WebDriverManager;
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

        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("geo.enabled", true);
        profile.setPreference("geo.provider.use_corelocation", true);
        profile.setPreference("geo.prompt.testing", true);
        profile.setPreference("geo.prompt.testing.allow", true);

        driver = new FirefoxDriver(options);

        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
    }

    @AfterEach
    void tearDown(){
        if(driver != null){
            driver.quit();
        }
    }
}
