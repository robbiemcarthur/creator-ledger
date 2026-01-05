package org.creatorledger.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal amount, String currency) {

    private static final int DECIMAL_PLACES = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_DOWN;

    public static Money gbp(final BigDecimal amount) {
        return of(amount, "GBP");
    }

    public static Money gbp(final String amount) {
        return gbp(new BigDecimal(amount));
    }

    public static Money of(final BigDecimal amount, final String currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Money amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be null or blank");
        }

        final BigDecimal scaled = amount.setScale(DECIMAL_PLACES, ROUNDING_MODE);
        return new Money(scaled, currency.toUpperCase());
    }

    public Money add(final Money other) {
        ensureSameCurrency(other);
        return new Money(
            amount.add(other.amount).setScale(DECIMAL_PLACES, ROUNDING_MODE),
            currency
        );
    }

    public Money subtract(final Money other) {
        ensureSameCurrency(other);
        final BigDecimal result = amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cannot subtract to negative amount");
        }
        return new Money(result.setScale(DECIMAL_PLACES, ROUNDING_MODE), currency);
    }

    public Money multiply(final BigDecimal factor) {
        return new Money(
            amount.multiply(factor).setScale(DECIMAL_PLACES, ROUNDING_MODE),
            currency
        );
    }

    public boolean isLessThan(final Money other) {
        ensureSameCurrency(other);
        return amount.compareTo(other.amount) < 0;
    }

    public boolean isGreaterThan(final Money other) {
        ensureSameCurrency(other);
        return amount.compareTo(other.amount) > 0;
    }

    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    private void ensureSameCurrency(final Money other) {
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
