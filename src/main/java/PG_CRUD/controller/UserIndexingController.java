package PG_CRUD.controller;

import PG_CRUD.entity.User;
import PG_CRUD.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;

import java.util.Optional;

@RestController
@RequestMapping("/users-with-indexing")
@Tag(name = "Users with Indexing", description = "User management operations with indexing")
public class UserIndexingController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    @Operation(summary = "Create a new user", description = "Create a new user with indexing support")
    public ResponseEntity<User> createUser(
            @Parameter(description = "User object to be created", required = true) @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user, null);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID with optimized indexing")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID of the user to retrieve", required = true) @PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/get")
    @Operation(summary = "Get all users", description = "Retrieve all users with indexing optimization")
    public ResponseEntity<Page<User>> getAllUsers() {
        Page<User> users = userService.getAllUsers(null);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/upload-file")
    @Operation(summary = "Upload file for users", description = "Upload a file containing user data")
    public ResponseEntity<String> uploadFile(
            @Parameter(description = "File to upload", required = true) @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return new ResponseEntity<>("Please select a file to upload", HttpStatus.BAD_REQUEST);
            }

            // Process file upload logic here
            String message = "File uploaded successfully: " + file.getOriginalFilename();
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to upload file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
