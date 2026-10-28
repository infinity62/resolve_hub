package com.jsp.resolve_hub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Request body of POST /api/auth/register. Validated by @Valid in the controller. */
@Data
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    private String phone;   // optional

    // 72 is BCrypt's limit: it only uses the first 72 bytes of a password.
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 72, message = "Password must be 6 to 72 characters")
    private String password;
}