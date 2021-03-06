package com.adammendak.todo.controller;

import com.adammendak.todo.exception.UserNotInDatabaseException;
import com.adammendak.todo.model.User;
import com.adammendak.todo.service.LoginService;
import com.adammendak.todo.utility.JsonResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api")
public class LoginController {

    private LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<JsonResponseBody> login (@RequestParam(value = "email") String email,
                                                   @RequestParam(value = "password") String password) {
        User user = null;
        try{
            Optional<User> userOptional =loginService.getUserFromDB(email, password);
            user = userOptional.get();
            log.info("user from db {}", user.toString());
            String jwt = loginService.createJwt(email,user.getEmail(), new Date());
            log.info("jwt {}", jwt);
            return ResponseEntity.status(HttpStatus.OK).header("jwt", jwt).body(new JsonResponseBody(HttpStatus.OK.value(), "login success!"));
        } catch (UserNotInDatabaseException e) {
            log.info("exception {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "wrong user!"));
        } catch (UnsupportedEncodingException e) {
            log.info("exception {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(), "bad request"));
        }
    }
}
