package com.jsp.resolve_hub.dto;

import com.jsp.resolve_hub.entity.RoleName;
import com.jsp.resolve_hub.entity.User;
import lombok.*;

/**
 * Safe view of a user returned by /api/auth/me and the /api/users endpoints.
 * It never includes the password.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private int id;
    private String name;
    private String email;
    private String phone;
    private boolean account_status;
    private RoleName roles;

    /** Convert an entity into a response: new UserResponse(user). */
    public UserResponse(User user) {
        this.id= user.getId();
        this.account_status = user.isAccount_status();
        this.email=user.getEmail();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.roles = user.getRoles();
    }
}