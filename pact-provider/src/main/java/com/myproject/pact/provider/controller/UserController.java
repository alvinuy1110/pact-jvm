package com.myproject.pact.provider.controller;

import com.myproject.pact.provider.domain.User;
import com.myproject.pact.provider.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	private UserService userService ;

	public UserController() {
	}
	
	@PostMapping(value="/user",produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<User> saveUser(@RequestBody User user){
		User userResult = userService.saveUser(user);

		return new ResponseEntity<User>(userResult, HttpStatus.CREATED);
	}

	@RequestMapping(value="/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> getUser(){
		User userResult = userService.getUser();

		return new ResponseEntity<User>(userResult, HttpStatus.OK);
	}

	@RequestMapping(value="/userXml", method = RequestMethod.GET, produces = MediaType.TEXT_XML_VALUE)
	public ResponseEntity<String> getUserXml(){

		String xml = "<root><name age=\"31\">testuser</name><email>abc@yahoo.com</email></root>";

		return new ResponseEntity<String>(xml, HttpStatus.OK);
	}

	@PostMapping(value="/userXml",consumes = MediaType.TEXT_XML_VALUE, produces= MediaType.TEXT_XML_VALUE)
	public ResponseEntity<String> saveUserXml(@RequestBody User user){

		String xml ="<root><name age=\"31\">testuser</name><email>abc@yahoo.com</email></root>";
		return new ResponseEntity(xml, HttpStatus.CREATED);
	}

	@RequestMapping(value="/userSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> searchUser(){
		User userResult = userService.getUser();

		return new ResponseEntity<User>(userResult, HttpStatus.OK);
	}

	public static Document loadXMLFromString(String xml) throws Exception
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}
}
