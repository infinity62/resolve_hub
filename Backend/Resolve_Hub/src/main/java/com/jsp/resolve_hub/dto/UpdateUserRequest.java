package com.jsp.resolve_hub.dto;

import jakarta.validation.constraints.NotBlank;

/** Body of PUT /api/users/{id}: the admin edits a user's name and phone. */
public record UpdateUserRequest(
        @NotBlank(message = "Name is required") String name,
        String phone
) {}