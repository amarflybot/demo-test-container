package com.example.demotestcontainer;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.data.redis.connection.ReactiveStreamCommands.AddStreamRecord.body;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@Slf4j
class PersonControllerTest {

    @Autowired
    private PersonRepo personRepo;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Container
    final public static RedisContainer redisContainer = new RedisContainer("redis:6.0.4");

    @Container
    final public static TestSQLContainer mySQLContainer = new TestSQLContainer("mysql:8.0.20");


    @BeforeAll
    static void setUp() {
        redisContainer.start();
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        redisContainer.stop();
        mySQLContainer.stop();
    }

    @BeforeEach
    void before() {
        assertTrue(redisContainer.isRunning());
        assertTrue(mySQLContainer.isRunning());
        Arrays.asList("Amar","Amit","Ankush","Rohit")
                .forEach(name -> personRepo.save(new Person(name)));

    }

    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        personRepo.deleteAll();
        mySQLContainer.execInContainer("redis-cli", "-v").getStdout();
        mySQLContainer.execInContainer("redis-cli","FLUSHALL").getStdout();
    }

    @Test
    void getPersonByIdOneCallOnly() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/person/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getPersonByIdShouldHitDbOnlyOnce() throws Exception {
        final SessionFactory sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);
        final Statistics statistics = sessionFactory.getStatistics();
        long queryEntityLoadCount = statistics.getEntityLoadCount();

        // Call person/1 API once should call database for first time
        Assertions.assertEquals(0, queryEntityLoadCount);
        log.info("queryEntityLoadCount Before first api hit: {}", queryEntityLoadCount);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/person/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Amar"));
        queryEntityLoadCount = statistics.getEntityLoadCount();
        Assertions.assertEquals(1, queryEntityLoadCount);
        log.info("queryEntityLoadCount After first api hit: {}", queryEntityLoadCount);

        // Call person/1 API twice should not call database for this time
        mockMvc.perform(MockMvcRequestBuilders.get("/api/person/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Amar"));
        queryEntityLoadCount = statistics.getEntityLoadCount();
        Assertions.assertEquals(1, queryEntityLoadCount);
        log.info("queryEntityLoadCount After second api hit: {}", queryEntityLoadCount);

        // Call person/2 API once should call database for first time
        mockMvc.perform(MockMvcRequestBuilders.get("/api/person/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Amit"));
        queryEntityLoadCount = statistics.getEntityLoadCount();
        Assertions.assertEquals(2, queryEntityLoadCount);
        log.info("queryEntityLoadCount After third api hit: {}", queryEntityLoadCount);

    }
}
