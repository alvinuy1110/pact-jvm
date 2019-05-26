Pact Provider
=============

Maven Setup
-----------

## Dependencies

```
	<dependency>
	    <groupId>au.com.dius</groupId>
	    <artifactId>pact-jvm-provider-junit_2.12</artifactId>
	    <version>3.6.7</version>
	     <scope>test</scope>
	</dependency>
```


### Spring support

```
    <dependency>
        <groupId>au.com.dius</groupId>
        <artifactId>pact-jvm-provider-spring_2.12</artifactId>
        <version>3.6.7</version>
        <scope>test</scope>
    </dependency>
```


Writing Pacts
-------------

Writing test requires JUnit.

## Writing Tests

This is an example of a Spring Boot Test with Pact
### Step 1: Create Test harness

```

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

...
   @LocalServerPort
    int serverPort;

    // if using HttpTarget, pass in a port that will boot the app
    @TestTarget
//    public final Target target = new HttpTarget(serverPort);
    public final Target target = new SpringBootHttpTarget(serverPort);

```

Note:

* Specify the broker location "@PactBroker"
* Use the correct provider value "@Provider"

### Step 2: Create the Test

This will be responsible for validating the pact against this instance of the Provider.

```
  // must match the state created by user
    @State (value = "create user")
    public void createUserState() throws Exception {

        // Optional mocks.  Leave empty to invoke real service like E2E
        User user = new User("testuser", "abc@yahoo.com", 311);
        when(userService.saveUser(any(User.class))).thenReturn(user);
    }
```

Note: You have to run the test as a class, not method level.



TODO
----

* Response body mismatch but still passes
* Successful pact verification not published, but failed one does

