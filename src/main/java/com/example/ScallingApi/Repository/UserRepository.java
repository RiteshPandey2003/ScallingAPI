package com.example.ScallingApi.Repository;

import com.example.ScallingApi.Entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u ORDER BY u.id ASC")
    List<User> findFirstPage(Pageable pageable);

    default List<User> findFirstPage(int limit) {
        return findFirstPage(PageRequest.of(0, limit));
    }

    @Query("SELECT u FROM User u WHERE u.id > :cursor ORDER BY u.id ASC")
    List<User> findNextPage(@Param("cursor") Long cursor, Pageable pageable);

    default List<User> findNextPage(Long cursor, int limit) {
        return findNextPage(cursor, PageRequest.of(0, limit));
    }
}
