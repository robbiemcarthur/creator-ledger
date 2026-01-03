/**
 * Reporting module - tax year summaries and financial reporting.
 * <p>
 * This module aggregates income and expense data to generate
 * UK tax year summaries for self-employed creatives.
 * </p>
 * <p>
 * <strong>Dependencies:</strong>
 * </p>
 * <ul>
 *   <li>{@code common} - shared kernel (Money, IDs)</li>
 *   <li>{@code expense::api} - ExpenseCategory for categorization</li>
 * </ul>
 */
@org.springframework.modulith.ApplicationModule(
    displayName = "Reporting",
    allowedDependencies = {"common", "expense :: api"}
)
package org.creatorledger.reporting;
