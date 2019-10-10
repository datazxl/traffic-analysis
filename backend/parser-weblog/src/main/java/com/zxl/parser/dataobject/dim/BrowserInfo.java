package com.zxl.parser.dataobject.dim;

/**
 * 浏览器以及系统信息类
 */
public class BrowserInfo {
    private boolean alexaToolBar;
    private String browserLanguage;
    private String colorDepth;
    private boolean cookieEnable;
    private String deviceName;
    private String deviceType;
    private String flashVersion;
    private boolean javaEnable;
    private String osLanguage;
    private String resolution;
    private String silverlightVersion;

    public boolean isAlexaToolBar() {
        return alexaToolBar;
    }

    public void setAlexaToolBar(boolean alexaToolBar) {
        this.alexaToolBar = alexaToolBar;
    }

    public String getBrowserLanguage() {
        return browserLanguage;
    }

    public void setBrowserLanguage(String browserLanguage) {
        this.browserLanguage = browserLanguage;
    }

    public String getColorDepth() {
        return colorDepth;
    }

    public void setColorDepth(String colorDepth) {
        this.colorDepth = colorDepth;
    }

    public boolean isCookieEnable() {
        return cookieEnable;
    }

    public void setCookieEnable(boolean cookieEnable) {
        this.cookieEnable = cookieEnable;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getFlashVersion() {
        return flashVersion;
    }

    public void setFlashVersion(String flashVersion) {
        this.flashVersion = flashVersion;
    }

    public boolean isJavaEnable() {
        return javaEnable;
    }

    public void setJavaEnable(boolean javaEnable) {
        this.javaEnable = javaEnable;
    }

    public String getOsLanguage() {
        return osLanguage;
    }

    public void setOsLanguage(String osLanguage) {
        this.osLanguage = osLanguage;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getSilverlightVersion() {
        return silverlightVersion;
    }

    public void setSilverlightVersion(String silverlightVersion) {
        this.silverlightVersion = silverlightVersion;
    }
}
