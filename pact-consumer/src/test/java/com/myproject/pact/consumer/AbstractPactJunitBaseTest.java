package com.myproject.pact.consumer;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.PactProviderRule;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.Rule;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPactJunitBaseTest {

    // Define the MockServer
    @Rule
    public PactProviderRule mockProvider = new PactProviderRule(PactConfig.PACT_PROVIDER, this);

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

}
