package TransactionAggregationAPI.service;

import TransactionAggregationAPI.client.MockSourceAClient;
import TransactionAggregationAPI.dto.MockTransactionDto;
import TransactionAggregationAPI.dto.TransactionResponseDto;
import TransactionAggregationAPI.model.Transaction;
import TransactionAggregationAPI.repository.TransactionRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final MockSourceAClient mockSourceAClient;

    public TransactionService(TransactionRepository transactionRepository, MockSourceAClient mockSourceAClient) {
        this.transactionRepository = transactionRepository;
        this.mockSourceAClient = mockSourceAClient;
    }

    @PostConstruct
    public void initialAggregation() {

        log.info("Starting initial aggregation from mock data sources.");

        List<MockTransactionDto> sourceAData = mockSourceAClient.fetchTransactions();

        List<Transaction> aggregatedTransactions = Stream.of(sourceAData) // Add other source data here
                .flatMap(List::stream)
                .map(this::mapAndCategorize)
                .toList();

        transactionRepository.saveAll(aggregatedTransactions);
        log.info("Successfully aggregated and saved {} transactions.", aggregatedTransactions.size());
    }

    /**
     * Maps external DTO to internal Entity and assigns a category.
     */
    private Transaction mapAndCategorize(MockTransactionDto dto) {
        Transaction transaction = new Transaction();
        transaction.setCustomerId(dto.getAccountHolderId());
        transaction.setAmount(dto.getValue());
        transaction.setCurrency(dto.getCurrencyCode());
        transaction.setDescription(dto.getDescriptionText());
        transaction.setTransactionDate(dto.getProcessedAt());
        transaction.setSource("MockSourceA");

        transaction.setCategory(determineCategory(dto.getDescriptionText()));

        return transaction;
    }

    /**
     * Simple keyword-based categorization logic.
     */
    private String determineCategory(String description) {
        String lowerDesc = description.toLowerCase();

        if (lowerDesc.contains("amazon") || lowerDesc.contains("target")) {
            return "Retail/Shopping";
        } else if (lowerDesc.contains("starbucks") || lowerDesc.contains("coffee") || lowerDesc.contains("restaurant")) {
            return "Food & Drink";
        } else if (lowerDesc.contains("uber") || lowerDesc.contains("airline")) {
            return "Travel";
        } else {
            return "Uncategorized";
        }
    }


    public Page<TransactionResponseDto> getTransactions(Long customerId, String category, Pageable pageable) {
        Page<Transaction> transactionPage;

        if (customerId != null && category != null) {
            transactionPage = transactionRepository.findAllByCustomerIdAndCategory(customerId, category, pageable);
        } else if (customerId != null) {
            transactionPage = transactionRepository.findAllByCustomerId(customerId, pageable);
        } else {
            transactionPage = transactionRepository.findAll(pageable);
        }

        return transactionPage.map(this::toResponseDto);
    }

    /**
     * Helper method to map the Transaction Entity to the TransactionResponseDto.
     */
    private TransactionResponseDto toResponseDto(Transaction transaction) {
        return TransactionResponseDto.builder()
                .transactionReference(String.valueOf(transaction.getId())) // Using ID as reference for simplicity
                .customerId(transaction.getCustomerId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .description(transaction.getDescription())
                .category(transaction.getCategory())
                .transactionDate(transaction.getTransactionDate())
                .sourceSystem(transaction.getSource())
                .build();
    }

    public Map<String, Double> getCategoryTotals(Long customerId) {

        return transactionRepository.findTotalAmountPerCategoryForCustomer(customerId).stream()
                .collect(
                        java.util.stream.Collectors.toMap(
                                arr -> (String) arr[0], // Category name
                                arr -> ((java.math.BigDecimal) arr[1]).doubleValue() // Total amount
                        )
                );
    }

}
