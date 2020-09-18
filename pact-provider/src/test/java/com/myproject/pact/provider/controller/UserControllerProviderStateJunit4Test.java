package com.myproject.pact.provider.controller;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.StateChangeAction;
import au.com.dius.pact.provider.junit.VerificationReports;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.loader.PactFilter;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringRestPactRunner;
import au.com.dius.pact.provider.spring.target.MockMvcTarget;
import com.myproject.pact.provider.PactConfig;
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

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * This will NOT  boot the app.  It uses all mock.
 * This may be way faster than booting up the entire app.
 * It will also bypass any filter and/ or advices if not configured.  This can be both good or bad.
 * <p>
 * Will demo the Provider State callback
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

//@VerificationReports(value = {"console","json","markdown"})
// optional filter to only whitelist selected interactions
@PactFilter({"search_user"})
@Slf4j
public class UserControllerProviderStateJunit4Test {

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

    // Not needed since params are passed in the main state method as well
    // must match the state created by user
    // Setup before interaction is invoked
//    @State(value = "search_user", action = StateChangeAction.SETUP)
    // the Map params is provided by the consumer contract
    public Map<String, Object> setupSearchUserState(Map<String, String> params) throws Exception {
        log.info("Inside Provider state: 'search_user' Setup Method");

        log.info("Provided params by consumer");
        params.forEach((k, v) -> {
            log.info("Key: {}, Value: {}", k, v);
        });

        // Do whatever setup needed.  Here we just assign a static value
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("accountNumber", "2222");

        log.info("Exit Provider state 'search_user' Setup Method");
        return userMap;
    }

    // Teardown after  interaction is invoked
    @State(value = "search_user", action = StateChangeAction.TEARDOWN)
    // the Map params is provided by the consumer contract
    public void teardownSearchUserState(Map<String, String> params) throws Exception {
        log.info("Inside Provider state: 'search_user' Teardown Method");

        log.info("Provided params by consumer");
        params.forEach((k, v) -> {
            log.info("Key: {}, Value: {}", k, v);
        });

        // Do whatever teardown needed.

        log.info("Exit Provider state 'search_user' Teardown Method");
    }

    // Execute actual verification
    @State(value = "search_user")
    // the Map params is provided by the consumer contract
    public void searchUserState(Map<String, String> params) throws Exception {

        log.info("Enter Search user state");
        log.info("Provided params by consumer");
        params.forEach((k, v) -> {
            log.info("Key: {}, Value: {}", k, v);
        });
        // Do Something with the provided value like db creation, etc.

        User user = new User("testuser", "abc@yahoo.com", 311);
        user.setAccountNum("sss"); // here we just add it back

        when(userService.getUser()).thenReturn(user);
        log.info("Exit Search user state");
    }

}