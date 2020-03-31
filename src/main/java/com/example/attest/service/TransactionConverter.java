package com.example.attest.service;

import com.example.attest.model.api.TransactionApi;
import com.example.attest.model.domain.Transaction;
import java.util.List;
import org.springframework.data.domain.Page;

public interface TransactionConverter {

	TransactionApi toApiModel(Transaction transaction, Class<TransactionApi> apiClass);

	Transaction toModelApi(TransactionApi transactionApi, Class<Transaction> domainClass);

	Page<TransactionApi> toApiPage(Page<Transaction> transactionPage);

	List<TransactionApi> toApiList(List<Transaction> transactions);
}
