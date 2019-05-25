package com.myproject.pact.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactSpecVersion;
import au.com.dius.pact.model.RequestResponsePact;
import com.jayway.jsonpath.JsonPath;
import com.myproject.pact.consumer.domain.User;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class UserConsumerTest {

    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("user_provider", PactSpecVersion.V3, this);
    private RestTemplate restTemplate=new RestTemplate();


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

}
