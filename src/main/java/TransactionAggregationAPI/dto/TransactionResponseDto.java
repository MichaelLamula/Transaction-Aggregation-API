package TransactionAggregationAPI.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Value
@Builder
public class TransactionResponseDto {
    private String transactionReference; // A public-facing ID, possibly derived/hashed from the internal ID
    private Long customerId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String category; // The key categorized field
    private Instant transactionDate;
    private String sourceSystem;
}
