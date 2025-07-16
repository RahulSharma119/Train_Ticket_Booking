package com.train.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.train.user.dto.RegisterRequest;
import com.train.user.model.User;
import com.train.user.repository.UserRepository;

@Service
public class UserService {
	
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	UserService(UserRepository userRepository){
		this.userRepository = userRepository;
		
	}
	
	public List<User> getAllUsers() {
        return userRepository.findAll();
    }
	
	public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User register(RegisterRequest request) {
    	User user = new User();
    	user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        return userRepository.save(user);
    }
    
    public User authenticateUser(String email, String password) {
    	User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User Not found"));
    	if(!passwordEncoder.matches(user.getPassword, password) ) {
    		throw new RuntimeException("Invalid Credentials");
    	}
    	return user;
    	
    }

}
