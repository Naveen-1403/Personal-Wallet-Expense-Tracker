package com.naveen.expensetracker.service;

import com.naveen.expensetracker.model.Expense;
import com.naveen.expensetracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    // 1. Get all expenses (With Search & Month Filter logic)
    public List<Expense> listAll(String keyword, String month, Integer year) {
        List<Expense> expenses = (keyword != null) ? expenseRepository.search(keyword) : expenseRepository.findAll();

        if (month != null && !month.isEmpty() && year != null) {
            String filterPrefix = year + "-" + month;
            return expenses.stream()
                .filter(e -> e.getDate() != null && e.getDate().toString().startsWith(filterPrefix))
                .collect(Collectors.toList());
        }
        return expenses;
    }

    // 2. Save/Update Transaction
    public void saveExpense(Expense expense) {
        expenseRepository.save(expense);
    }

    // 3. Get by ID
    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElse(null);
    }

    // 4. Delete Transaction
    public void deleteExpenseById(Long id) {
        expenseRepository.deleteById(id);
    }

    // 5. Total Income
    public Double getTotalIncome(List<Expense> expenses) {
        return expenses.stream()
                .filter(e -> "INCOME".equals(e.getTransactionType()))
                .mapToDouble(Expense::getAmount).sum();
    }

    // 6. Total Expense
    public Double getTotalExpenseAmount(List<Expense> expenses) {
        return expenses.stream()
                .filter(e -> "EXPENSE".equals(e.getTransactionType()))
                .mapToDouble(Expense::getAmount).sum();
    }

    // 7. Balance
    public Double getBalance(List<Expense> expenses) {
        return getTotalIncome(expenses) - getTotalExpenseAmount(expenses);
    }

    // 8. Category-wise Total for Pie Chart
    public Map<String, Double> getCategoryWiseTotal(List<Expense> expenses) {
        Map<String, Double> categoryMap = new HashMap<>();
        for (Expense expense : expenses) {
            if ("EXPENSE".equals(expense.getTransactionType()) || expense.getTransactionType() == null) {
                String category = expense.getCategory();
                Double amount = expense.getAmount();
                categoryMap.put(category, categoryMap.getOrDefault(category, 0.0) + amount);
            }
        }
        return categoryMap;
    }

    // ==========================================
    // UPDATED: YEARLY REPORT WITH GRAND TOTALS
    // ==========================================
    public Map<String, Object> getYearlyReport(Integer year) {
        List<Map<String, Object>> summaryList = new ArrayList<>();
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        double grandTotalIncome = 0;
        double grandTotalExpense = 0;

        for (int i = 1; i <= 12; i++) {
            String monthStr = String.format("%02d", i);
            List<Expense> monthlyData = listAll(null, monthStr, year);

            double mIncome = getTotalIncome(monthlyData);
            double mExpense = getTotalExpenseAmount(monthlyData);

            Map<String, Object> monthMap = new HashMap<>();
            monthMap.put("monthName", monthNames[i-1]);
            monthMap.put("income", mIncome);
            monthMap.put("expense", mExpense);
            monthMap.put("balance", mIncome - mExpense);
            
            summaryList.add(monthMap);

            // Yearly Total calculations
            grandTotalIncome += mIncome;
            grandTotalExpense += mExpense;
        }

        // Ellathaiyum oru single Map-la pottu anupurom
        Map<String, Object> finalResponse = new HashMap<>();
        finalResponse.put("monthlyList", summaryList);
        finalResponse.put("yearlyTotalIncome", grandTotalIncome);
        finalResponse.put("yearlyTotalExpense", grandTotalExpense);
        finalResponse.put("yearlyTotalBalance", grandTotalIncome - grandTotalExpense);

        return finalResponse;
    }
}