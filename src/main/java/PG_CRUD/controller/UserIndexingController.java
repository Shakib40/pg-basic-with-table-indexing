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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
            @Parameter(description = "File to upload", required = true)
            @RequestParam("file") MultipartFile file) {

    try {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                .equals(contentType)) {
            return ResponseEntity.badRequest().body("Only .xlsx format is supported");
        }

        // Read Excel file
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            if (sheet.getPhysicalNumberOfRows() == 0) {
                return ResponseEntity.badRequest().body("Excel file is empty");
            }

            // Validate header row
            Row headerRow = sheet.getRow(0);

            List<String> expectedHeaders = Arrays.asList("id", "user", "email");

            for (int i = 0; i < expectedHeaders.size(); i++) {

                Cell cell = headerRow.getCell(i);

                String actualHeader = cell != null
                        ? cell.getStringCellValue().trim()
                        : "";

                if (!expectedHeaders.get(i).equalsIgnoreCase(actualHeader)) {
                    return ResponseEntity.badRequest().body(
                            String.format(
                                    "Invalid column name at position %d. Expected '%s' but found '%s'",
                                    i + 1,
                                    expectedHeaders.get(i),
                                    actualHeader));
                }
            }

            // Process data rows
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {

                Row row = sheet.getRow(rowNum);

                if (row == null) {
                    continue;
                }

                String id = getCellValue(row.getCell(0));
                String user = getCellValue(row.getCell(1));
                String email = getCellValue(row.getCell(2));

                System.out.println("ID: " + id);
                System.out.println("User: " + user);
                System.out.println("Email: " + email);

                // userService.createUser(...)
            }
        }

        return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to upload file: " + e.getMessage());
    }
}

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}
