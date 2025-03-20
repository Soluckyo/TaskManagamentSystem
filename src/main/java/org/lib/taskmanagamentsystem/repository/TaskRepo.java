package org.lib.taskmanagamentsystem.repository;

import org.lib.taskmanagamentsystem.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {
    Page<Task> findByUserId(Long user, Pageable pageable);
}
