package org.creatorledger.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value object representing a monetary amount with currency.
 * Uses BigDecimal for precision in financial calculations.
 */
public record Money(BigDecimal amount, String currency) {

    private static final int DECIMAL_PLACES = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_DOWN;

    /**
     * Creates Money in GBP from a BigDecimal amount.
     *
     * @param amount the monetary amount
     * @return a new Money in GBP
     * @throws IllegalArgumentException if amount is invalid
     */
    public static Money gbp(BigDecimal amount) {
        return of(amount, "GBP");
    }

    /**
     * Creates Money in GBP from a string amount.
     *
     * @param amount the monetary amount as string
     * @return a new Money in GBP
     * @throws IllegalArgumentException if amount is invalid
     */
    public static Money gbp(String amount) {
        return gbp(new BigDecimal(amount));
    }

    /**
     * Creates Money with specified amount and currency.
     *
     * @param amount the monetary amount
     * @param currency the currency code
     * @return a new Money
     * @throws IllegalArgumentException if amount or currency is invalid
     */
    public static Money of(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Money amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be null or blank");
        }

        // Scale to 2 decimal places for currency precision
        BigDecimal scaled = amount.setScale(DECIMAL_PLACES, ROUNDING_MODE);
        return new Money(scaled, currency.toUpperCase());
    }

    /**
     * Adds another Money amount to this one.
     *
     * @param other the other Money
     * @return a new Money with the sum
     * @throws IllegalArgumentException if currencies don't match
     */
    public Money add(Money other) {
        ensureSameCurrency(other);
        return new Money(
            amount.add(other.amount).setScale(DECIMAL_PLACES, ROUNDING_MODE),
            currency
        );
    }

    /**
     * Subtracts another Money amount from this one.
     *
     * @param other the other Money
     * @return a new Money with the difference
     * @throws IllegalArgumentException if currencies don't match or result is negative
     */
    public Money subtract(Money other) {
        ensureSameCurrency(other);
        BigDecimal result = amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cannot subtract to negative amount");
        }
        return new Money(result.setScale(DECIMAL_PLACES, ROUNDING_MODE), currency);
    }

    /**
     * Multiplies this Money by a factor.
     *
     * @param factor the multiplication factor
     * @return a new Money with the product
     */
    public Money multiply(BigDecimal factor) {
        return new Money(
            amount.multiply(factor).setScale(DECIMAL_PLACES, ROUNDING_MODE),
            currency
        );
    }

    /**
     * Checks if this Money is less than another.
     *
     * @param other the other Money
     * @return true if this is less than other
     * @throws IllegalArgumentException if currencies don't match
     */
    public boolean isLessThan(Money other) {
        ensureSameCurrency(other);
        return amount.compareTo(other.amount) < 0;
    }

    /**
     * Checks if this Money is greater than another.
     *
     * @param other the other Money
     * @return true if this is greater than other
     * @throws IllegalArgumentException if currencies don't match
     */
    public boolean isGreaterThan(Money other) {
        ensureSameCurrency(other);
        return amount.compareTo(other.amount) > 0;
    }

    /**
     * Checks if this Money is zero.
     *
     * @return true if amount is zero
     */
    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Checks if this Money is positive (greater than zero).
     *
     * @return true if amount is positive
     */
    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    private void ensureSameCurrency(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Cannot perform operation on different currencies: " + currency + " and " + other.currency
            );
        }
    }

    @Override
    public String toString() {
        return "Money[" + currency + " " + amount + "]";
    }
}
