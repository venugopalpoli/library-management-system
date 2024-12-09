package com.library.controller;

import com.library.exception.InvalidUserException;
import com.library.model.AuthenticationRequest;
import com.library.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody AuthenticationRequest authRequest) throws InvalidUserException {
        return new ResponseEntity<String>(jwtUtil.generateToken(authRequest), HttpStatus.CREATED);
    }
}
