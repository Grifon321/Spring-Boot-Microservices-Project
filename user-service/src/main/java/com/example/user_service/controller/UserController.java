package com.example.user_service.controller;

import com.example.user_service.model.User;
import com.example.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Object> registerUser(@RequestBody User user) {
        // 400 response if username consists only out of numbers
        if (user.getUsername().matches("\\d+"))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username can not consist of numbers only.");

        // Register the user and 400 responce if username already exists
        User createdUser = userService.registerUser(user);
        if (createdUser == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with the username already exists.");

        // Send 201 responce with user's location
         URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        // Delete the user or send 404 responce if no user with the id
        User user = userService.getUserById(id);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        if (!userService.deleteUserById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        
        // Send 204 responce
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}") // ToDo : Security flaw
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        // Get the user or send 404 responce if no user with the id
        User user = userService.getUserById(id);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");

        // Send 200 responce with user in body
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @RequestBody User newUser) {
        // Update the user or send 404 responce if no user with the id
        User user = userService.getUserById(id);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        user = userService.updateUser(id, newUser);

        // Send 200 responce with user in body
        return ResponseEntity.ok(user);
    }

    @GetMapping("/password/{username}") // ToDo : Security flaw
    public ResponseEntity<String> getPasswordByUser(@PathVariable String username) {
        // Get the user and extract password
        User user = userService.getUserByUsername(username);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        String password = user.getPassword();
        // Send 200 responce with password in body
        return ResponseEntity.ok(password);
    }

    @GetMapping("/id-by-username/{username}")
    public ResponseEntity<Object> getIdByUsername(@PathVariable String username) {
        // Get the user and extract the id
        User user = userService.getUserByUsername(username);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        Long id = user.getId();
        // Send 200 responce with password in body
        return ResponseEntity.ok(id);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() { // ToDo : Security flaw since passwords are included
        // Send 200 responce with a list of users in body
        return ResponseEntity.ok(userService.getAllUsers());
    }
}