package com.example.ScallingApi.Service;

import com.example.ScallingApi.Entity.User;
import com.example.ScallingApi.Mapper.UserMapper;
import com.example.ScallingApi.Repository.UserRepository;
import com.example.ScallingApi.util.PaginationRequest;
import com.example.ScallingApi.util.PaginationUtils;
import com.example.ScallingApi.util.PagingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Cacheable(
            cacheResolver = "pageBasedCacheResolver",
            value = "users",
            key = "T(String).valueOf(#request.page) + '-' + #request.size + '-' + #request.sortField + '-' + #request.direction.name()"
    )
    public PagingResult<User> getAllUsers(PaginationRequest request) {
        Pageable pageable = PaginationUtils.getPageable(request);
        Page<User> entities = userRepository.findAll(pageable);

        List<User> dtoList = entities.stream()
                .map(mapper::convertToDto)
                .toList();

        return new PagingResult<>(
                dtoList,
                entities.getTotalPages(),
                entities.getTotalElements(),
                entities.getSize(),
                entities.getNumber(),
                entities.isEmpty()
        );
    }
}
