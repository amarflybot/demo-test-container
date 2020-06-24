package com.example.demotestcontainer;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.Base58;

import java.util.Arrays;

public class RedisContainer extends GenericContainer<RedisContainer> {

    public RedisContainer() {
        this("redis:6.0.4");
    }

    public RedisContainer(String dockerImageName) {
        super(dockerImageName);
        this.logger().info("Starting an redis container using [{}]", dockerImageName);
        this.setPortBindings(Arrays.asList("6379:6379"));
        this.withNetworkAliases(new String[]{"redis-" + Base58.randomString(6)});
        this.addExposedPorts(new int[]{6379});
    }

}
