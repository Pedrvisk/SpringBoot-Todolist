package dev.pedrovisk.todolist.Repositories.Interfaces;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.pedrovisk.todolist.Data.UserModel;

import java.util.UUID;

public interface IUserRepository extends JpaRepository<UserModel, UUID> {
    UserModel findByUsername(String username);
}
