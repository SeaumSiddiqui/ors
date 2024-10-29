package com.rms.ors.user.repository;

import com.rms.ors.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u.id FROM User u WHERE LOWER(u.name) = LOWER(:name)")
    Long findFirstIdByNameIgnoreCase(String name);
}
