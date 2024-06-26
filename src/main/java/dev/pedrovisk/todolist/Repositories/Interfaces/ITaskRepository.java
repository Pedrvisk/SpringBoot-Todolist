package dev.pedrovisk.todolist.Repositories.Interfaces;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.pedrovisk.todolist.Data.TaskModel;

import java.util.List;
import java.util.UUID;

public interface ITaskRepository extends JpaRepository<TaskModel, UUID> {
    List<TaskModel> findByUserId(UUID userId);
}
