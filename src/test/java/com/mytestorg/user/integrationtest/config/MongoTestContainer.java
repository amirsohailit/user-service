package com.mytestorg.user.integrationtest.config;

import org.testcontainers.containers.MongoDBContainer;

public class MongoTestContainer {
    private static final MongoDBContainer CONTAINER;
    static {
        CONTAINER = new MongoDBContainer("mongo:7.0.5");
        CONTAINER.start();
    }
    public static String getConnectionString() {
        return CONTAINER.getConnectionString()+"/test";
    }


}
