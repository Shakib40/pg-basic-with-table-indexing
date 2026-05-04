package PG_CRUD.controller;

import PG_CRUD.entity.User;
import PG_CRUD.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users-basic")
@Tag(name = "Users Basic", description = "Basic user operations")
public class UserBasicController {

    @Autowired
    private UserService userService;

    @GetMapping("/get")
    @Operation(summary = "Get all users", description = "Retrieve all users (basic operation)")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers(null).getContent();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID of the user to retrieve", required = true)
            @PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
}
