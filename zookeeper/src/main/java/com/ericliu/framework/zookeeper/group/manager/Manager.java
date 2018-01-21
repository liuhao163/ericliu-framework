package com.ericliu.framework.zookeeper.group.manager;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.Watcher;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2018/1/21
 */
public class Manager {

    private String path = "/ericliu/manager";

    private ZkClient zooKeeper;

    public Manager(String zkHost) throws IOException {
        this.zooKeeper = new ZkClient(zkHost,6000);

        zooKeeper.subscribeStateChanges(new IZkStateListener() {
            public void handleStateChanged(Watcher.Event.KeeperState keeperState) throws Exception {
            }

            public void handleNewSession() throws Exception {
                System.out.println("new session register");
                zooKeeper.setCurrentState(Watcher.Event.KeeperState.SyncConnected);
            }
        });

        zooKeeper.subscribeChildChanges(path, new IZkChildListener() {
            public void handleChildChange(String s, List<String> list) throws Exception {
                System.out.println("register node" + s + " list:" + list);
            }
        });
    }

    public void initPath() {
        boolean isExists = zooKeeper.exists(path);

        if (!isExists) {
            zooKeeper.createPersistent(path, true);
        }
    }

}
