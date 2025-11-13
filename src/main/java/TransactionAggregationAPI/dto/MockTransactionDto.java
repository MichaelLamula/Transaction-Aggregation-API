package TransactionAggregationAPI.dto;

import lombok.Builder;
import lombok.Data;


import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class MockTransactionDto {
    private String transactionId;
    private Long accountHolderId; // Map to customerId
    private BigDecimal value;
    private String descriptionText;
    private String currencyCode;
    private Instant processedAt;
}
