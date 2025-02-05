/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.client.security;

import org.elasticsearch.client.Validatable;
import org.elasticsearch.client.ValidationException;
import org.elasticsearch.client.security.user.User;
import org.elasticsearch.core.CharArrays;
import org.elasticsearch.core.Nullable;
import org.elasticsearch.xcontent.ToXContentObject;
import org.elasticsearch.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Request object to create or update a user in the native realm.
 */
public final class PutUserRequest implements Validatable, ToXContentObject {

    private final User user;
    private final @Nullable char[] password;
    private final @Nullable char[] passwordHash;
    private final boolean enabled;
    private final RefreshPolicy refreshPolicy;

    /**
     * Create or update a user in the native realm, with the user's new or updated password specified in plaintext.
     * @param user the user to be created or updated
     * @param password the password of the user. The password array is not modified by this class.
     *                 It is the responsibility of the caller to clear the password after receiving
     *                 a response.
     * @param enabled true if the user is enabled and allowed to access elasticsearch
     * @param refreshPolicy the refresh policy for the request.
     */
    public static PutUserRequest withPassword(User user, char[] password, boolean enabled, RefreshPolicy refreshPolicy) {
        return new PutUserRequest(user, password, null, enabled, refreshPolicy);
    }

    /**
     * Create or update a user in the native realm, with the user's new or updated password specified as a cryptographic hash.
     * @param user the user to be created or updated
     * @param passwordHash the hash of the password of the user. It must be in the correct format for the password hashing algorithm in
     *                     use on this elasticsearch cluster. The array is not modified by this class.
     *                     It is the responsibility of the caller to clear the hash after receiving a response.
     * @param enabled true if the user is enabled and allowed to access elasticsearch
     * @param refreshPolicy the refresh policy for the request.
     */
    public static PutUserRequest withPasswordHash(User user, char[] passwordHash, boolean enabled, RefreshPolicy refreshPolicy) {
        return new PutUserRequest(user, null, passwordHash, enabled, refreshPolicy);
    }

    /**
     * Update an existing user in the native realm without modifying their password.
     * @param user the user to be created or updated
     * @param enabled true if the user is enabled and allowed to access elasticsearch
     * @param refreshPolicy the refresh policy for the request.
     */
    public static PutUserRequest updateUser(User user, boolean enabled, RefreshPolicy refreshPolicy) {
        return new PutUserRequest(user, null, null, enabled, refreshPolicy);
    }

    /**
     * Creates a new request that is used to create or update a user in the native realm.
     *
     * @param user the user to be created or updated
     * @param password the password of the user. The password array is not modified by this class.
     *                 It is the responsibility of the caller to clear the password after receiving
     *                 a response.
     * @param enabled true if the user is enabled and allowed to access elasticsearch
     * @param refreshPolicy the refresh policy for the request.
     * @deprecated Use {@link #withPassword(User, char[], boolean, RefreshPolicy)} or
     *             {@link #updateUser(User, boolean, RefreshPolicy)} instead.
     */
    @Deprecated
    public PutUserRequest(User user, @Nullable char[] password, boolean enabled, @Nullable RefreshPolicy refreshPolicy) {
        this(user, password, null, enabled, refreshPolicy);
    }

    /**
     * Creates a new request that is used to create or update a user in the native realm.
     * @param user the user to be created or updated
     * @param password the password of the user. The password array is not modified by this class.
     *                 It is the responsibility of the caller to clear the password after receiving
     *                 a response.
     * @param passwordHash the hash of the password. Only one of "password" or "passwordHash" may be populated.
     *                     The other parameter must be {@code null}.
     * @param enabled true if the user is enabled and allowed to access elasticsearch
     * @param refreshPolicy the refresh policy for the request.
     */
    private PutUserRequest(User user, @Nullable char[] password, @Nullable char[] passwordHash, boolean enabled,
                           RefreshPolicy refreshPolicy) {
        this.user = Objects.requireNonNull(user, "user is required, cannot be null");
        if (password != null && passwordHash != null) {
            throw new IllegalArgumentException("cannot specify both password and passwordHash");
        }
        this.password = password;
        this.passwordHash = passwordHash;
        this.enabled = enabled;
        this.refreshPolicy = refreshPolicy == null ? RefreshPolicy.getDefault() : refreshPolicy;
    }

    public User getUser() {
        return user;
    }

    public @Nullable char[] getPassword() {
        return password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public RefreshPolicy getRefreshPolicy() {
        return refreshPolicy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PutUserRequest that = (PutUserRequest) o;
        return Objects.equals(user, that.user)
                && Arrays.equals(password, that.password)
                && Arrays.equals(passwordHash, that.passwordHash)
                && enabled == that.enabled
                && refreshPolicy == that.refreshPolicy;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(user, enabled, refreshPolicy);
        result = 31 * result + Arrays.hashCode(password);
        result = 31 * result + Arrays.hashCode(passwordHash);
        return result;
    }

    @Override
    public Optional<ValidationException> validate() {
        if (user.getMetadata() != null && user.getMetadata().keySet().stream().anyMatch(s -> s.startsWith("_"))) {
            ValidationException validationException = new ValidationException();
            validationException.addValidationError("user metadata keys may not start with [_]");
            return Optional.of(validationException);
        }
        return Optional.empty();
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        builder.field("username", user.getUsername());
        if (password != null) {
            charField(builder, "password", password);
        }
        if (passwordHash != null) {
            charField(builder, "password_hash", passwordHash);
        }
        builder.field("roles", user.getRoles());
        if (user.getFullName() != null) {
            builder.field("full_name", user.getFullName());
        }
        if (user.getEmail() != null) {
            builder.field("email", user.getEmail());
        }
        builder.field("metadata", user.getMetadata());
        builder.field("enabled", enabled);
        return builder.endObject();
    }

    private void charField(XContentBuilder builder, String fieldName, char[] chars) throws IOException {
        byte[] charBytes = CharArrays.toUtf8Bytes(chars);
        try {
            builder.field(fieldName).utf8Value(charBytes, 0, charBytes.length);
        } finally {
            Arrays.fill(charBytes, (byte) 0);
        }
    }
}
