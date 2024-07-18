package com.springSecurity.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
//deniega toodo al menos que este autorizado en el metedo de la req
@PreAuthorize("denyAll()")
public class TestAuthController {


    @GetMapping("/get")
    //para autorizar mas permisos
    @PreAuthorize("hasAuthority('READ') or hasAuthority('CREATED')")
    public String helloGet(){
        return "Hello World - GET";
    }

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('READ')")
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
    @PreAuthorize("hasAuthority('REFACTOR')")
    public String helloPatch(){
        return "Hello World - PATCH";
    }

}
