package org.lib.taskmanagamentsystem.repository;

import org.lib.taskmanagamentsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> getByEmail(String email);

    Optional <User> findByEmail(String email);
}
