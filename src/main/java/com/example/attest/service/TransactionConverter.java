package com.example.attest.service;

import com.example.attest.model.api.TransactionApi;
import com.example.attest.model.domain.Transaction;

public interface TransactionConverter {

	public TransactionApi toApiModel(Transaction transaction, Class<TransactionApi> apiClass);

	public Transaction toModelApi(TransactionApi transactionApi, Class<Transaction> domainClass);


}
