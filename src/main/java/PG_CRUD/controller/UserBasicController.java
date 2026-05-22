package PG_CRUD.controller;

import PG_CRUD.entity.User;
import PG_CRUD.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
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
            @Parameter(description = "ID of the user to retrieve", required = true) @PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new user", description = "Create a new user in the system")
    public ResponseEntity<User> createUser(
            @RequestBody User user, 
            HttpServletRequest request) {
        try {
            User createdUser = userService.createUser(user, request);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update an existing user", description = "Update user details by ID")
    public ResponseEntity<User> updateUser(
            @Parameter(description = "ID of the user to update", required = true) @PathVariable Long id,
            @RequestBody User userDetails,
            HttpServletRequest request) {
        try {
            User updatedUser = userService.updateUser(id, userDetails, request);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete a user", description = "Delete a user by their ID")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        try {
            userService.deleteUser(id, request);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
