package com.example.attest.service;

import com.example.attest.model.api.TransactionApi;
import com.example.attest.model.api.TransactionStatusApiRequest;
import com.example.attest.model.api.TransactionStatusApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {

	TransactionApi findByReference(String reference);

	Page<TransactionApi> findByAccountIban(String accountIban, Pageable pageable);

	TransactionApi create(TransactionApi transactionApi);

	TransactionStatusApiResponse getTransactionStatus(TransactionStatusApiRequest transactionStatusApiRequest);

}
