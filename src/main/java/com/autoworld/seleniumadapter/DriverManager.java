package com.autoworld.seleniumadapter;

import com.autoworld.configprovider.ConfigProvider;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.testng.log4testng.Logger;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class DriverManager {
    protected WebDriver driver;
    private static Logger logger = Logger.getLogger(DriverManager.class);

    protected abstract void startService();

    protected abstract void createDriver();

    public abstract void stopService();

    public WebDriver getDriver() {
        if (this.driver == null) {
            if (!this.isSeleniumGridRequired()) {
                DriverExecutables.setBrowserExe();
                this.startService();
            }
            this.createDriver();
        }
        return this.driver;
    }

    boolean isSeleniumGridRequired() {
        String value = System.getProperty("SeleniumGrid", ConfigProvider.getAsString("SeleniumGrid"));
        return Boolean.valueOf(value);
    }

    Platform getPlatform() {
        String platformValue = System.getProperty("platform", ConfigProvider.getAsString("platform"));
        String platform = platformValue.toLowerCase();

        return switch (platform) {
            case "windows7", "windows", "7" -> Platform.WINDOWS;
            case "windows8", "8" -> Platform.WIN8;
            case "windows8.1", "8.1" -> Platform.WIN8_1;
            case "windows10", "10" -> Platform.WIN10;
            case "windows11", "11" -> Platform.WIN11;
            case "windowsxp", "xp" -> Platform.XP;
            case "mac" -> Platform.MAC;
            case "vista" -> Platform.VISTA;
            case "linux" -> Platform.LINUX;
            case "unix" -> Platform.UNIX;
            default -> Platform.ANY;
        };
    }

    URL getServerUrl() {
        URL url = null;
        String urlString = "";
        try {
            urlString = System.getProperty("hub_url").trim();
            url = this.toURL(urlString);
        } catch (Exception e) {
            logger.warn("hub_url property is not define");
        }
        if (urlString.isEmpty()) {
            logger.warn("hub_url value not defined");
        }

        return url;
    }

    private URL toURL(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            logger.warn("url may not be correct");
        }
        return url;
    }
}
