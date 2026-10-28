package com.jsp.resolve_hub.dto;

import lombok.Builder;
import lombok.Data;

/** Response of a successful login: the JWT and how long it stays valid. */
@Data
@Builder
public class AuthResponse {

    private String token;       // sent back by the client as "Authorization: Bearer <token>"
    private String expiresIn;   // lifetime in seconds, e.g. "3600"
}