package com.jsp.resolve_hub.dto;

import com.jsp.resolve_hub.entity.RoleName;
import jakarta.validation.constraints.NotNull;

/** Body of PATCH /api/users/{id}/role, e.g. { "role": "AGENT" }. */
public record UpdateRoleRequest(
        @NotNull(message = "Role is required") RoleName role
) {}