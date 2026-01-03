/**
 * User module - user registration and management.
 * <p>
 * This module handles user identity and provides the foundation
 * for multi-tenancy across all other modules.
 * </p>
 * <p>
 * <strong>Dependencies:</strong>
 * </p>
 * <ul>
 *   <li>{@code common} - shared kernel (UserId)</li>
 * </ul>
 */
@org.springframework.modulith.ApplicationModule(
    displayName = "User",
    allowedDependencies = {"common"}
)
package org.creatorledger.user;
