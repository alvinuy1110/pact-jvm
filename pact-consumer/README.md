Pact Consumer
=============

This uses a WireMock server to essentially mock HTTP Responses.

Maven Setup
-----------

## Dependencies

```
	<dependency>
		    <groupId>au.com.dius</groupId>
		    <artifactId>pact-jvm-consumer-junit_2.12</artifactId>
		    <version>3.6.7</version>
		    <scope>test</scope>
		</dependency>
```

## Plugin

This is to allow publishing the pact to the broker.

Ensure that the plugin (group, artifact, version) matches the dependency version.

```
<build>
        <plugins>
            <plugin>
                <groupId>au.com.dius</groupId>
                <artifactId>pact-jvm-provider-maven_2.12</artifactId>
                <version>3.6.7</version>
                <configuration>
                    <!-- where the broker is -->
                    <pactBrokerUrl>http://localhost:8500</pactBrokerUrl>
                    <!-- where the pacts are generated -->
                    <pactDirectory>target/pacts</pactDirectory>


                </configuration>
            </plugin>
        </plugins>
    </build>
```

Reference: https://github.com/DiUS/pact-jvm/tree/master/pact-jvm-provider-maven


Writing Pacts
-------------

Writing test requires JUnit.

## Writing Tests

### Step 1: Create JUnit Rule

```
 @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("user_provider", PactSpecVersion.V3, this);
```


### Step 2: Create the Pact

This will be responsible for creating what the expected response should be

#### Example of JSON REST

```

    @Pact(provider = "user_provider", consumer = "user_consumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);


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

This should your normal test.

Things to note:

* annotate with "@PactVerification"
* make sure to send to the URL defined by Pact "mockProvider.getUrl()"

```

    @Test
	@PactVerification
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

## Step 4: Run maven build to publish the pacts to the pact broker

This should be done only when ready to share.  Consider changing the maven goal.

```
mvn clean install pact:publish
```
Note: a the goal `install` can be replaced as long as the test executes
