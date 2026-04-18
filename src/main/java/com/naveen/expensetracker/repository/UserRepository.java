package com.naveen.expensetracker.repository;

import com.naveen.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Login pannum pothu Email & Password correct ah irukka nu check panna intha line thevai!
    User findByEmailAndPassword(String email, String password);
    
}