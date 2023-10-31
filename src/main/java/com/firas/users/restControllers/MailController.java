package com.firas.users.restControllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.firas.users.service.MailService;

import config.MailStructure;

@RestController
@CrossOrigin(origins = "*")
public class MailController {
    @Autowired
    private MailService mailService;

    @PostMapping("/send/{email}")
    public String sendMail (@PathVariable String email, @RequestBody MailStructure mailStructure) {
        mailService.sendMail(email, mailStructure);
        return "Mail sent successfully";
    }
}
