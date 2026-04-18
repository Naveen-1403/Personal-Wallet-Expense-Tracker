package com.naveen.expensetracker.controller;

import com.naveen.expensetracker.model.Expense;
import com.naveen.expensetracker.service.ExpenseService;
import jakarta.validation.Valid; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; 
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate; 
import java.util.List;
import java.util.Map; // <--- Import Map thevai

@Controller
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    // 1. Show Home Page (UPDATED FOR YEARLY TOTALS)
    @GetMapping("/")
    public String viewHomePage(Model model, 
                               @RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "month", required = false) String month,
                               @RequestParam(value = "year", required = false) Integer year) {
        
        // Step A: Current Date detection
        LocalDate today = LocalDate.now(); 

        if (month == null || month.isEmpty()) {
            month = String.format("%02d", today.getMonthValue()); 
        }

        if (year == null) {
            year = today.getYear();
        }

        // Step B: Monthly Data (Main Dashboard)
        List<Expense> listExpenses = expenseService.listAll(keyword, month, year);
        model.addAttribute("listExpenses", listExpenses);
        model.addAttribute("totalIncome", expenseService.getTotalIncome(listExpenses));
        model.addAttribute("totalExpense", expenseService.getTotalExpenseAmount(listExpenses));
        model.addAttribute("balance", expenseService.getBalance(listExpenses));
        model.addAttribute("chartData", expenseService.getCategoryWiseTotal(listExpenses));
        
        // ==========================================================
        // NEW: YEARLY REPORT (Monthly List + Yearly Grand Totals)
        // ==========================================================
        Map<String, Object> yearlyReport = expenseService.getYearlyReport(year);
        
        // Map-la irundhu ovvonaa eduthu Model-la podurom
        model.addAttribute("yearlySummary", yearlyReport.get("monthlyList"));
        model.addAttribute("yearlyTotalIncome", yearlyReport.get("yearlyTotalIncome"));
        model.addAttribute("yearlyTotalExpense", yearlyReport.get("yearlyTotalExpense"));
        model.addAttribute("yearlyTotalBalance", yearlyReport.get("yearlyTotalBalance"));
        
        // Pass UI Selections
        model.addAttribute("keyword", keyword); 
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);
        
        return "index"; 
    }

    // 2. Show form to add a new transaction
    @GetMapping("/showNewExpenseForm")
    public String showNewExpenseForm(Model model) {
        Expense expense = new Expense();
        model.addAttribute("expense", expense);
        return "new_expense"; 
    }

    // 3. Save the transaction to database
    @PostMapping("/saveExpense")
    public String saveExpense(@Valid @ModelAttribute("expense") Expense expense, BindingResult result) {
        if (result.hasErrors()) {
            return "new_expense"; 
        }
        expenseService.saveExpense(expense);
        return "redirect:/"; 
    }

    // 4. Show form to update/edit an existing transaction
    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(@PathVariable(value = "id") Long id, Model model) {
        Expense expense = expenseService.getExpenseById(id);
        model.addAttribute("expense", expense);
        return "update_expense"; 
    }

    // 5. Delete a transaction
    @GetMapping("/deleteExpense/{id}")
    public String deleteExpense(@PathVariable(value = "id") Long id) {
        expenseService.deleteExpenseById(id);
        return "redirect:/"; 
    }
}