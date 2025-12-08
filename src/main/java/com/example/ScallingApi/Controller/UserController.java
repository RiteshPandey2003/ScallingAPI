package com.example.ScallingApi.Controller;

import com.example.ScallingApi.Entity.User;
import com.example.ScallingApi.Mapper.CursorResponse;
import com.example.ScallingApi.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<CursorResponse<User>> getAllUsers(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int limit) {

        CursorResponse<User> response = userService.getAllUsers(cursor, limit);
        return ResponseEntity.ok(response);
    }
}

