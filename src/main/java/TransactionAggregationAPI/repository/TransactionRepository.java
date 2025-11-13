package TransactionAggregationAPI.repository;

import TransactionAggregationAPI.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllByCustomerIdAndCategory(Long customerId, String category, Pageable pageable);

    Page<Transaction> findAllByCustomerId(Long customerId, Pageable pageable);

    @Query("SELECT t.category, SUM(t.amount) " +
            "FROM Transaction t " +
            "WHERE t.customerId = :customerId " +
            "GROUP BY t.category")
    List<Object[]> findTotalAmountPerCategoryForCustomer(Long customerId);

}
