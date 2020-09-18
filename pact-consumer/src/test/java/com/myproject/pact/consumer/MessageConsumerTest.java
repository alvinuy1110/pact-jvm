package com.myproject.pact.consumer;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit.MessagePactProviderRule;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.messaging.MessagePact;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageConsumerTest {

    @Rule
    public MessagePactProviderRule mockProvider = new MessagePactProviderRule(PactConfig.PACT_PROVIDER,this);
    private byte[] currentMessage;

    @Pact(provider = PactConfig.PACT_PROVIDER, consumer = PactConfig.PACT_CONSUMER_MESSAGE)
    public MessagePact createMessagePact(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();
        body.stringValue("testParam1", "value1");
        body.stringValue("testParam2", "value2");

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("contentType", "application/json");

        return builder.given("send message")
                .expectsToReceive("a test message")
                .withMetadata(metadata)
                .withContent(body)
                .toPact();
    }

    @Test
    @PactVerification(value = PactConfig.PACT_PROVIDER, fragment = "createMessagePact")
    public void test() throws Exception {
        assertThat(currentMessage).isNotEmpty();
    }

    public void setMessage(byte[] messageContents) {
        currentMessage = messageContents;
    }
}
