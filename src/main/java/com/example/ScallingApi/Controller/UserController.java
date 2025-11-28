package com.example.ScallingApi.Controller;

import com.example.ScallingApi.Entity.User;
import com.example.ScallingApi.Service.UserService;
import com.example.ScallingApi.util.PaginationRequest;
import com.example.ScallingApi.util.PagingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    private RedisConnectionFactory factory;  // field injection or constructor injection in config/service

    @GetMapping("/test")
    public String test() {
        var conn = factory.getConnection();
        return conn.ping();
    }

    @GetMapping("/users")
    public ResponseEntity<PagingResult<User>> getAllUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {

        PaginationRequest request = new PaginationRequest(page, size, sortField, direction);
        PagingResult<User> result = userService.getAllUsers(request);
        return ResponseEntity.ok(result);
    }





}
