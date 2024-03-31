package dev.pedrovisk.todolist.Controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pedrovisk.todolist.Data.TaskModel;
import dev.pedrovisk.todolist.Lib.Properties;
import dev.pedrovisk.todolist.Repositories.Interfaces.ITaskRepository;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    // GET - http://localhost:8080/tasks
    @GetMapping
    public ResponseEntity<List<TaskModel>> findAllTasks(HttpServletRequest request) {
        var userId = request.getAttribute("userId");
        var tasks = this.taskRepository.findByUserId((UUID) userId);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    // PUT - http://localhost:8080/tasks/:id
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable UUID id,
            @RequestBody TaskModel taskModel,
            HttpServletRequest request) {
        var userId = request.getAttribute("userId");
        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no such task");
        }

        if (!task.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to change this task");
        }

        Properties.copyNonNullProperties(taskModel, task);
        this.taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    // POST - http://localhost:8080/tasks
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskModel taskModel, HttpServletRequest request) {

        var userId = request.getAttribute("userId");
        taskModel.setUserId((UUID) userId);

        var currentDate = LocalDateTime.now();
        if (currentDate.isAfter(taskModel.getStartAt())
                || currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("The start date or end date must be greater than the current date");
        }

        var endDate = taskModel.getEndAt();
        if (endDate.isBefore(taskModel.getStartAt())
                || endDate.isEqual(taskModel.getStartAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("The end date ends before or at the same time as the start date");
        }

        var taskCreated = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskCreated);
    }
}
