package org.prezrohit.securefilestorage.controllers;

import org.prezrohit.securefilestorage.dtos.LoginUserDto;
import org.prezrohit.securefilestorage.dtos.RegisterUserDto;
import org.prezrohit.securefilestorage.entities.EncryptionKeys;
import org.prezrohit.securefilestorage.entities.LoginResponse;
import org.prezrohit.securefilestorage.entities.User;
import org.prezrohit.securefilestorage.services.crypto.EncryptionKeysService;
import org.prezrohit.securefilestorage.services.security.AuthenticationService;
import org.prezrohit.securefilestorage.services.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final EncryptionKeysService encryptionKeysService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, EncryptionKeysService encryptionKeysService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.encryptionKeysService = encryptionKeysService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        EncryptionKeys encryptionKeys = encryptionKeysService.generateKeys();
        User registerUser = authenticationService.signup(registerUserDto, encryptionKeys);
        return ResponseEntity.ok(registerUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(authenticatedUser.getEmail(), jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }
}
