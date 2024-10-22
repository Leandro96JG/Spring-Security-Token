package com.spring_security.controller;


import com.spring_security.controller.dto.AuthLoginRequest;
import com.spring_security.controller.dto.AuthRegisterRequest;
import com.spring_security.controller.dto.AuthReponse;
import com.spring_security.service.UserDatailServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@PreAuthorize("denyAll()")
public class AuthController {

    private UserDatailServiceImpl userDatailService;

    public AuthController(UserDatailServiceImpl userDatailService) {
        this.userDatailService = userDatailService;
    }

    @PostMapping("/log-in")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AuthReponse>login(@RequestBody @Valid AuthLoginRequest userRequest){
        return new ResponseEntity<>(this.userDatailService.loginUser(userRequest), HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AuthReponse> register(@RequestBody @Valid AuthRegisterRequest authRegisterRequest){
        return new ResponseEntity<>(this.userDatailService.createUser(authRegisterRequest),HttpStatus.CREATED);
    }

}
