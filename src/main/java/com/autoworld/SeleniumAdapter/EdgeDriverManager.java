package com.autoworld.SeleniumAdapter;

import com.autoworld.ConfigProvider.ConfigProvider;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.log4testng.Logger;

import java.util.HashMap;
import java.util.regex.Pattern;

public class EdgeDriverManager extends DriverManager{

    private static final String headlessFlag=System.getProperty("headless", ConfigProvider.getAsString("headless"));
    private static final String edgecaps=System.getProperty("edge.caps.list.of.strings",ConfigProvider.getAsString("edge.caps.list.of.strings"));
    private final Boolean bsFlag=Boolean.valueOf(System.getProperty("Browserstack",ConfigProvider.getAsString("Browserstack")));
    private final Boolean seleniumGridFalg=Boolean.valueOf(System.getProperty("SeleniumGrid",ConfigProvider.getAsString("SeleniumGrid")));
    private final String downloadPath=System.getProperty("edge.file.download.path",ConfigProvider.getAsString("edge.file.download.path"));
    private final String autoFileDownload=System.getProperty("auto.file.download",ConfigProvider.getAsString("auto.file.download"));
    private EdgeDriverService edgeDriverService;
    private static Logger logger=Logger.getLogger(EdgeDriverManager.class);

    public EdgeDriverManager(){}

    private boolean isServiceInitialized(){
        return this.edgeDriverService!=null;
    }


    @Override
    public void startService() {
        if(!isServiceInitialized()){
            this.edgeDriverService=EdgeDriverService.createDefaultService();
        }
    }
    public void stopService() {
        try{
            if(this.isServiceInitialized() && this.edgeDriverService.isRunning()){
                this.edgeDriverService.stop();
            }else if(this.driver!=null){
                this.driver.quit();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public void createDriver() {
        logger.info("Start Launching Edge Driver");
        MutableCapabilities capabilities=new MutableCapabilities();
        EdgeOptions options=new EdgeOptions();
        HashMap<String,Object> browserstackOptions=new HashMap<>();
        this.fnEdgeBSCapabilities(capabilities,browserstackOptions);
        if(headlessFlag.equalsIgnoreCase("true")){
            this.fnInitiateEdgeHeadlessDriver(capabilities,options);
        }else{
            String[] values=ConfigProvider.getAsString("options.list.of.strings").split(",");
            options.addArguments(values);
            HashMap<String,Object> edgePrefs=new HashMap<>();
            if(!this.autoFileDownload.isEmpty() && this.autoFileDownload.equalsIgnoreCase("true")){
                edgePrefs.put("profile.default_content_setting_values.automatic_downloads",1);
            }else{
                edgePrefs.put("profile.default_content_settings.popup",0);
            }
            edgePrefs.put("safebrowsing.enabled",true);
            if(StringUtils.isNoneEmpty(new CharSequence[]{this.downloadPath})){
                edgePrefs.put("download.default_directory",this.downloadPath);
            }
            options.setExperimentalOption("prefs",edgePrefs);
            options.setCapability("platformName",this.getPlatform());

            if(this.isSeleniumGridRequired()){
                capabilities.setCapability("ms:edgeOptions",options);
                this.driver=new RemoteWebDriver(this.getServerUrl(),capabilities,true);
            }else {
                logger.info("Selenium grid flag: "+isSeleniumGridRequired());
                this.driver=new EdgeDriver(options);
            }
        }

        logger.info("Done Launching Edge Driver");
    }

    private void fnInitiateEdgeHeadlessDriver(MutableCapabilities capabilities,EdgeOptions options){
        String[] values=ConfigProvider.getAsString("edgeheadless.options.list.of.strings").split(",");
        options.addArguments(values);
        options.setCapability("platformNAme",this.getPlatform());
        if (StringUtils.isNoneEmpty(new CharSequence[]{this.downloadPath})){
            HashMap<String,Object> edgePrefs=new HashMap<>();
            edgePrefs.put("download.default_directory",this.downloadPath);
        }
        if(this.isSeleniumGridRequired()){
            capabilities.setCapability("ms:edgeOptions",options);
            this.driver=new RemoteWebDriver(this.getServerUrl(),capabilities,true);
        }else {
            logger.info("Selenium grid flag: "+isSeleniumGridRequired());
            this.driver=new EdgeDriver(options);
        }
    }

    private void fnEdgeBSCapabilities(MutableCapabilities capabilities,HashMap<String,Object> browserstackOptions){
        if(this.bsFlag && this.seleniumGridFalg){
            if(!edgecaps.isEmpty() && edgecaps.contains("||")){
                String[] arr=edgecaps.split(Pattern.quote("||"));
                for(int i=0;i<arr.length;i++){
                    fnEdgeCapabilities(browserstackOptions,arr[1]);
                }
            }else if(!edgecaps.isEmpty()){
                fnEdgeCapabilities(browserstackOptions,edgecaps);
            }
            capabilities.setCapability("bstack:options",browserstackOptions);
        }
    }

    private void fnEdgeCapabilities(HashMap<String,Object> browserstackOptions,String edgecaps){
        String[] caps=edgecaps.split(",");
        if(!caps[1].equalsIgnoreCase("true") && !caps[1].equalsIgnoreCase("false")){
            browserstackOptions.put(caps[0],caps[1]);
        }else{
            boolean flag=Boolean.parseBoolean(caps[1]);
            browserstackOptions.put(caps[0],flag);
        }
    }

}
