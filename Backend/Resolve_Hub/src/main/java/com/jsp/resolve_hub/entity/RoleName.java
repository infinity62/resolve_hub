package com.jsp.resolve_hub.entity;

/**
 * The three roles of ResolveHub. Stored as text in the "roles" column of the
 * users table and used by Spring Security for role-based authorization.
 */
public enum RoleName {
    EMPLOYEE,   // raises tickets
    AGENT,      // works on assigned tickets
    ADMIN       // manages users, categories and assignments
}