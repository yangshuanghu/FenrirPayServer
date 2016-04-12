package com.fenrir.server;

import com.aol.micro.server.MicroserverApp;

/**
 * Created by yume on 16-4-12.
 */
public class SimpleApp {
    public static void main(String[] args){
        new MicroserverApp(()->"fenrir").run();
    }
}
