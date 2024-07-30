package com.example.demo.hybrid.controller;

import com.example.demo.hybrid.entity.UsersEntity;
import com.example.demo.hybrid.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/save")
    public ResponseEntity<?> createUser(@RequestBody UsersEntity user) {
        try {
            UsersEntity savedUser = userService.saveUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getStackTrace());
        }
    }

    @PostMapping("/get-user")
    public ResponseEntity<UsersEntity> getUser(@RequestBody Map<String, String> requestData) {

        if (Objects.isNull(requestData.get("userId")) || requestData.get("userId").isEmpty() ) {
            // Handle case where publicKeyBase64 is missing or empty
            return ResponseEntity.badRequest().build();
        }

        if (Objects.isNull(requestData.get("publicKeyBase64")) || requestData.get("publicKeyBase64").isEmpty() ) {
            // Handle case where publicKeyBase64 is missing or empty
            return ResponseEntity.badRequest().build();
        }

        Long userId = Long.parseLong(requestData.get("userId")); // Assuming userId is passed from FE
        String publicKeyBase64 = requestData.get("publicKeyBase64");


        try {
            UsersEntity user = userService.getUser(userId, publicKeyBase64);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
