package com.coderiders.AggregateService.models;

import com.coderiders.AggregateService.exceptions.AggregateException;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@ToString
public class UserContext {
    private static final ThreadLocal<UserContext> userContextThreadLocal = new ThreadLocal<>();

    private final String clerkId;
    private final List<String> roles;
    private final String firstname;
    private final String lastname;
    private final String username;
    private final Map<String, String> userOrganizations;

    private UserContext(String userId, List<String> roles, String firstname, String lastname, String username, Map<String, String> userOrganizations) {
        this.clerkId = userId;
        this.roles = roles;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.userOrganizations = userOrganizations;

    }

    public static void create(String userId, List<String> roles, String firstname, String lastname, String username, Map<String, String> userOrganizations) {
        UserContext userContext = new UserContext(userId, roles, firstname, lastname, username, userOrganizations);
        setCurrentUserContext(userContext);
    }

    public static UserContext getCurrentUserContext() {
        UserContext userContext = userContextThreadLocal.get();
        if (userContext == null) {
            throw new AggregateException("User context is not available for the current request.");
        }
        return userContext;
    }

    public static void setCurrentUserContext(UserContext userContext) {
        userContextThreadLocal.set(userContext);
    }

    public static void clearCurrentUserContext() {
        userContextThreadLocal.remove();
    }
}