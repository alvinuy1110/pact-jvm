package com.myproject.pact.consumer;


import au.com.dius.pact.consumer.junit.PactVerification;
import com.myproject.pact.consumer.domain.User;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

/*
This is just to demonstrate if the pacts are in the super class
 */
public class UserConsumerSubClassTest extends AbstractPactJunitBaseTest {

    private RestTemplate restTemplate = new RestTemplate();


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
