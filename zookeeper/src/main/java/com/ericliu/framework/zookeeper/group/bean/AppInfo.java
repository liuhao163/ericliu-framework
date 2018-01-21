package com.ericliu.framework.zookeeper.group.bean;

import java.io.Serializable;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2018/1/21
 */
public class AppInfo implements Serializable {
    private String appName;
    private String appUrl;

    public AppInfo(String appName, String appUrl) {
        this.appName = appName;
        this.appUrl = appUrl;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "appName='" + appName + '\'' +
                ", appUrl='" + appUrl + '\'' +
                '}';
    }
}
