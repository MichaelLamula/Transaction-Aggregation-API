package TransactionAggregationAPI.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Value
@Builder
public class TransactionResponseDto {
    private String transactionReference;
    private Long customerId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String category;
    private Instant transactionDate;
    private String sourceSystem;
}
