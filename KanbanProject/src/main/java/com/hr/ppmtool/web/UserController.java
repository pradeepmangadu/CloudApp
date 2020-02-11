package com.hr.ppmtool.web;

import static com.hr.ppmtool.security.SecurityConstants.TOKEN_PREFIX;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr.ppmtool.Validator.UserValidator;
import com.hr.ppmtool.domain.User;
import com.hr.ppmtool.payload.JWTLoginSuccessResponse;
import com.hr.ppmtool.payload.LoginRequest;
import com.hr.ppmtool.security.JWTTokenProvider;
import com.hr.ppmtool.services.MapValidationErrorService;
import com.hr.ppmtool.services.UserService;

@RestController
@RequestMapping("/api/users/")
public class UserController {

	@Autowired
	private MapValidationErrorService mapValidationErrorService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserValidator userValidator;

	@Autowired
	private JWTTokenProvider tokenProvider;

	@Autowired
	private AuthenticationManager authenticationManager;

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {
		ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
		if (errorMap != null)
			return errorMap;
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = TOKEN_PREFIX + tokenProvider.generateToken(authentication);

		return ResponseEntity.ok(new JWTLoginSuccessResponse(true, jwt));
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result) {

		// Validate Password Match
		userValidator.validate(user, result);

		ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
		if (errorMap != null)
			return errorMap;

		User newUser = userService.saveUser(user);
		return new ResponseEntity<User>(newUser, HttpStatus.CREATED);

	}

}
