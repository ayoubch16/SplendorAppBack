package org.example.back.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.back.domain.User;
import org.example.back.repository.UserRepository;
import org.example.back.service.dto.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Transactional
@Slf4j
@AllArgsConstructor
public class UserResource {

    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getPassword() != null) {
            try {
                // Hacher le nouveau mot de passe avec MD5 avant de le sauvegarder
                String hashedPassword = hashMD5(user.getPassword());
                existingUser.setPassword(hashedPassword);
            } catch (NoSuchAlgorithmException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing password");
            }
        }
        if (user.getFirstName() != null) {
            existingUser.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            existingUser.setLastName(user.getLastName());
        }
        existingUser.setActive(user.isActive());
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }

        return ResponseEntity.ok(userRepository.save(existingUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userRepository.findById(id).orElse(null));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }

//    @PostMapping("/login")
//    public ResponseEntity<Long> login(@RequestBody LoginRequest loginRequest) {
//
//        User user = userRepository.findByEmail(loginRequest.getEmail())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//
//        if (!user.getPassword().equals(loginRequest.getPassword())) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
//        }
//
//        return ResponseEntity.ok(user.getId());
//    }

    @PostMapping("/login")
    public ResponseEntity<Long> login(@RequestBody LoginRequest loginRequest) {
        // Trouver l'utilisateur par email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Hacher le mot de passe reçu avec MD5
        String hashedInputPassword;
        try {
            hashedInputPassword = hashMD5(loginRequest.getPassword());
        } catch (NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing password");
        }

        // Comparer avec le mot de passe haché dans la base de données
        if (!user.getPassword().equals(hashedInputPassword)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        return ResponseEntity.ok(user.getId());
    }

    private String hashMD5(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();

        // Convertir le byte array en format hexadécimal
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }

        return sb.toString();
    }
}