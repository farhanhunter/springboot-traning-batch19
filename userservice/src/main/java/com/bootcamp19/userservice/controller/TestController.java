package com.bootcamp19.userservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/v1/test")
public class TestController {

    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello from userservice";
    }
}
