package com.myproject.pact.provider.service;

import com.myproject.pact.provider.domain.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	
	
	public User saveUser(User user){
		
		/* for testing the provider end. This service will be mocked */
		return new User("fakeUser", "testemail@xyz.com", 33);
	}

	public User getUser(){

		/* for testing the provider end. This service will be mocked */
		return new User("fakeUser", "testemail@xyz.com", 33);
	}
}
