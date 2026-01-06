package org.creatorledger.reporting.infrastructure.web;

import org.creatorledger.common.Money;

public record MoneyDto(
    String amount,
    String currency
) {

    public static MoneyDto from(final Money money) {
        if (money == null) {
            throw new IllegalArgumentException("Money cannot be null");
        }
        return new MoneyDto(
            money.amount().toString(),
            money.currency()
        );
    }
}
