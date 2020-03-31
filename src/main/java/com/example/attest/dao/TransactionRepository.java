package com.example.attest.dao;

import com.example.attest.model.domain.Transaction;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

	Optional<Transaction> findByReference(String reference);

}
