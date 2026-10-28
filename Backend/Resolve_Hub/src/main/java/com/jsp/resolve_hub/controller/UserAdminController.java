package com.jsp.resolve_hub.controller;

import com.jsp.resolve_hub.dto.UpdateRoleRequest;
import com.jsp.resolve_hub.dto.UpdateUserRequest;
import com.jsp.resolve_hub.dto.UserResponse;
import com.jsp.resolve_hub.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User management for admins. The class-level @PreAuthorize protects every
 * method: any non-admin gets 403 ACCESS_DENIED.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

    // "final" is required so @RequiredArgsConstructor injects it.
    private final UserService userService;

    /** GET /api/users */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /** GET /api/users/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable int id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /** PUT /api/users/{id}  body: { "name": "...", "phone": "..." } */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable int id,
                                                   @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    /** PUT /api/users/{id}/status?status=true|false  (no request body) */
    @PutMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateAccountStatus(@PathVariable int id,
                                                            @RequestParam boolean status) {
        return ResponseEntity.ok(userService.updateStatus(id, status));
    }

    /** PATCH /api/users/{id}/role  body: { "role": "AGENT" } */
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateRole(@PathVariable int id,
                                                   @Valid @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(userService.updateRole(id, request.role()));
    }
}