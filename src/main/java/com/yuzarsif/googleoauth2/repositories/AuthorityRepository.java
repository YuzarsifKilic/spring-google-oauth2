package com.yuzarsif.googleoauth2.repositories;

import com.yuzarsif.googleoauth2.models.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
}
