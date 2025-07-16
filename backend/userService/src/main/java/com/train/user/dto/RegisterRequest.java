package com.train.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RegisterRequest {

	private String name;
	private String email;
	private String password;
	private String phone;
}
