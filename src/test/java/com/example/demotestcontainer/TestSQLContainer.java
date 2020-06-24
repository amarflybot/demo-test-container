package com.example.demotestcontainer;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.Base58;

import java.util.Arrays;

public class TestSQLContainer extends MySQLContainer<TestSQLContainer> {

    public TestSQLContainer() {
        this("mysql:8.0.20");
    }

    public TestSQLContainer(String dockerImageName) {
        super(dockerImageName);
        this.logger().info("Starting an redis container using [{}]", dockerImageName);
        this.setPortBindings(Arrays.asList("3306:3306"));
        this.withUsername("testUser");
        this.withPassword("testPass");
        this.withDatabaseName("testdb");
        this.withNetworkAliases(new String[]{"mysql-" + Base58.randomString(6)});
        this.addExposedPorts(new int[]{3306});
    }
}
