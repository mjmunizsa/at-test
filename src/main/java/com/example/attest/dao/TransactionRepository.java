package com.example.attest.dao;

import com.example.attest.model.domain.Transaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, String> {

	Optional<Transaction> findByReference(String reference);

	List<Transaction> findByAccountIban(String accountIban, Sort sort);

}
