/**
 * Common (Shared Kernel) - domain primitives and value objects shared across all modules.
 * <p>
 * This module contains:
 * </p>
 * <ul>
 *   <li>{@code Money} - monetary value object used across all financial domains</li>
 *   <li>{@code UserId}, {@code EventId}, {@code IncomeId}, {@code ExpenseId}, {@code TaxYearSummaryId}
 *       - module identifiers for cross-module references</li>
 * </ul>
 * <p>
 * <strong>Design Principle:</strong> This is a Shared Kernel in DDD terms. All types here
 * are intentionally exposed to all modules. Keep this module minimal - only add types
 * that truly need to be shared across multiple bounded contexts.
 * </p>
 */
@org.springframework.modulith.ApplicationModule(
    displayName = "Common (Shared Kernel)",
    allowedDependencies = {}
)
package org.creatorledger.common;
