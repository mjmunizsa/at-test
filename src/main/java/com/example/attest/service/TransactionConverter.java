package com.example.attest.service;

import com.example.attest.model.api.TransactionApi;
import com.example.attest.model.domain.Transaction;
import java.util.List;

public interface TransactionConverter {

	public TransactionApi toApiModel(Transaction transaction, Class<TransactionApi> apiClass);

	public Transaction toModelApi(TransactionApi transactionApi, Class<Transaction> domainClass);


	List<TransactionApi> toApiList(List<Transaction> transactions);
}
