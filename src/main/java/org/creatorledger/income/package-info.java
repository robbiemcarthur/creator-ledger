/**
 * Income module - income recording and tracking.
 * <p>
 * This module handles income from various sources including
 * event fees, product sales, and other revenue streams.
 * </p>
 * <p>
 * <strong>Dependencies:</strong>
 * </p>
 * <ul>
 *   <li>{@code common} - shared kernel (IncomeId, EventId, UserId, Money)</li>
 * </ul>
 */
@org.springframework.modulith.ApplicationModule(
    displayName = "Income",
    allowedDependencies = {"common"}
)
package org.creatorledger.income;
