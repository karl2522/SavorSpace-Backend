package com.example.usersavorspace.repositories;

import com.example.usersavorspace.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByEmailAndDeletedFalse(String email);
    List<User> findByActiveFalse();
    List<User> findByActiveIsTrueAndRoleAndDeletedFalse(String role);
    List<User> findByDeletedTrueAndRole(String role);

    Optional<User> findByIdAndDeletedTrue(Integer id);

    Optional<User> findByIdAndDeletedFalse(Integer id);

    default List<User> findAllActiveUsers() {
        return findByActiveIsTrueAndRoleAndDeletedFalse("USER");
    }

    default List<User> findAllDeletedUsers() {
        return findByDeletedTrueAndRole("USER");
    }
}