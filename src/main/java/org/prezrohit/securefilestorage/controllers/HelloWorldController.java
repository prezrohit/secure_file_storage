package org.prezrohit.securefilestorage.controllers;

import org.prezrohit.securefilestorage.util.ProfileConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hello")
public class HelloWorldController {

    private final ProfileConfiguration profileConfiguration;

    public HelloWorldController(ProfileConfiguration profileConfiguration) {
        this.profileConfiguration = profileConfiguration;
    }

    @GetMapping()
    public String hello() {
        return "Hello, unknown!";
    }

    @GetMapping("user")
    public String user() {
        return "Hello User!";
    }

    @GetMapping("admin")
    public String admin() {
        return "Hello Admin!";
    }

    @GetMapping("profile")
    public String profile() {
        return "Hello Profile " + profileConfiguration.getName() + "!";
    }

}
