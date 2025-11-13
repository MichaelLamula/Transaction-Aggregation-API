package TransactionAggregationAPI.client;

import TransactionAggregationAPI.dto.MockTransactionDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Component
public class MockSourceAClient {

    public List<MockTransactionDto> fetchTransactions() {
        return List.of(
                MockTransactionDto.builder()
                        .transactionId("A1001").accountHolderId(1L)
                        .value(new BigDecimal("50.25")).currencyCode("USD")
                        .descriptionText("Payment to Amazon.com for electronics").processedAt(Instant.now().minusSeconds(86400))
                        .build(),
                MockTransactionDto.builder()
                        .transactionId("A1002").accountHolderId(1L)
                        .value(new BigDecimal("500.99")).currencyCode("R")
                        .descriptionText("Starbucks Coffee purchase").processedAt(Instant.now().minusSeconds(3600))
                        .build()
        );
    }
}
