package com.example.attest.dao;

import com.example.attest.model.domain.Transaction;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, String> {

	Optional<Transaction> findByReference(String reference);

	Page<Transaction> findByAccountIban(String accountIban, Pageable pageable);

}
