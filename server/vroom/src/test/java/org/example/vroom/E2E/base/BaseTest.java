package org.example.vroom.E2E.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.persistence.EntityManager;
import org.example.vroom.E2E.utils.DbUtils;
import org.example.vroom.entities.Admin;
import org.example.vroom.entities.Driver;
import org.example.vroom.entities.RegisteredUser;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
@Rollback
public class BaseTest {
    protected WebDriver driver;

    @Autowired
    private EntityManager em;

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


        Long vehicleId = DbUtils.insertVehicle("Toyota", "Camry", "NS-123-AA");

        Long userId = DbUtils.insertUser("asdfghyei7@gmail.com", "$2a$12$avnzX5tbC1aoVYiYcyBeNut0CF/7QQFyv.PFhxeab9zPY.G89jOS6");
        Long adminId = DbUtils.insertAdmin("admin@vroom.com", "$2a$12$M//VXfyb2TNZhMGILCe0pO.vGWD8gGOD2WeCNiU62hNV4ktMbfyy2");
        Long driverId = DbUtils.insertDriver("test@test.com", "$2a$12$avnzX5tbC1aoVYiYcyBeNut0CF/7QQFyv.PFhxeab9zPY.G89jOS6", vehicleId);

        Long routeId = DbUtils.insertRoute(45.2396, 19.8227, 45.2491, 19.8550, "Start Street 1", "End Avenue 2");

        DbUtils.insertFinishedRide(driverId, userId, routeId);
    }


    @AfterEach
    void tearDown(){
        if(driver != null){
            driver.quit();
        }
    }
}
