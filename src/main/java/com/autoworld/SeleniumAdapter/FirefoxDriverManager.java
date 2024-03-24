package com.autoworld.SeleniumAdapter;

import com.autoworld.ConfigProvider.ConfigProvider;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.log4testng.Logger;

import java.util.HashMap;
import java.util.regex.Pattern;

public class FirefoxDriverManager extends DriverManager{

    private static final String ffcaps=System.getProperty("ff.caps.list.of.string", ConfigProvider.getAsString("ff.caps.list.of.string"));
    private final Boolean bsFlag=Boolean.valueOf(System.getProperty("Browserstack",ConfigProvider.getAsString("Browserstack")));
    private final Boolean seleniumGridFalg=Boolean.valueOf(System.getProperty("SeleniumGrid",ConfigProvider.getAsString("SeleniumGrid")));
    private GeckoDriverService geckoDriverService;
    private static Logger logger=Logger.getLogger(FirefoxDriverManager.class);

    public FirefoxDriverManager(){}

    private boolean isServiceInitialized(){
        return this.geckoDriverService!=null;
    }
    @Override
    public void startService() {
        if(!isServiceInitialized()){
            this.geckoDriverService= GeckoDriverService.createDefaultService();
        }
    }
    public void stopService(){
        if(this.isServiceInitialized() && this.geckoDriverService.isRunning()){
            this.geckoDriverService.stop();
        }else {
            this.driver.quit();
        }
    }

    @Override
    public void createDriver() {
        logger.info("Start Launching Firefox Driver");

        FirefoxOptions capabilities=new FirefoxOptions();
        FirefoxProfile profile=new FirefoxProfile();
        FirefoxOptions options=new FirefoxOptions();
        profile.setPreference("browser.download.folderList",1);
        profile.setPreference("browser.download.manager.showWhenStarting",false);
        profile.setPreference("browser.download.manager.focusWhenStarting",false);
        profile.setPreference("browser.download.useDownloadDir",true);
        profile.setPreference("browser.healperApps.alwaysAsk.force",false);
        profile.setPreference("browser.download.manager.alertOnEXEOpen",false);
        profile.setPreference("browser.download.manager.closeWhenDone",true);
        profile.setPreference("browser.download.manager.showAlertOnComplete",false);
        profile.setPreference("browser.download.manager.useWindow",false);
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk","application/octet-stream");
        options.setProfile(profile);
        options.setCapability("platformName",this.getPlatform());
        options.setCapability("browserName","firefox");

        HashMap<String,Object> browserstackOptions=new HashMap<>();
        if(this.bsFlag && this.seleniumGridFalg){
            if(!ffcaps.isEmpty() && ffcaps.contains("||")){
                String[] arr=ffcaps.split(Pattern.quote("||"));
                for(int i=0;i< arr.length;i++){
                    String caps[]=arr[i].split(",");
                    fnFirefoxCapabilities(browserstackOptions,caps);
                }
            }
            capabilities.setCapability("bstack:options",browserstackOptions);
            capabilities.setProfile(profile);
        }

        if(this.isSeleniumGridRequired()){
            this.driver=new RemoteWebDriver(this.getServerUrl(),capabilities,true);
        }else {
            this.driver=new FirefoxDriver(this.geckoDriverService,options);
        }

        logger.info("Start Launching Firefox Driver");
    }

    private void fnFirefoxCapabilities( HashMap<String,Object> browserstackOptions,String[] caps){
        if(!caps[1].equalsIgnoreCase("true") && !caps[1].equalsIgnoreCase("false")){
            browserstackOptions.put(caps[0],caps[1]);
        }else{
            boolean flag=Boolean.parseBoolean(caps[1]);
            browserstackOptions.put(caps[0],flag);
        }
    }
}
