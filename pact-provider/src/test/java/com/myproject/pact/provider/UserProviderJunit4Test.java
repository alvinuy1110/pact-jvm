package com.myproject.pact.provider;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringRestPactRunner;
import au.com.dius.pact.provider.spring.target.SpringBootHttpTarget;
import com.myproject.pact.provider.domain.User;
import com.myproject.pact.provider.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * This will actually boot the app, but replace the "Service" component with a mock.
 * So this is just like an E2E.
 */

@RunWith(SpringRestPactRunner.class)

@SpringBootTest(classes = PactJvmProviderApplication.class,
        properties = {"spring.profiles.active=test", "spring.cloud.config.enabled=false"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// settings for pact
//@PactBroker(host = "localhost", port = "8500")
@PactBroker(scheme = "${pactbroker.scheme}", host = "${pactbroker.host}", port = "${pactbroker.port}")
// provider name
@Provider(PactConfig.PACT_PROVIDER)

// use this instead if not fetching remotely but from static folder
//@PactFolder("pacts")
@Slf4j
public class UserProviderJunit4Test {

    @MockBean
    private UserService userService;

    @LocalServerPort
    int serverPort;

    // if using HttpTarget, pass in a port that will boot the app
    @TestTarget
    public final Target target = new SpringBootHttpTarget(serverPort);


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
        // Rest data
        // Mock dependent service responses
        // ...
//        embeddedService.addExpectation(
//                onRequestTo("/data"), giveEmptyResponse()
//        );

    }

    @After //Method will be run before each test of interaction
    public void after() {
        log.info("after test method");
    }

    // must match the state created by user
    @State(value = "create user")
    public void createUserState() throws Exception {

        // TODO this should fail as the body doesnt match the pact!!!, instead it passes
        // Optional mocks.  Leave empty to invoke real service like E2E
        User user = new User("testuser", "abc@yahoo.com", 311);
        when(userService.saveUser(any(User.class))).thenReturn(user);
    }

    // must match the state created by user
    @State(value = "get user")
    public void getUserState() throws Exception {

        // TODO this should fail as the body doesnt match the pact!!!, instead it passes
        // Optional mocks.  Leave empty to invoke real service like E2E

        // TODO why name must match??
        // The name value must match
        // The email value doest not match
        // The age (int value) does not match, but passes
        User user = new User("testuser1", "abc@yahoo.com", 311);
        when(userService.getUser()).thenReturn(user);
    }

    // must match the state created by user
    @State(value = "get user XML")
    public void getUserXmlState() throws Exception {

        // TODO this should fail as the body doesnt match the pact!!!, instead it passes
        // Optional mocks.  Leave empty to invoke real service like E2E

        // TODO why name must match??
        // The name value must match
        // The email value doest not match
        // The age (int value) does not match, but passes
//        User user = new User("testuser1", "abc@yahoo.com", 311);
//        when(userService.getUser()).thenReturn(user);
    }

    // must match the state created by user
    @State(value = "create user XML")
    public void createUserXmlState() throws Exception {

        // TODO this should fail as the body doesnt match the pact!!!, instead it passes
        // Optional mocks.  Leave empty to invoke real service like E2E

        // TODO why name must match??
        // The name value must match
        // The email value doest not match
        // The age (int value) does not match, but passes
//        User user = new User("testuser1", "abc@yahoo.com", 311);
//        when(userService.getUser()).thenReturn(user);
    }

}