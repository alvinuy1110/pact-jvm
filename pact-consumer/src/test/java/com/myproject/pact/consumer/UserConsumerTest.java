package com.myproject.pact.consumer;


import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.PactProviderRule;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
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

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;


public class UserConsumerTest {

    // Define the MockServer
    @Rule
    public PactProviderRule mockProvider = new PactProviderRule(PactConfig.PACT_PROVIDER, this);

    private RestTemplate restTemplate = new RestTemplate();

    // Define consumer-provider pact/s
    @Pact(provider = PactConfig.PACT_PROVIDER, consumer = PactConfig.PACT_CONSUMER)
    public RequestResponsePact createUser(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);


        PactDslJsonBody bodyResponse = new PactDslJsonBody()
                .stringValue("name", "testuser")
                .stringType("email", "abc@yahoo.com")
                .integerType("age", 31);

        //@formatter:off
        return builder
                // Provider state
                .given("create user")
                .uponReceiving("a request to save user")
                .path("/api/user")
                .body(bodyResponse)
                .method(RequestMethod.POST.name())
                .willRespondWith()
                .headers(headers)
                .status(201).body(bodyResponse).toPact();
        //@formatter:on
    }

    @Pact(provider = PactConfig.PACT_PROVIDER, consumer = PactConfig.PACT_CONSUMER)
    public RequestResponsePact getUser(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);


        PactDslJsonBody bodyResponse = new PactDslJsonBody()
                .stringValue("name", "testuser1")
                .stringType("email", "abc1@yahoo.com")
                .integerType("age", 35);

        //@formatter:off
        return builder
                // Provider state
                .given("get user")
                .uponReceiving("a request to get user")
                .path("/api/user")

                .method(RequestMethod.GET.name())
                .willRespondWith()
                .headers(headers)
                .status(200).body(bodyResponse).toPact();
        //@formatter:on
    }

    //============================
    //== Tests
    //============================
    @Test
    @PactVerification(value = PactConfig.PACT_PROVIDER, fragment = "createUser")
    public void user_create_success() throws IOException {

        User user = new User("testuser", "abc@yahoo.com", 31);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<Object> request = new HttpEntity<Object>(user, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(mockProvider.getUrl() + "/api/user", request, String.class);
        assertEquals(201, responseEntity.getStatusCodeValue());

        String json = responseEntity.getBody();
        assertThat(json, hasJsonPath("$.name", is("testuser")));
        assertThat(json, hasJsonPath("$.email", is("abc@yahoo.com")));
        assertThat(json, hasJsonPath("$.age", is(31)));

    }


    @Test
    @PactVerification(value = PactConfig.PACT_PROVIDER, fragment = "getUser")
    public void api_getUser_success() throws IOException {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(mockProvider.getUrl() + "/api/user", String.class);
        assertEquals(200, responseEntity.getStatusCodeValue());

        String json = responseEntity.getBody();
        assertThat(json, hasJsonPath("$.name", is("testuser1")));
        assertThat(json, hasJsonPath("$.email", is("abc1@yahoo.com")));
        assertThat(json, hasJsonPath("$.age", is(35)));
    }

}
