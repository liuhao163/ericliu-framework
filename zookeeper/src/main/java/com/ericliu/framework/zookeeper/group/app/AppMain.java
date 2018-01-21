package com.ericliu.framework.zookeeper.group.app;

import com.ericliu.framework.zookeeper.group.bean.AppInfo;

import java.io.*;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2018/1/21
 */
public class AppMain {
    public static void main(String[] args) throws IOException {
        App app = new App("localhost:2181");
        while (true) {
            byte[] b = new byte[1024];
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String readLine = reader.readLine();
            String[] params = readLine.split(" ");
            String commend = params[0];
            if (commend.equals("register")) {
                app.registerApp(params[1]);
            } else if (commend.equals("write")) {
                app.writeApp(new AppInfo(params[1], params[2]));
            } else if (commend.equals("read")) {
                System.out.println(app.getAppInfo(params[1]));
            } else if (commend.equals("readall")) {
                System.out.println(app.getAll());
            } else if (commend.equals("modify")) {
                String appName = params[1];
                AppInfo appInfo = app.getAppInfo(appName);
                String appUrl = params[2];
                app.writeApp(new AppInfo(appInfo.getAppName(), appUrl));
            } else if (commend.startsWith("del")) {
                app.unRegisterApp(params[1]);
            } else if (commend.startsWith("sync")) {
                app.syncAll();
            }
        }

    }
}
