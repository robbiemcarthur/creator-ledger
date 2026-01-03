/**
 * Expense module - manages business expenses and deductions.
 * <p>
 * This module provides expense recording and categorization capabilities
 * following HMRC guidelines for self-employed creatives.
 * </p>
 * <p>
 * <strong>Public API:</strong> The {@code api} package contains types that
 * other modules can safely depend on, such as {@code ExpenseCategory}.
 * </p>
 */
@org.springframework.modulith.ApplicationModule(
    displayName = "Expense",
    allowedDependencies = {"common"}
)
package org.creatorledger.expense;
