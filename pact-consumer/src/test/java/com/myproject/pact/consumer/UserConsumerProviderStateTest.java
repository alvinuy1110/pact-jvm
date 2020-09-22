package com.myproject.pact.consumer;


import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.PactProviderRule;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.myproject.pact.consumer.domain.User;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

/**
 * Here we will demonstrate using dynamic provider state to be executed at Provider side.
 */

public class UserConsumerProviderStateTest {

    // Define the MockServer
    @Rule
    public PactProviderRule mockProvider = new PactProviderRule(PactConfig.PACT_PROVIDER, this);

    private RestTemplate restTemplate = new RestTemplate();

    // Define consumer-provider pact/s
    @Pact(provider = PactConfig.PACT_PROVIDER, consumer = PactConfig.PACT_CONSUMER)
    public RequestResponsePact searchUser(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);


        PactDslJsonBody bodyResponse = new PactDslJsonBody()
                .stringValue("name", "testuser") // match value
                .stringType("email", "abc@yahoo.com")  // dont match exact value

                //.stringType("accountNum", "someVal") // dont match value
                .valueFromProviderState("accountNum", "accountNum","someVal")  // variable replaced by provider

                .integerType("age", 35);

        // params to pass to provider (optional, but can be used to initial provider test
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("account", "user1_contract@test.com");
        params.put("fistName", "user1_firstName");
        params.put("lastName", "user1_lastName");
        //@formatter:off
        return builder
                // Provider state
                .given("search_user", params)

                .uponReceiving("a request to search user")
                .path("/api/userSearch")

                //.query("accountNum=123") // This is just a dummy
                .queryParameterFromProviderState("accountNum","${accountNum}", "123") // use the ${} variable to be substituted by Provider

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
    @PactVerification(value = PactConfig.PACT_PROVIDER, fragment = "searchUser")
    @SneakyThrows
    public void api_searchUser_success() throws IOException {
        String url =UriComponentsBuilder.fromHttpUrl(mockProvider.getUrl()).path("/api/userSearch").queryParam("accountNum","123")
                .toUriString();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        assertEquals(200, responseEntity.getStatusCodeValue());

        String json = responseEntity.getBody();
        assertThat(json, hasJsonPath("$.name", is("testuser")));
        assertThat(json, hasJsonPath("$.email", is("abc@yahoo.com")));
        assertThat(json, hasJsonPath("$.age", is(35)));
        assertThat(json, hasJsonPath("$.accountNum", is("someVal")));
    }

}
