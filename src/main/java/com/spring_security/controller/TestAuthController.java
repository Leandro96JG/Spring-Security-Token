package com.spring_security.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/method")
//deniega toodo al menos que esté autorizado en el método de la req
@PreAuthorize("denyAll()")
public class TestAuthController {


    @GetMapping("/get")
    //Recordar que también se pueden autorizar los permisos en los security config
    //para autorizar más permisos
    @PreAuthorize("permitAll()")
    public String helloGet(){
        return "Hello World - GET";
    }

    @PostMapping("/post")
    //Creo que hay problemas al usar más de 1 rol con este método
    @PreAuthorize("hasAnyRole('ADMIN','DEVELOPER')")
    public String helloPost(){
        return "Hello World - POST";
    }

    @PutMapping("/put")
    public String helloPut(){
        return "Hello World - PUT";
    }

    @DeleteMapping("/delete")
    public String helloDelete(){
        return "Hello World - DELETE";
    }

    @PatchMapping("/patch")
    @PreAuthorize("hasAnyAuthority('REFACTOR')")
    public String helloPatch(){
        return "Hello World - PATCH";
    }

}
