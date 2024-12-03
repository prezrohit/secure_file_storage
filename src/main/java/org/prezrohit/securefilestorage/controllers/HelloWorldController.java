package org.prezrohit.securefilestorage.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prezrohit.securefilestorage.util.ProfileConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hello")
public class HelloWorldController {

    private static final Logger log = LogManager.getLogger(HelloWorldController.class);
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
        log.trace("Hello Profile!");
        log.debug("Hello Profile!");
        log.info("Hello Profile!");
        log.warn("Hello Profile!");
        log.error("Hello Profile!");
        return "Hello Profile " + profileConfiguration.getName() + "!";
    }

}
