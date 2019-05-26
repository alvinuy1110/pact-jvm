package com.myproject.pact.provider.controller;

import com.myproject.pact.provider.domain.User;
import com.myproject.pact.provider.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

	private UserService userService ;


	public UserController(UserService userService) {
		this.userService=userService;
	}
	
	@PostMapping(value="/user",produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<User> saveUser(@RequestBody User user){
		User userResult = userService.saveUser(user);

		return new ResponseEntity<User>(userResult, HttpStatus.CREATED);
	}

}
