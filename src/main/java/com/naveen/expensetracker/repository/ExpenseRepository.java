package com.naveen.expensetracker.repository;

import com.naveen.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    // Puthusa add panna vendiya query:
    @Query("SELECT e FROM Expense e WHERE e.description LIKE %?1% OR e.category LIKE %?1%")
    List<Expense> search(String keyword);
    
}