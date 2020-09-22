Pact Provider
=============

This is to demonstrate using Pact as Provider.

NOTE: 
* For publishing provider verification results to a pact broker, make sure the Java system property pact.provider.version is set with the version of your provider.
* When a consumer publishes a new Pact to the broker, the provider will fail immediately unless it adds a new verification test


JUnit 4
=======

Maven Setup
-----------

## Dependencies

```
        <dependency>
            <groupId>au.com.dius.pact.provider</groupId>
            <artifactId>junit</artifactId>
            <version>${pact-jvm.version}</version>
            <scope>test</scope>
        </dependency>
```


### Spring support

```
        <dependency>
            <groupId>au.com.dius</groupId>
            <artifactId>pact-jvm-provider-spring</artifactId>
            <version>4.0.10</version>
            <scope>test</scope>
        </dependency>
```


Writing Tests
-------------

## Example of E2E with mock Service

This is an example of a Spring Boot Test with Pact.  

See [UserProviderJunit4Test](./src/test/java/com/myproject/pact/provider/UserProviderJunit4Test.java)

### Step 1: Create Test harness

```
@RunWith(SpringRestPactRunner.class)

@SpringBootTest(classes = PactJvmProviderApplication.class,
        properties = {"spring.profiles.active=test", "spring.cloud.config.enabled=false"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// settings for pact
//@PactBroker(host = "localhost", port = "8500")
@PactBroker(scheme = "${pactbroker.scheme}", host = "${pactbroker.host}", port = "${pactbroker.port}")
// provider name
@Provider("user_provider")

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

```

Note:

* Specify the broker location "@PactBroker"
* Use the correct provider value "@Provider"
* Use SpringRestPactRunner
* see [application.yaml](./src/test/resources/application.yaml) for externalized test config

### Step 2: Create the Test

This will be responsible for validating the pact against this instance of the Provider.

```
    // must match the state created by user
    @State(value = "create user")
    public void createUserState() throws Exception {

        // TODO this should fail as the body doesnt match the pact!!!, instead it passes
        // Optional mocks.  Leave empty to invoke real service like E2E
        User user = new User("testuser", "abc@yahoo.com", 311);
        when(userService.saveUser(any(User.class))).thenReturn(user);
    }
```

Note: <b>You have to run the test as a class, not method level.</b>



## Example of Controller with mock Service

This is an example of a MockMVC Controller test.  This is not an E2E test, and pure mocks involved.   

See [UserControllerJunit4Test](./src/test/java/com/myproject/pact/provider/controller/UserControllerJunit4Test.java)

### Step 1: Create Test harness

```
@RunWith(SpringRestPactRunner.class)

@SpringBootTest(classes = PactJvmProviderApplication.class,
        properties = {"spring.profiles.active=test", "spring.cloud.config.enabled=false"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// settings for pact
//@PactBroker(host = "localhost", port = "8500")
@PactBroker(scheme = "${pactbroker.scheme}", host = "${pactbroker.host}", port = "${pactbroker.port}")
// provider name
@Provider("user_provider")

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

```

Note:

* Specify the broker location "@PactBroker"
* Use the correct provider value "@Provider"
* Use SpringRestPactRunner
* see [application.yaml](./src/test/resources/application.yaml) for externalized test config

### Step 2: Create the Test

This will be responsible for validating the pact against this instance of the Provider.

```
    // must match the state created by user
    @State(value = "create user")
    public void createUserState() throws Exception {

        // TODO this should fail as the body doesnt match the pact!!!, instead it passes
        // Optional mocks.  Leave empty to invoke real service like E2E
        User user = new User("testuser", "abc@yahoo.com", 311);
        when(userService.saveUser(any(User.class))).thenReturn(user);
    }
```

Note: <b>You have to run the test as a class, not method level.</b>


Pact Verification
=================

## JUnit4

If you are using JUnit, you don't need the maven plugin.

Just trigger like running tests

```
mvn clean test
or
mvn clean verfiy
```

### Optional Pact Arguments

|Argument|Description|
|--------|-----------|
|pact.provider.tag|comma-separated value to add tags|
|pact.verifier.publishResults|true - to submit to broker; default is false|
|pact.verification.report|comma-separated value to specify type of reports. Values: "console","json","markdown"|
|pact.verification.reportDir| the directory to write reports to (defaults to "target/pact/reports")|

#### Usage

```
mvn clean test -Dpact.provider.tag=someTag
```

## Maven Plugin

TODO: create a different project to verify this

```
mvn pact:verify
```

### Pact Provider State/ Callback

see https://medium.com/testvagrant/pact-steering-providers-with-provider-state-callbacks-179252a5c63e

#### Notes

* Having 2 states with same name: (1) with Params; and (2) without Params; both are called
* Substitution of state values does not seem to work with Spring 


PACT CLI
--------

* https://github.com/pact-foundation/pact-ruby-standalone/releases
* https://pactflow.io/blog/deploying-your-microservices-with-confidence-using-can-i-deploy/
* https://kreuzwerker.de/post/integrating-contract-tests-into-build-pipelines-with-pact-broker-and

## Setup

### Releases
https://github.com/pact-foundation/pact-ruby-standalone/releases 

### Linux 64

```
curl -LO https://github.com/pact-foundation/pact-ruby-standalone/releases/download/v1.88.5/pact-1.88.5-linux-x86_64.tar.gz
tar xzf pact-1.88.5-linux-x86_64.tar.gz
```

TODO
----


* Response body mismatch but still passes

* state pass into provider (done) 
* state pass from provider  (not working as expected)


* run with filter 

* spring endpoint example???

