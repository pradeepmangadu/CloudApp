package com.hr.ppmtool.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.hr.ppmtool.domain.User;
import com.hr.ppmtool.exceptions.UserNameAlreadyExistsException;
import com.hr.ppmtool.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public User saveUser(User newUser) {
		try {
			newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
			newUser.setUsername(newUser.getUsername());
			// UserName has to be Unique

			// Make sure password and Confirm password Match

			// Make sure we dont persist or show the confirm password
			newUser.setConfirmPassword("");
			return userRepository.save(newUser);
		} catch (Exception e) {
			throw new UserNameAlreadyExistsException("UserName '" + newUser.getUsername() + "' already exits");

		}

	}
}
