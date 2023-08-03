package com.limethecoder.data.repository;

import com.limethecoder.data.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {
    @Query(value = "select login from User", nativeQuery = true)
    List<String> getAllLogins();
}
