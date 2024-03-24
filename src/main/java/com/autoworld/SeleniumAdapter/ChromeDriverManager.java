package com.autoworld.SeleniumAdapter;

import com.autoworld.ConfigProvider.ConfigProvider;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.log4testng.Logger;

import java.util.HashMap;
import java.util.regex.Pattern;

public class ChromeDriverManager extends DriverManager{

    private static final String headlessFlag=System.getProperty("headless", ConfigProvider.getAsString("headless"));
    HashMap<String,Object> chromePrefs;
    ChromeOptions options;
    private ChromeDriverService chService;
    private final String chromecaps=System.getProperty("chrome.caps.list.of.strings",ConfigProvider.getAsString("chrome.caps.list.of.strings"));
    private final Boolean bsFlag=Boolean.valueOf(System.getProperty("Browserstack",ConfigProvider.getAsString("Browserstack")));
    private final Boolean seleniumGridFalg=Boolean.valueOf(System.getProperty("SeleniumGrid",ConfigProvider.getAsString("SeleniumGrid")));
    private final String downloadPath=System.getProperty("chrome.file.download.path",ConfigProvider.getAsString("chrome.file.download.path"));
    private final String autoFileDownload=System.getProperty("auto.file.download",ConfigProvider.getAsString("auto.file.download"));
    private static Logger logger=Logger.getLogger(ChromeDriverManager.class);
    public ChromeDriverManager(){}

    private boolean isServiceInitialized(){
        return this.chService!=null;
    }
    @Override
    public void startService() {
        if(!this.isServiceInitialized()){
            this.chService=ChromeDriverService.createDefaultService();
        }
    }

    public void stopService(){
        if(this.isServiceInitialized() && this.chService.isRunning()){
            this.chService.stop();
        }else {
            this.driver.quit();
        }
    }
    @Override
    public void createDriver() {
        logger.info("Start Launching Chrome Driver");
        MutableCapabilities capabilities=new MutableCapabilities();
        this.options=new ChromeOptions();
        HashMap<String,Object> browsestackOption=new HashMap<>();
        fnAddCapabilities(capabilities,browsestackOption);
        String[] values;
        if(headlessFlag.equalsIgnoreCase("true")){
            values=ConfigProvider.getAsString("headless.options.list.of.strings").split(",");
            this.options.addArguments(values);
            this.options.setCapability("platformName",this.getPlatform());
            if(StringUtils.isNoneEmpty(new CharSequence[]{this.downloadPath})){
                this.chromePrefs=new HashMap<>();
                this.chromePrefs.put("download.default_directory",this.downloadPath);
            }

            this.fnInitiateDriver(capabilities,this.options);
        }else{
            values=ConfigProvider.getAsString("options.list.of.strings").split(",");
            this.options.addArguments(values);
            this.driverFileDownload();
            if(this.isSeleniumGridRequired()){
                capabilities.setCapability("goog:chromeOptions",this.options);
                this.driver=new RemoteWebDriver(this.getServerUrl(),capabilities,true);
            }else{
                this.driver=new ChromeDriver(this.chService,this.options);
            }

        }
        logger.info("Done Launching Chrome Driver");
    }


    private void fnInitiateDriver(MutableCapabilities capabilities,ChromeOptions options){
        if(this.isSeleniumGridRequired()){
            capabilities.setCapability("goog:chromeOptions",options);
            this.driver=new RemoteWebDriver(this.getServerUrl(),capabilities,true);
        }else{
            logger.info("Selenium grid flag is "+this.isSeleniumGridRequired());
            this.driver=new ChromeDriver(options);
        }
    }

    private void driverFileDownload(){
        HashMap<String,Object> chromePrefsVal=new HashMap<>();
        if(!this.autoFileDownload.isEmpty() && this.autoFileDownload.equalsIgnoreCase("true")){
            chromePrefsVal.put("profile.default_content_setting_values.automatic_downloads",1);
        }else{
            chromePrefsVal.put("profile.default_content_settings.popup",0);
        }

        chromePrefsVal.put("safebrowsing.enabled",true);
        if(StringUtils.isNoneEmpty(new CharSequence[]{this.downloadPath})){
            chromePrefsVal.put("download.default_directory",this.downloadPath);
        }
        this.options.setExperimentalOption("prefs",chromePrefsVal);
        this.options.setCapability("platformName",this.getPlatform());
    }
    private void fnAddCapabilities(MutableCapabilities capabilities,HashMap<String,Object> browserstackOptions){
        if(this.bsFlag && this.seleniumGridFalg){
            if(!this.chromecaps.isEmpty() && this.chromecaps.contains("||")){
                this.fnAddBSOptions(browserstackOptions);
            }else if(!this.chromecaps.isEmpty()){
                this.fnAddChromeCaps(this.chromecaps,browserstackOptions);
            }
            capabilities.setCapability("bstack:options",browserstackOptions);
        }
    }

    private void fnAddBSOptions(HashMap<String,Object> browserstackOptions){
        String[] arr=this.chromecaps.split(Pattern.quote("||"));
        for(int i=0;i<arr.length;i++){
            this.fnAddChromeCaps(arr[i],browserstackOptions);
        }
    }
    private void fnAddChromeCaps(String chromecaps,HashMap<String,Object> browserstackOptions){
        String[] caps=chromecaps.split(",");
        if(!caps[1].equalsIgnoreCase("true") && !caps[1].equalsIgnoreCase("false")){
            browserstackOptions.put(caps[0],caps[1]);
        }else{
            boolean flag=Boolean.parseBoolean(caps[1]);
            browserstackOptions.put(caps[0],flag);

        }
    }
}
