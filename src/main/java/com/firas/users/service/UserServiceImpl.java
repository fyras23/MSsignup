package com.firas.users.service;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firas.users.entities.Role;
import com.firas.users.entities.User;
import com.firas.users.repos.RoleRepository;
import com.firas.users.repos.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.Random;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;





@Transactional
@Service
public class UserServiceImpl implements UserService{
	@Autowired
    UserRepository userRep;
    @Autowired
    RoleRepository roleRep;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String mailAddress;
    @Override
    public User saveUser(User user) {
        Random random = new Random();
        String verificationCode = String.format("%04d", random.nextInt(10000));

        String emailSubject = "Activation Code";
        String emailBody = "<html><body>" +
                          "<h1>Your Activation Code</h1>" +
                          "<p>Thank you for registering. Your activation code is:</p>" +
                          "<p style='font-size: 24px; background-color: #f2f2f2; padding: 10px; border-radius: 5px;'>" +
                          verificationCode +
                          "</p>" +
                          "</body></html>";

        sendHtmlEmail(user.getEmail(), emailSubject, emailBody);

        user.setVerificationCode(verificationCode);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRep.save(user);
    }

    private void sendHtmlEmail(String email, String subject, String body) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, true); // Set the body as HTML
            helper.setFrom(mailAddress);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            // Handle any exceptions
        }
    }

    @Override
    public User addRoleToUser(long id, Role r) {
        User usr = userRep.findUserById(id);

        List<Role> roles = usr.getRoles();
        roles.add(r);

        usr.setRoles(roles);

        return userRep.save(usr);
    }


    @Override
    public List<User> findAllUsers() {
        return userRep.findAll();
    }

    @Override
    public Role addRole(Role role) {
        return roleRep.save(role);
    }
    @Override
    public User findUserByUsername(String username) {
        return userRep.findByUsername(username);
    }


    @Override
    public User findUserById(Long id) {
        return userRep.findById(id).get();
    }

    @Override
    public List<Role> findAllRoles() {
        return roleRep.findAll();
    }
    @Override
    public Role findRoleById(Long id) {
        return roleRep.findRoleById(id);
    }

    @Override
    public void deleteUser(long id) {
        userRep.deleteByUserId(id);
    }
    @Override
    public User removeRoleFromUser(long id,Role r)
    {
        User user = userRep.findUserById(id);
        List<Role> listOfRoles = user.getRoles();
        listOfRoles.remove(r);
        userRep.save(user);
        return user;
    }

    @Override
    public List<Role> findUserRolesById(Long id) {
        User user = userRep.findUserById(id);
        List<Role> listOfRoles = user.getRoles();
        return listOfRoles;
    }

    @Override
    public User activateUser(String username, String code) {
    	 User user = userRep.findByUsername(username);
    	    if (user.getVerificationCode().equals(code)) {
    	        user.setEnabled(true);
    	        user.setVerificationCode(null);

    	        // Set the user's role to "user"
    	        int p =2;
    	        Role userRole = roleRep.findRoleByIdS(p);
    	        if (userRole != null) {
    	            user.setRoles(List.of(userRole));
    	        }

    	        userRep.save(user);
    	    }
    	    return user;
        
    }
}
