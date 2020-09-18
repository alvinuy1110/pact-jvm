package com.myproject.pact.provider.controller;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.VerificationReports;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringRestPactRunner;
import au.com.dius.pact.provider.spring.target.MockMvcTarget;
import au.com.dius.pact.provider.spring.target.SpringBootHttpTarget;
import com.myproject.pact.provider.PactConfig;
import com.myproject.pact.provider.controller.UserController;
import com.myproject.pact.provider.domain.User;
import com.myproject.pact.provider.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * This will NOT  boot the app.  It uses all mock.
 * This may be way faster than booting up the entire app.
 * It will also bypass any filter and/ or advices if not configured.  This can be both good or bad.
 */

@RunWith(SpringRestPactRunner.class)

@SpringBootTest(classes = TestConfiguration.class,
        properties = {"spring.profiles.active=test", "spring.cloud.config.enabled=false"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
// settings for pact
//@PactBroker(host = "localhost", port = "8500")
@PactBroker(scheme = "${pactbroker.scheme}", host = "${pactbroker.host}", port = "${pactbroker.port}")
// provider name
@Provider(PactConfig.PACT_PROVIDER)

// use this instead if not fetching remotely but from static folder
//@PactFolder("pacts")

@VerificationReports(value = {"console","json","markdown"})
@Slf4j
public class UserControllerJunit4Test {

    //Create an instance of your controller.  We cannot autowire this as we're not using (and don't want to use)  a Spring test runner.
    @InjectMocks
    private UserController userController = new UserController();

    //Create an instance of your controller advice (if you have one).  This will be passed to the MockMvcTarget constructor to be wired up with MockMvc.
    //@InjectMocks
   // private CustomControllerAdvice customControllerAdvice = new CustomControllerAdvice();

    //Mock your service logic class.  We'll use this to create scenarios for respective provider states.
    @Mock
    private UserService userService;

    //Create a new instance of the MockMvcTarget and annotate it as the TestTarget for PactRunner
    @TestTarget
    public final MockMvcTarget target = new MockMvcTarget();

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

        //initialize your mocks using your mocking framework
        MockitoAnnotations.initMocks(this);

//        TestRestTemplate testRestTemplate = new TestRestTemplate();
//        target.setMessageConverters(testRestTemplate.getRestTemplate().getMessageConverters());
        //configure the MockMvcTarget with your controller and controller advice
        target.setControllers(userController);

//        target.setControllerAdvice(...);

        target.setPrintRequestResponse(true);
    }

    @After //Method will be run before each test of interaction
    public void after() {
        log.info("after test method");
    }

    //=======================================
    // Start of state and Pacts
    //=======================================
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