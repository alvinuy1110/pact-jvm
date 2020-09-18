Pact Consumer
=============

This uses a WireMock server to essentially mock HTTP Responses.

JUnit 4
=======

Maven Setup
-----------

## Dependencies

Refer to https://docs.pact.io/implementation_guides/jvm/consumer

### Junit 4
```
        <dependency>
            <groupId>au.com.dius.pact.consumer</groupId>
            <artifactId>junit</artifactId>
            <version>4.1.7</version>
            <scope>test</scope>
        </dependency>
```

### Junit 5

```
        <dependency>
            <groupId>au.com.dius.pact.consumer</groupId>
            <artifactId>junit5</artifactId>
            <version>4.1.7</version>
            <scope>test</scope>
        </dependency>
```

Consumer Steps for Writing Pacts
--------------------------------

In this example, we are writing test requiring JUnit. Other frameworks like JUnit5 may follow the same pattern.

## Writing Tests

### Step 1: Create JUnit Rule

```
    @Rule
    public PactProviderRule mockProvider = new PactProviderRule(PactConstants.PACT_PROVIDER,this);
```
The server and port are randomized.  This will start up a MockServer to load the pacts into.

### Step 2: Create the Pact

This will be responsible for creating what the expected response should be

#### Example of JSON REST

```

    @Pact(provider = "user_provider", consumer = "user_consumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);


        PactDslJsonBody bodyResponse = new PactDslJsonBody()
                .stringValue("name", "testuser")
                 .stringType("email", "abc@yahoo.com")
                .integerType("age", 31);

        return builder.given("create user").uponReceiving("a request to save user")
                .path("/api/user")
                .body(bodyResponse)
                .method(RequestMethod.POST.name())
                .willRespondWith()
                .headers(headers)
                .status(201).body(bodyResponse).toPact();
    }
```


### Step 3: Create the Test

This should be your normal test.

Things to note:

* annotate with "@PactVerification" with the Provider name
* make sure to send to the URL defined by Pact "mockProvider.getUrl()"

```

    @Test
	@PactVerification( PactConstants.PACT_PROVIDER)
	public void testCreateInventoryConsumer() throws IOException {

        User user = new User("testuser", "abc@yahoo.com", 31);
    	HttpHeaders headers=new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    	HttpEntity<Object> request=new HttpEntity<Object>(user, headers);
    	ResponseEntity<String> responseEntity=restTemplate.postForEntity(mockProvider.getUrl()+"/api/user", request, String.class);
        assertEquals(201, responseEntity.getStatusCodeValue());
    	assertEquals("testuser", JsonPath.read(responseEntity.getBody(),"$.name"));
    	assertEquals("abc@yahoo.com", JsonPath.read(responseEntity.getBody(),"$.email"));
    	assertEquals((Integer)31, (Integer) JsonPath.read(responseEntity.getBody(),"$.age"));
	}
```

## Step 4: Run the test

```
mvn clean test
```

The pacts are stored in "target/pacts" directory (configurable based on maven plugin).


## Step 4: Run maven build to publish the pacts to the pact broker

This should be done only when ready to share.  Consider changing the maven goal.

```
mvn clean install pact:publish
```
Note: a the goal `install` can be replaced as long as the test executes

### If you want just to publish pacts

```
mvn pact:publish
```

### Maven Plugin Setup

This is to allow publishing the pact to the broker.

Ensure that the plugin (group, artifact, version) matches the dependency version.

```
<build>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>2.22.2</version>
                <configuration>
                    <systemPropertyVariables>
                        <pact.rootDir>${pact.directory}</pact.rootDir>
                    </systemPropertyVariables>
                </configuration>
            </plugin>
            <plugin>
                <groupId>au.com.dius.pact.provider</groupId>
                <artifactId>maven</artifactId>
                <version>${pact-jvm.version}</version>

                <!-- publish consumer to broker -->
                <configuration>
                    <!-- Defaults to ${project.build.directory}/pacts -->
                    <pactDirectory>${pact.directory}</pactDirectory>
                    <!-- where the broker is -->
                    <pactBrokerUrl>${pact-broker.url}</pactBrokerUrl>

                    <!-- Defaults to ${project.version} -->
                    <!--                    <projectVersion>1.0.100</projectVersion> -->
                    <trimSnapshot>true</trimSnapshot> <!-- Defaults to false -->
                    <!-- Defaults to false -->
                    <skipPactPublish>false</skipPactPublish>

                </configuration>
            </plugin>
        </plugins>    </build>
```

## Writing Message Tests

### Sample

see [MessageConsumerTest](./src/test/java/com/myproject/pact/consumer/MessageConsumerTest.java)

### Known Limitations

```
Cannot merge pacts as they are not compatible
```

* Assign a different consumer name to messaging vs HTTP

Writing Pacts Advance
---------------------

See https://github.com/DiUS/pact-jvm/blob/v3.5.x/pact-jvm-consumer-junit/src/test/java/au/com/dius/pact/consumer/pactproviderrule/PactProviderWithMultipleFragmentsTest.java#L103

## Terminology

* Fragment - pointer to the Pact to use
* State - identifies a unique identifier for the MockServer to respond to

## Writing Tests

* Use fragments to point to the Pact by putting the methodName for the Pact

### Provide Unique/ Proper State

* This example is to give info to the Broker as well and end user what to display

```
PactDslWithProvider builder....
   builder.given("create user XML")
                .uponReceiving("a request to save user")
```
## Pact Format

* XML - use PactXmlBuilder
* JSON - use PactDslJsonBody

## Observations

* Using Junit Rule, the pacts must be in same class as test or the super class



## TODO

* how to write pacts (matching patterns, etc.)
* tests and provider states
* using tags in publishing

