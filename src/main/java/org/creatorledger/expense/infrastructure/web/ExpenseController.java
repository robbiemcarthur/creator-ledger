package org.creatorledger.expense.infrastructure.web;

import org.creatorledger.expense.api.ExpenseId;
import org.creatorledger.expense.application.ExpenseApplicationService;
import org.creatorledger.expense.application.RecordExpenseCommand;
import org.creatorledger.expense.application.UpdateExpenseCommand;
import org.creatorledger.user.api.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseApplicationService expenseApplicationService;

    public ExpenseController(final ExpenseApplicationService expenseApplicationService) {
        this.expenseApplicationService = expenseApplicationService;
    }

    @PostMapping
    public ResponseEntity<Void> record(@RequestBody final RecordExpenseRequest request) {
        try {
            final RecordExpenseCommand command = new RecordExpenseCommand(
                    UserId.of(request.userId()),
                    request.amount(),
                    request.currency(),
                    request.category(),
                    request.description(),
                    request.incurredDate()
            );
            final ExpenseId expenseId = expenseApplicationService.record(command);

            final URI location = URI.create("/api/expenses/" + expenseId.value());
            return ResponseEntity.created(location).build();
        } catch (final IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable final String id, @RequestBody final UpdateExpenseRequest request) {
        try {
            final UUID uuid = UUID.fromString(id);
            final ExpenseId expenseId = ExpenseId.of(uuid);

            final UpdateExpenseCommand command = new UpdateExpenseCommand(
                    expenseId,
                    request.amount(),
                    request.currency(),
                    request.category(),
                    request.description(),
                    request.incurredDate()
            );
            expenseApplicationService.update(command);

            return ResponseEntity.noContent().build();
        } catch (final IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getExpense(@PathVariable final String id) {
        try {
            final UUID uuid = UUID.fromString(id);
            final ExpenseId expenseId = ExpenseId.of(uuid);

            return expenseApplicationService.findById(expenseId)
                    .map(ExpenseResponse::from)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (final IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> exists(@PathVariable final String id) {
        try {
            final UUID uuid = UUID.fromString(id);
            final ExpenseId expenseId = ExpenseId.of(uuid);

            final boolean exists = expenseApplicationService.existsById(expenseId);
            return ResponseEntity.ok(exists);
        } catch (final IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
