package com.train.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.train.user.dto.LoginRequest;
import com.train.user.dto.LoginResponse;
import com.train.user.dto.RegisterRequest;
import com.train.user.model.User;
import com.train.user.security.JWTUtil;
import com.train.user.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
    private UserService userService;
    private JWTUtil jwtUtil;
    
    @Autowired
    public UserController(UserService userService, JWTUtil jwtUtil) {
		this.userService = userService;
		this.jwtUtil = jwtUtil;
	}
    
    
    @GetMapping("/")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @GetMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    	User user = userService.authenticateUser(request.getEmail(),request.getPassword());
    	Map<String,Object> mp = new HashMap<>();
    	mp.put("role", user.getRole().toString());
    	String token = jwtUtil.generateToken(mp, user);
    	if(token != null && token.length()>0) {
    		return new ResponseEntity<LoginResponse>(new LoginResponse(token, user.getName()), HttpStatus.OK);
    	}
    	return ResponseEntity.badRequest().build();
    }

}
