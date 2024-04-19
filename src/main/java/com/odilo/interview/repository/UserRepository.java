package com.odilo.interview.repository;

import com.odilo.interview.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByUsernameOrEmail(String username, String email);

}