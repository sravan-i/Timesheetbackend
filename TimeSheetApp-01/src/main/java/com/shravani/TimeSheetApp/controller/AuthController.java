package com.shravani.TimeSheetApp.controller;

import com.shravani.TimeSheetApp.dto.UserDto;
import com.shravani.TimeSheetApp.service.UserService;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class AuthController {


    @Autowired
    private UserService userService;
    private JavaMailSender javaMailSender;


    @GetMapping("/users")
    public ResponseEntity<List<UserDto>>  getUsers()
    {
        List<UserDto> users=userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/register")

    public ResponseEntity<UserDto> registeredUser( @RequestBody UserDto userDto,BindingResult result)
    {
        UserDto existingUser=userService.findUserByEmail(userDto.getEmail());
        if(existingUser !=null && existingUser.getEmail() !=null && !existingUser.getEmail().isEmpty())
        {
            result.rejectValue("email",null,"There is already an account registered with the same email");
        }
        if(result.hasErrors()){
            return ResponseEntity.badRequest().body(userDto);
        }
        UserDto savedUser=userService.saveUser(userDto);
        return  ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/user")
    public ResponseEntity<UserDto> getUserProfile(Authentication authentication)
    {
        String userName=authentication.name();
        UserDto existingUser=userService.findUserByName(userName);

        if(existingUser!=null)
        {
            return ResponseEntity.status(HttpStatus.OK).body(existingUser);
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    /* @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {

        if (email != null && password.equals("admin123")) {
            // Login successful
            return ResponseEntity.ok("Login successful");
        } else {
            // Login failed
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }  */

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam("email") String email, @RequestParam("password") String password) {
        // Authenticate the user based on the email and password
        UserDto user = userService.findUserByEmail(email);
        /*if (user == null) {
            // User not found
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Invalid email or password"));
        }

        if (!isValidPassword(user, password)) {
            // Invalid password
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Invalid email or password"));
        }*/

        // Assuming authentication is successful
        // Generate a token or session for the logged-in user
        String token = generateToken(user); // Replace this with your own token generation logic

        // Create a JSON object to return in the response
        Map<String, String> response = new HashMap<>();
        response.put("UserId & UserName", token);

        // Return the JSON object in the response
        return ResponseEntity.ok(response);
    }


    private boolean isValidPassword(UserDto user, String password) {
        // Compare the provided password with the user's stored password
        // You can use your password hashing and verification logic here

        // Assuming a simple comparison for demonstration purposes
        return user.getPassword().equals(password);
    }

    private String generateToken(UserDto user) {
        // Implement your token generation logic here
        // Generate a unique token for the user

        // Replace this with your own token generation code
        String username =  user.getId() + " " +user.getEmail();

        return username;
    }

    

    /*@PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestParam("email") String email) {
        // Check if the email exists in the database
        UserDto user = userService.findUserByEmail(email);

        if (user == null) {
            // User not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "User not found"));
        }

        String password = user.getPassword(); // Get the existing password from the user object

        // Send the password to the user's email
        sendPasswordEmail(user.getEmail(), password);

        // Return a success message
        return ResponseEntity.ok(Collections.singletonMap("message", "Password sent to your email."));
    } */

 // forgot password
	
 		@GetMapping("/forgot")
 		public String forgetPwd() {
 			return "forgot";
 		}

 		@PostMapping("/forgotPwd")
 		public String forgotPwdPage(@RequestParam("email") String email) {
 	        String temp="";
 			boolean status = userService.forgotPwd(email);

 			if (status) {
 				temp= "Your Password sent your mail";
 			} else {
 				temp="Invalid Email";
 			}

 			return temp;
 		}


}
