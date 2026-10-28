package com.jsp.resolve_hub.service;

import com.jsp.resolve_hub.dao.UserRepository;
import com.jsp.resolve_hub.dto.UpdateUserRequest;
import com.jsp.resolve_hub.dto.UserResponse;
import com.jsp.resolve_hub.entity.RoleName;
import com.jsp.resolve_hub.entity.User;
import com.jsp.resolve_hub.exception.SelfModificationException;
import com.jsp.resolve_hub.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Admin-side user management: list, view, edit, activate/deactivate, change role.
 * Called by UserAdminController. Business rules live here, not in the controller.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /** readOnly = true tells the database no changes will be made (faster, safer). */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::new)     // entity -> safe response (no password)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(int id) {
        return new UserResponse(findUserOrThrow(id));
    }

    /** Edits name and phone. Allowed on your own account too. */
    @Transactional
    public UserResponse updateUser(int id, UpdateUserRequest request) {
        User user = findUserOrThrow(id);
        user.setName(request.name());
        user.setPhone(request.phone());
        return new UserResponse(userRepository.save(user));
    }

    /** Activates (true) or deactivates (false) an account. */
    @Transactional
    public UserResponse updateStatus(int id, boolean status) {
        preventSelfChange(id);
        User user = findUserOrThrow(id);
        user.setAccount_status(status);
        return new UserResponse(userRepository.save(user));
    }

    /** Changes a user's role. */
    @Transactional
    public UserResponse updateRole(int id, RoleName role) {
        preventSelfChange(id);
        User user = findUserOrThrow(id);
        user.setRoles(role);
        return new UserResponse(userRepository.save(user));
    }

    /** One place for "find or throw 404", reused by every method above. */
    private User findUserOrThrow(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    /**
     * Business rule: an admin cannot deactivate or demote their own account.
     * JwtFilter stores the full User as the principal, so we compare its id
     * with the id in the URL.
     */
    private void preventSelfChange(int id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User current && current.getId() == id) {
            throw new SelfModificationException("You cannot change your own status or role");
        }
    }
}