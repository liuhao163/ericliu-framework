package com.ericliu.framework.zookeeper.group.app;

import com.ericliu.framework.zookeeper.group.bean.AppInfo;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2018/1/21
 */
public class App {


    private String path = "/ericliu/manager";

    private ZkClient zooKeeper;

//    private Map<String, AppInfo> map = new HashMap<>();

    public App(String zkHost) {
        this.zooKeeper = new ZkClient(zkHost);

        //todo
        zooKeeper.subscribeStateChanges(new IZkStateListener() {
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState keeperState) throws Exception {
                System.out.println(keeperState);
            }

            @Override
            public void handleNewSession() throws Exception {
                System.out.println("new session");
            }
        });

        //当有新节点变化时候注册
        zooKeeper.subscribeChildChanges(path, new IZkChildListener() {
            @Override
            public void handleChildChange(String s, List<String> list) {
                System.out.println(" child node changes: path:" + s + " list:" + list);
//                for (String childName : list) {
//                    registerApp(childName);
//                    getAppInfo(childName);
//                }
            }
        });

        //init load all data
        syncAll();
    }

    /**
     * 注册
     *
     * @param appName
     */
    private void registerApp(String appName) {
        if (!zooKeeper.exists(path + "/" + appName)) {
            zooKeeper.createPersistent(path + "/" + appName);
            zooKeeper.subscribeDataChanges(path + "/" + appName, new IZkDataListener() {
                @Override
                public void handleDataChange(String s, Object o) throws Exception {
                    AppInfo data = (AppInfo) o;
                    System.out.println("modify date change.data:" + data);
//                map.put(data.getAppName(), data);
                }

                @Override
                public void handleDataDeleted(String s) throws Exception {
                    System.out.println("delte date change.data.");
                    String[] paths = s.split("/");
                    String key = paths[s.length() - 1];
                }
            });
        }
    }

    /**
     * 删除
     *
     * @param appName
     */
    public void unRegisterApp(String appName) {
        zooKeeper.delete(path + "/" + appName);
    }

    /**
     * 获取数据
     *
     * @param appName
     * @return
     */
    public AppInfo getAppInfo(String appName) {
//        AppInfo appInfo = map.get(appName);
//        if (appInfo == null) {
        AppInfo appInfo = zooKeeper.readData(path + "/" + appName);
//            if (appInfo != null) {
//                map.put(appInfo.getAppName(), appInfo);
//            }
//        }
        return appInfo;
    }

    /**
     * 得到所有数据
     *
     * @return
     */
    public Map<String, AppInfo> getAll() {
//        return map;
        Map<String, AppInfo> map = new HashMap<>();
        List<String> childred = zooKeeper.getChildren(path);
        for (String childName : childred) {
//            registerApp(childName);
            AppInfo apppInfo = getAppInfo(childName);
            map.put(apppInfo.getAppName(), apppInfo);
        }
        return map;
    }

    /**
     * 更新数据
     *
     * @param appInfo
     */
    public Stat writeApp(AppInfo appInfo) {
        registerApp(appInfo.getAppName());
        Stat stat = zooKeeper.writeData(path + "/" + appInfo.getAppName(), appInfo);
//        map.put(appInfo.getAppName(), appInfo);
        return stat;
    }

    public void syncAll() {
//        List<String> list = zooKeeper.getChildren(path);
//        for (String childName : list) {
//            registerApp(childName);
//            AppInfo appinfo = getAppInfo(childName);
//            if (appinfo != null) {
//                map.put(appinfo.getAppName(), appinfo);
//            }
//        }
    }
}
