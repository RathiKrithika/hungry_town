package com.food.controller;

import com.food.dto.CreateUser;
import com.food.dto.Login;
import com.food.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService service;
    @PostMapping("/register")
    public ResponseEntity createAnUser(@RequestBody CreateUser c){
       return new ResponseEntity( service.register(c), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Login login){
        return new ResponseEntity(service.login(login), HttpStatus.OK) ;
    }

}
