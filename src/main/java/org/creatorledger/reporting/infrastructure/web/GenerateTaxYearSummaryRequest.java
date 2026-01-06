package org.creatorledger.reporting.infrastructure.web;

import java.util.UUID;

public record GenerateTaxYearSummaryRequest(
    UUID userId,
    Integer taxYear
) {
}
