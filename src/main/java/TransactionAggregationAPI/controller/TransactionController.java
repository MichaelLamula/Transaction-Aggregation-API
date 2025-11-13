package TransactionAggregationAPI.controller;

import TransactionAggregationAPI.dto.TransactionResponseDto;
import TransactionAggregationAPI.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/transactions")
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    /**
     * GET /api/v1/transactions
     * Extensive API for retrieval with filtering, pagination, and sorting.
     * Params: customerId, category, page, size, sort
     */
    @GetMapping
    public ResponseEntity<Page<TransactionResponseDto>> getTransactions(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String category,
            Pageable pageable) {

        Page<TransactionResponseDto> transactions = transactionService.getTransactions(customerId, category, pageable);

        return ResponseEntity.ok(transactions);
    }


    /**
     * GET /api/v1/transactions/customer/1/summary
     * API for retrieving aggregated information (total spend per category).
     */
    @GetMapping("/customer/{customerId}/summary")
    public ResponseEntity<Map<String, Double>> getCustomerSummary(
            @PathVariable Long customerId) {

        Map<String, Double> summary = transactionService.getCategoryTotals(customerId);

        if (summary.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(summary);
    }

}
