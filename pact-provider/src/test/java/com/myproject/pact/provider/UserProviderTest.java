package com.myproject.pact.provider;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.loader.PactFolder;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringRestPactRunner;
import au.com.dius.pact.provider.spring.target.SpringBootHttpTarget;
import com.myproject.pact.provider.domain.User;
import com.myproject.pact.provider.service.UserService;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith(SpringRestPactRunner.class)
@SpringBootTest(classes = PactJvmProviderApplication.class,
        properties = {"spring.profiles.active=test", "spring.cloud.config.enabled=false"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")

// settings for pact
@PactBroker(host = "localhost", port = "8500")
@Provider("user_provider")

// use this instead if not fetching remotely but from static folder
//@PactFolder("pacts")
public class UserProviderTest {

    @MockBean
    private UserService userService;

    @LocalServerPort
    int serverPort;

    // if using HttpTarget, pass in a port that will boot the app
    @TestTarget
//    public final Target target = new HttpTarget(serverPort);
    public final Target target = new SpringBootHttpTarget(serverPort);

    // must match the state created by user
    @State (value = "create user")
    public void createUserState() throws Exception {

        // TODO this should fail as the body doesnt match the pact!!!, instead it passes
        // Optional mocks.  Leave empty to invoke real service like E2E
        User user = new User("testuser", "abc@yahoo.com", 311);
        when(userService.saveUser(any(User.class))).thenReturn(user);
    }
}