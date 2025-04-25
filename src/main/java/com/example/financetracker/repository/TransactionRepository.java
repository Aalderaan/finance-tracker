package com.example.financetracker.repository;

import com.example.financetracker.dto.CategorySummaryDTO;
import com.example.financetracker.model.Transaction;
import com.example.financetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
           "WHERE t.user = :user AND t.type = :type AND t.timestamp BETWEEN :from AND :to")
    Double getTotalAmountByTypeAndDateRange(@Param("user") User user,
                                            @Param("type") String type,
                                            @Param("from") LocalDateTime from,
                                            @Param("to") LocalDateTime to);

    @Query("SELECT new com.example.financetracker.dto.CategorySummaryDTO(t.category, SUM(t.amount)) " +
           "FROM Transaction t " +
           "WHERE t.user = :user AND t.type = :type AND t.timestamp BETWEEN :from AND :to " +
           "GROUP BY t.category")
    List<CategorySummaryDTO> getAmountGroupedByCategory(@Param("user") User user,
                                                        @Param("type") String type,
                                                        @Param("from") LocalDateTime from,
                                                        @Param("to") LocalDateTime to);
}