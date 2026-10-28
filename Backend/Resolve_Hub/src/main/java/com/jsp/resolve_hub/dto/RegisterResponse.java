package com.jsp.resolve_hub.dto;

import com.jsp.resolve_hub.entity.RoleName;
import com.jsp.resolve_hub.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

/** Response of POST /api/auth/register. Contains no password. */
@Data
public class RegisterResponse {
    private int id;
    private String name;
    private String email;
    private String phone;
    private RoleName roles;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    /** Copies only the safe fields from the entity. */
    public RegisterResponse(User user){
        id = user.getId();
        name= user.getName();
        email= user.getEmail();
        phone= user.getPhone();
        roles = user.getRoles();
        created_at= user.getCreated_at();
        updated_at= user.getUpdated_at();
    }
}