package com.myproject.pact.consumer;


import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.PactProviderRule;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.consumer.xml.PactXmlBuilder;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.myproject.pact.consumer.domain.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xmlunit.assertj.XmlAssert;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

/*
This will use XML as the request response
 */
@Slf4j
public class UserXmlConsumerTest {

    // Define the MockServer
    @Rule
    public PactProviderRule mockProvider = new PactProviderRule(PactConfig.PACT_PROVIDER, this);

    private RestTemplate restTemplate = new RestTemplate();

    // Define consumer-provider pact/s
    @Pact(provider = PactConfig.PACT_PROVIDER, consumer = PactConfig.PACT_CONSUMER)
    @SneakyThrows
    public RequestResponsePact createUser(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.TEXT_XML_VALUE);

        String xml ="<root><name age=\"31\">testuser</name><email>abc@yahoo.com</email></root>";
        Document document = loadXMLFromString(xml);
        PactXmlBuilder bodyResponse = new PactXmlBuilder("sample");
        bodyResponse.setDoc(document);

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Content-Type", MediaType.TEXT_XML_VALUE);
        requestHeaders.put("SOAPAction", "http://ws.app.com/getVersion");

        User user = new User("testuser", "abc@yahoo.com", 31);
        XmlMapper xmlMapper = new XmlMapper();
        String requestXml = xmlMapper.writeValueAsString(user);
        log.info("requestXml: {}", requestXml);

        //@formatter:off
        return builder
                // Provider state
                .given("create user XML")
                .uponReceiving("a request to save user XML")
                .path("/api/userXml")
                .headers(requestHeaders)
                .body(requestXml)
                .method(RequestMethod.POST.name())
                .willRespondWith()
                .headers(headers)
                .status(201).body(bodyResponse).toPact();
        //@formatter:on
    }

    @Pact(provider = PactConfig.PACT_PROVIDER, consumer = PactConfig.PACT_CONSUMER)
    @SneakyThrows
    public RequestResponsePact getUser(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.TEXT_XML_VALUE);


        String xml ="<root><name age=\"31\">testuser</name><email>abc@yahoo.com</email></root>";
        Document document = loadXMLFromString(xml);
        PactXmlBuilder bodyResponse = new PactXmlBuilder("sample");
        bodyResponse.setDoc(document);


        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("SOAPAction", "http://ws.app.com/getVersion");

        //@formatter:off
        return builder
                // Provider state
                .given("get user XML")
                .uponReceiving("a request to get user XML")
                .path("/api/userXml")
//                .headers(requestHeaders)

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

        XmlMapper xmlMapper = new XmlMapper();
        String xml = xmlMapper.writeValueAsString(user);
        log.info("xml: {}", xml);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.set("SOAPAction", "http://ws.app.com/getVersion");
        HttpEntity<Object> request = new HttpEntity<Object>(xml, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(mockProvider.getUrl() + "/api/userXml", request, String.class);
        assertEquals(201, responseEntity.getStatusCodeValue());

        String xmlResponse = responseEntity.getBody();

        XmlAssert.assertThat(xmlResponse).valueByXPath("/root/name").isEqualTo("testuser");
        XmlAssert.assertThat(xmlResponse).valueByXPath("/root/email").isEqualTo("abc@yahoo.com");
        XmlAssert.assertThat(xmlResponse).valueByXPath("/root/name/@age").isEqualTo(31);

    }


    @Test
    @PactVerification(value = PactConfig.PACT_PROVIDER, fragment = "getUser")
    public void api_getUser_success() throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.set("SOAPAction", "http://ws.app.com/getVersion");

        HttpEntity<Object> request = new HttpEntity<Object>( headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(mockProvider.getUrl() + "/api/userXml", HttpMethod.GET, request, String.class);
        assertEquals(200, responseEntity.getStatusCodeValue());

        String xmlResponse = responseEntity.getBody();

        XmlAssert.assertThat(xmlResponse).valueByXPath("/root/name").isEqualTo("testuser");
        XmlAssert.assertThat(xmlResponse).valueByXPath("/root/email").isEqualTo("abc@yahoo.com");
        XmlAssert.assertThat(xmlResponse).valueByXPath("/root/name/@age").isEqualTo(31);
    }


    public static Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
}
