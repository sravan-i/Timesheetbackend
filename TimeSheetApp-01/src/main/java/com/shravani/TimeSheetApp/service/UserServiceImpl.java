package com.shravani.TimeSheetApp.service;
import com.shravani.TimeSheetApp.dto.UserDto;
import com.shravani.TimeSheetApp.dto.UserMapper;
import com.shravani.TimeSheetApp.entity.Role;
import com.shravani.TimeSheetApp.entity.User;
import com.shravani.TimeSheetApp.repository.RoleRepository;
import com.shravani.TimeSheetApp.repository.UserRepository;
import com.shravani.TimeSheetApp.util.EmailUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    
    
    @Autowired
    private EmailUtils emailUtil;

    @Autowired
    private RoleRepository roleRepository;
    @Override
    public List<UserDto> findAllUsers() {

        List<User> users=userRepository.findAll();

        return users.stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public UserDto findUserByEmail(String email) {
        User user=userRepository.findByEmail(email);
        if(user==null)
            return null;
        return userMapper.toDto(user);
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        User user=userMapper.toEntity(userDto);
        List<Role> roles=new ArrayList<>();
        for(String roleName:userDto.getRoles())
        {
            Role role=roleRepository.findByName(roleName);
            if(role==null){
                role= Role.builder().name(roleName).build();
                roleRepository.save(role);
            }
            roles.add(role);
        }
        user.setRoles(roles);
        User savedUser=userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto findUserByName(String userName) {
        User user= userRepository.findByEmail(userName);
        if(user!=null)
            return userMapper.toDto(user);
        else
            return null;

    }

	
	
	
	/* private void sendPasswordEmail(String recipientEmail, String password) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(recipientEmail);
    message.setSubject("Password Reset");
    message.setText("Your new password is: " + password);

    javaMailSender.send(message);
} */

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


	@Override
	public boolean forgotPwd(String email) {
		// check Record Presence in db with given mail
				User entity = userRepository.findByEmail(email);

				// if record not available sent msg
				if (entity == null) {
					return false;
				}

				// if record available in db sent password and sent success msg
				String subject = "<h1>Recover Password<h1>";
				String body = "Your Pwd :: " + entity.getPassword();

				emailUtil.sendEmail(email, subject, body);

				return true;
		
	}


}
