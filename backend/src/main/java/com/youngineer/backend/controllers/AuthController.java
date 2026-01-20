package com.youngineer.backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import java.time.Duration;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;

import com.youngineer.backend.dto.requests.LoginRequest;
import com.youngineer.backend.dto.requests.SignupRequest;
import com.youngineer.backend.dto.responses.BackendResponse;
import com.youngineer.backend.dto.responses.ServiceResponse;
import com.youngineer.backend.services.AuthService;
import com.youngineer.backend.utils.JwtHelper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<BackendResponse> signup(@RequestBody SignupRequest signupRequest, HttpServletResponse response) {
        try {
            ServiceResponse serviceResponse = authService.signup(signupRequest);
            if(serviceResponse.isSuccessful()) {
                return ResponseEntity.ok(new BackendResponse("OK", null));
            }

            throw new Exception(serviceResponse.message());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new BackendResponse(e.getMessage(), null));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BackendResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BackendResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<BackendResponse> userLogin(@Validated @RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            if (authentication.isAuthenticated()) {
                String accessToken = authService.generateAccessToken(request.email());

                ResponseCookie cookie = ResponseCookie.from("token", accessToken)
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .maxAge(Duration.ofHours(1))
                        .build();
                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

                BackendResponse responseDto = new BackendResponse("Login successful!", "Login successful!");
                return ResponseEntity.ok(responseDto);
            } else {
                throw new BadCredentialsException("Error authenticating the user");
            }
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new BackendResponse("Invalid credentials", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BackendResponse("An unexpected error occurred: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/logout")
    public ResponseEntity<BackendResponse> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofSeconds(0))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new BackendResponse("Logout successful!", "Logout successful!"));
    }

    @PostMapping("/isLoggedIn")
    public ResponseEntity<Boolean> isLoggedIn(HttpServletRequest request) {
        String token = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null) {
            String emailId = JwtHelper.extractEmail(token);
            if(JwtHelper.validateToken(token, emailId))
                return ResponseEntity.ok(true);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(false);
    }
}
