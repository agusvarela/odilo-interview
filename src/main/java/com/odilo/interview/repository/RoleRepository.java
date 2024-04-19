package com.odilo.interview.repository;

import com.odilo.interview.model.Role;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@CacheConfig(cacheNames = "roles")
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Cacheable
    Optional<Role> findByName(String name);

}