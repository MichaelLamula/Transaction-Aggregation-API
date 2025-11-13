package TransactionAggregationAPI;

import TransactionAggregationAPI.client.MockSourceAClient;
import TransactionAggregationAPI.dto.TransactionResponseDto;
import TransactionAggregationAPI.model.Transaction;
import TransactionAggregationAPI.repository.TransactionRepository;
import TransactionAggregationAPI.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionalServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private MockSourceAClient mockSourceAClient;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction mockTransaction;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        mockTransaction = new Transaction();
        mockTransaction.setId(1L);
        mockTransaction.setCustomerId(101L);
        mockTransaction.setAmount(new BigDecimal("100.00"));
        mockTransaction.setCurrency("USD");
        mockTransaction.setDescription("Test transaction for Travel");
        mockTransaction.setCategory("Travel");
        mockTransaction.setSource("MockSourceA");
        mockTransaction.setTransactionDate(Instant.now());

        pageable = PageRequest.of(0, 10);
    }


    @Test
    void testInitialAggregation_SavesDataAndCategorizes() {

        when(mockSourceAClient.fetchTransactions()).thenReturn(List.of(
                TransactionAggregationAPI.dto.MockTransactionDto.builder()
                        .transactionId("A1").accountHolderId(200L).value(new BigDecimal("12.50"))
                        .currencyCode("USD").descriptionText("Starbucks Coffee").processedAt(Instant.now()).build(),
                TransactionAggregationAPI.dto.MockTransactionDto.builder()
                        .transactionId("A2").accountHolderId(200L).value(new BigDecimal("500.00"))
                        .currencyCode("EUR").descriptionText("New Amazon Order").processedAt(Instant.now()).build()
        ));

        // When the service runs the PostConstruct method
        transactionService.initialAggregation();

        // Verify that saveAll was called exactly once
        verify(transactionRepository, times(1)).saveAll(any(List.class));

    }


    @Test
    void testGetTransactions_NoFilters_ReturnsAllPagedAndMapped() {

        Page<Transaction> mockPage = new PageImpl<>(List.of(mockTransaction), pageable, 1);
        when(transactionRepository.findAll(pageable)).thenReturn(mockPage);

        Page<TransactionResponseDto> result = transactionService.getTransactions(null, null, pageable);

        verify(transactionRepository, times(1)).findAll(pageable);
        verify(transactionRepository, never()).findAllByCustomerId(any(), any());

        assertFalse(result.getContent().isEmpty());
        TransactionResponseDto dto = result.getContent().get(0);
        assertEquals(101L, dto.getCustomerId());
        assertEquals("Travel", dto.getCategory());
        assertEquals("1", dto.getTransactionReference());
    }

    @Test
    void testGetTransactions_FilterByCustomerId_ReturnsFilteredAndMapped() {
        Long customerId = 101L;
        Page<Transaction> mockPage = new PageImpl<>(List.of(mockTransaction), pageable, 1);

        when(transactionRepository.findAllByCustomerId(eq(customerId), eq(pageable))).thenReturn(mockPage);

        Page<TransactionResponseDto> result = transactionService.getTransactions(customerId, null, pageable);

        verify(transactionRepository, times(1)).findAllByCustomerId(eq(customerId), eq(pageable));
        verify(transactionRepository, never()).findAll(any(Pageable.class));

        assertFalse(result.getContent().isEmpty());
        assertEquals(customerId, result.getContent().get(0).getCustomerId());
    }

    @Test
    void testGetTransactions_FilterByCustomerAndCategory_ReturnsFilteredAndMapped() {
        Long customerId = 101L;
        String category = "Travel";
        Page<Transaction> mockPage = new PageImpl<>(List.of(mockTransaction), pageable, 1);

        when(transactionRepository.findAllByCustomerIdAndCategory(eq(customerId), eq(category), eq(pageable))).thenReturn(mockPage);

        Page<TransactionResponseDto> result = transactionService.getTransactions(customerId, category, pageable);

        verify(transactionRepository, times(1)).findAllByCustomerIdAndCategory(eq(customerId), eq(category), eq(pageable));

        assertFalse(result.getContent().isEmpty());
        assertEquals(customerId, result.getContent().get(0).getCustomerId());
        assertEquals(category, result.getContent().get(0).getCategory());
    }

    @Test
    void testGetCategoryTotals_ReturnsCorrectMap() {
        Long customerId = 101L;
        List<Object[]> mockTotals = List.of(
                new Object[]{"Groceries", new BigDecimal("150.00")},
                new Object[]{"Travel", new BigDecimal("500.50")}
        );
        when(transactionRepository.findTotalAmountPerCategoryForCustomer(customerId)).thenReturn(mockTotals);

        Map<String, Double> result = transactionService.getCategoryTotals(customerId);

        verify(transactionRepository, times(1)).findTotalAmountPerCategoryForCustomer(customerId);

        assertEquals(2, result.size());
        assertTrue(result.containsKey("Groceries"));
        assertEquals(150.00, result.get("Groceries"));
        assertTrue(result.containsKey("Travel"));
        assertEquals(500.50, result.get("Travel"));
    }
}
