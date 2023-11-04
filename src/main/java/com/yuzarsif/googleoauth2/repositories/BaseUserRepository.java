package com.yuzarsif.googleoauth2.repositories;

import com.yuzarsif.googleoauth2.models.BaseUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BaseUserRepository extends JpaRepository<BaseUser, String> {

    Optional<BaseUser> findByEmail(String email);
}
