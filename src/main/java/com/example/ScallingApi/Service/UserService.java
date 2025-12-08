package com.example.ScallingApi.Service;

import com.example.ScallingApi.Entity.User;
import com.example.ScallingApi.Mapper.CursorResponse;
import com.example.ScallingApi.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CursorResponse<User> getAllUsers(Long cursor, int limit) {

        List<User> users = (cursor == null)
                ? userRepository.findFirstPage(limit)
                : userRepository.findNextPage(cursor, limit);

        Long nextCursor = users.isEmpty()
                ? null
                : users.get(users.size() - 1).getId();

        return new CursorResponse<>(users, nextCursor);
    }
}
