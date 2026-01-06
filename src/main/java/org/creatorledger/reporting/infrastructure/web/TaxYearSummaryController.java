package org.creatorledger.reporting.infrastructure.web;

import org.creatorledger.reporting.api.TaxYearSummaryId;
import org.creatorledger.reporting.application.GenerateTaxYearSummaryCommand;
import org.creatorledger.reporting.application.TaxYearSummaryApplicationService;
import org.creatorledger.reporting.domain.TaxYear;
import org.creatorledger.user.api.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/tax-year-summaries")
public class TaxYearSummaryController {

    private final TaxYearSummaryApplicationService taxYearSummaryApplicationService;

    public TaxYearSummaryController(final TaxYearSummaryApplicationService taxYearSummaryApplicationService) {
        this.taxYearSummaryApplicationService = taxYearSummaryApplicationService;
    }

    @PostMapping
    public ResponseEntity<Void> generate(@RequestBody final GenerateTaxYearSummaryRequest request) {
        try {
            final GenerateTaxYearSummaryCommand command = new GenerateTaxYearSummaryCommand(
                    UserId.of(request.userId()),
                    TaxYear.of(request.taxYear())
            );
            final TaxYearSummaryId summaryId = taxYearSummaryApplicationService.generate(command);

            final URI location = URI.create("/api/tax-year-summaries/" + summaryId.value());
            return ResponseEntity.created(location).build();
        } catch (final IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaxYearSummaryResponse> getSummary(@PathVariable final String id) {
        try {
            final UUID uuid = UUID.fromString(id);
            final TaxYearSummaryId summaryId = TaxYearSummaryId.of(uuid);

            return taxYearSummaryApplicationService.findById(summaryId)
                    .map(TaxYearSummaryResponse::from)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (final IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
