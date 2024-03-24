package com.autoworld.SeleniumAdapter;

import com.autoworld.ConfigProvider.ConfigProvider;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.testng.log4testng.Logger;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class DriverManager {
    protected WebDriver driver;
    private static Logger logger=Logger.getLogger(DriverManager.class);

    public DriverManager(){}

    protected abstract void startService();
    protected abstract void createDriver();
    public abstract void stopService();

    public WebDriver getDriver(){
        if(this.driver==null){
            if(!this.isSeleniumGridRequired()){
                DriverExecutables.setBrowserExe();
                this.startService();
            }
            this.createDriver();
        }
        return this.driver;
    }

    boolean isSeleniumGridRequired(){
        String value=System.getProperty("SeleniumGrid", ConfigProvider.getAsString("SeleniumGrid"));
        return Boolean.valueOf(value);
    }

    Platform getPlatform(){
        String platformValue=System.getProperty("platform",ConfigProvider.getAsString("platform"));
        if(!platformValue.equalsIgnoreCase("Windows7") && !platformValue.equalsIgnoreCase("windows") && !platformValue.equalsIgnoreCase("7")){
            if(!platformValue.equalsIgnoreCase("windows8") && !platformValue.equalsIgnoreCase("8")){
                if(!platformValue.equalsIgnoreCase("windows8.1") && !platformValue.equalsIgnoreCase("8.1")){
                    if(!platformValue.equalsIgnoreCase("windows10") && !platformValue.equalsIgnoreCase("10")){
                        if(!platformValue.equalsIgnoreCase("windowsXP") && !platformValue.equalsIgnoreCase("xp")){
                            if(platformValue.equalsIgnoreCase("mac")){
                                return Platform.MAC;
                            }else if(platformValue.equalsIgnoreCase("vista")){
                                return Platform.VISTA;
                            }else if(platformValue.equalsIgnoreCase("linux")){
                                return Platform.LINUX;
                            }else {
                                return platformValue.equalsIgnoreCase("unix")?Platform.UNIX:Platform.ANY;
                            }
                        }else {
                            return Platform.XP;
                        }

                    }else{
                        return Platform.WIN10;
                    }
                }else{
                    return Platform.WIN8_1;
                }
            }else{
                return Platform.WIN8;
            }
        }else{
            return Platform.WINDOWS;
        }
    }

    URL getServerUrl(){
        URL url=null;
        String urlString="";
        try{
            urlString=System.getProperty("hub_url").trim();
            url=this.toURL(urlString);
        }catch (Exception e){
            logger.warn("hub_url property is not define");
        }
        if(urlString.isEmpty()){
            logger.warn("hub_url value not defined");
        }

        return url;
    }

    private URL toURL(String urlString){
        URL url=null;
        try{
            url=new URL(urlString);
        } catch (MalformedURLException e) {
            logger.warn("url may not be correct");
        }
        return url;
    }
}
