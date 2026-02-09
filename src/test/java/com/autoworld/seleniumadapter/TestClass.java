package com.autoworld.seleniumadapter;

import com.autoworld.configprovider.ConfigProvider;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestClass {

    private WebDriver driver;
    private DriverManager driverManager;

    @BeforeMethod
    public void setUp() {
        driverManager = DriverManagerFactory.getManager(ConfigProvider.getAsString("browser"));
        driver = driverManager.getDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testLaunchDriverAndOpenUrl() {
        String url = "https://www.google.com";
        driver.get(url);

        // Verify that the page title contains "Google"
        String pageTitle = driver.getTitle();
        Assert.assertNotNull(pageTitle, "Page title should not be null");
        Assert.assertTrue(pageTitle.contains("Google"),
                "Page title should contain 'Google', but was: " + pageTitle);

        // Verify that the current URL matches the expected URL
        String currentUrl = driver.getCurrentUrl();
        Assert.assertNotNull(currentUrl, "Current URL should not be null");
        Assert.assertTrue(currentUrl.contains("google.com"),
                "Current URL should contain 'google.com', but was: " + currentUrl);

        System.out.println("Test passed! Successfully launched driver and opened URL: " + url);
        System.out.println("Page title: " + pageTitle);
    }

    @AfterMethod
    public void tearDown() {
        if (driverManager != null) {
            driverManager.stopService();
        }
    }
}
