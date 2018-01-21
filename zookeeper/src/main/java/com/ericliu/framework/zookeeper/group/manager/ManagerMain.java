package com.ericliu.framework.zookeeper.group.manager;

import java.io.IOException;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2018/1/21
 */
public class ManagerMain {

    public static void main(String[] args) throws IOException {

        Manager manager = new Manager("localhost:2181");
        manager.initPath();
        while (true){}
    }
}
