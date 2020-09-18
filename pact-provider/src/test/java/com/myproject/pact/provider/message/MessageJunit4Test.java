package com.myproject.pact.provider.message;


import au.com.dius.pact.provider.MessageAndMetadata;
import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit.Consumer;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.VerificationReports;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.AmqpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringMessagePactRunner;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.pact.provider.PactConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringMessagePactRunner.class) // Say JUnit to run tests with custom Runner

@SpringBootTest(classes = TestConfiguration.class,
        properties = {"spring.profiles.active=test", "spring.cloud.config.enabled=false"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)

@Provider(PactConfig.PACT_PROVIDER) // Set up name of tested provider
//@Consumer(PactConfig.PACT_CONSUMER_MESSAGE) // filter only
@PactBroker(scheme = "${pactbroker.scheme}", host = "${pactbroker.host}", port = "${pactbroker.port}")

@Slf4j
public class MessageJunit4Test {

    @TestTarget // Annotation denotes Target that will be used for tests
    // Note in 4.1.1, AmqpTarget renamed to MessageTarget
    //public final Target target = new MessageTarget(); // Out-of-the-box implementation of Target (for more information take a look at Test Target section)
    public final Target target = new AmqpTarget(); // Out-of-the-box implementation of Target (for more information take a look at Test Target section)


    @BeforeClass //Method will be run once: before whole contract test suite
    public static void setUpService() {
        log.info("setupService");
        //Run DB, create schema
        //Run service
        //...
    }

    @AfterClass //Method will be run once: after whole contract test suite
    public static void tearDownService() {
        log.info("tearDownService");
    }

    @Before //Method will be run before each test of interaction
    public void before() {
        log.info("before test method");

    }

    @After //Method will be run before each test of interaction
    public void after() {
        log.info("after test method");
    }

    // Sample using pure message body
    @SneakyThrows
//    @State(value ="send message") // match the given provider state
//    @PactVerifyProvider("a test message") // this must match the expected Receive state set in consumer
    public String verifyMessage() {
        Map<String, String> map = new HashMap<>();
        map.put("testParam1", "value1");
        map.put("testParam2", "value2");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(map);



        return json;
    }


    @SneakyThrows
    @State(value ="send message") // match the given provider state
    @PactVerifyProvider("a test message") // this must match the expected Receive state set in consumer
    public MessageAndMetadata verifyMessageWithMetadata() {
        Map<String, String> map = new HashMap<>();
        map.put("testParam1", "value1");
        map.put("testParam2", "value2");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(map);

        Map<String, Object > headers = new HashMap<>();
        headers.put("abc","def");
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        Message<String> message = MessageBuilder.createMessage(json, messageHeaders);

        return generateMessageAndMetadata(message);
    }

    private MessageAndMetadata generateMessageAndMetadata(Message<String> message) {
        HashMap<String, Object> metadata = new HashMap<String, Object>();
        message.getHeaders().forEach((k, v) -> metadata.put(k, v));

        return new MessageAndMetadata(message.getPayload().getBytes(), metadata);
    }
}